package org.uberfire.server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;
import org.uberfire.io.IOService;

public class FileUploadServlet
        extends BaseUploadServlet {

    private static final String PARAM_PATH = "path";
    private static final String PARAM_FOLDER = "folder";
    private static final String PARAM_FILENAME = "fileName";

    private static final String RESPONSE_OK = "OK";
    private static final String RESPONSE_FAIL = "FAIL";

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Override
    protected void doPost( HttpServletRequest request,
                           HttpServletResponse response ) throws ServletException, IOException {

        try {
            if ( request.getParameter( PARAM_PATH ) != null ) {
                writeFile( ioService,
                           ioService.get( new URI( request.getParameter( PARAM_PATH ) ) ),
                           getFileItem( request ) );

                writeResponse( response,
                               RESPONSE_OK );

            } else if ( request.getParameter( PARAM_FOLDER ) != null ) {

                //See https://bugzilla.redhat.com/show_bug.cgi?id=1091204
                //If the User-provided file name has an extension use that; otherwise use the same extension as the original (OS FileSystem) extension
                String targetFileName;
                final String originalFileName = getFileItem( request ).getName();
                final String providedFileName = request.getParameter( PARAM_FILENAME );
                if ( providedFileName.contains( "." ) ) {
                    targetFileName = providedFileName;
                } else {
                    targetFileName = providedFileName + getExtension( originalFileName );
                }

                writeFile( ioService,
                           ioService.get( new URI( request.getParameter( PARAM_FOLDER ) + "/" + targetFileName ) ),
                           getFileItem( request ) );

                writeResponse( response,
                               RESPONSE_OK );
            }

        } catch ( FileUploadException e ) {
            logError( e );
            writeResponse( response,
                           RESPONSE_FAIL );

        } catch ( URISyntaxException e ) {
            logError( e );
            writeResponse( response,
                           RESPONSE_FAIL );
        }
    }

    private String getExtension( final String originalFileName ) {
        if ( originalFileName.contains( "." ) ) {
            return "." + originalFileName.substring( originalFileName.lastIndexOf( "." ) + 1 );
        }
        return "";
    }

}
