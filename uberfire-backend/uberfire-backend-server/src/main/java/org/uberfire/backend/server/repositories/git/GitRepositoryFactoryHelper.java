package org.uberfire.backend.server.repositories.git;

import java.net.URI;
import java.util.HashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.impl.git.GitRepository;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigItem;
import org.uberfire.backend.server.config.PasswordService;
import org.uberfire.backend.server.config.SecureConfigItem;
import org.uberfire.backend.server.repositories.EnvironmentParameters;
import org.uberfire.backend.server.repositories.RepositoryFactoryHelper;
import org.uberfire.backend.server.util.Paths;

import static org.uberfire.commons.validation.Preconditions.*;
import static org.uberfire.backend.repositories.impl.git.GitRepository.*;

@ApplicationScoped
public class GitRepositoryFactoryHelper implements RepositoryFactoryHelper {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    @Inject
    private PasswordService secureService;

    @Override
    public boolean accept( final ConfigGroup repoConfig ) {
        checkNotNull( "repoConfig", repoConfig );
        final ConfigItem<String> schemeConfigItem = repoConfig.getConfigItem( EnvironmentParameters.SCHEME );
        checkNotNull( "schemeConfigItem", schemeConfigItem );
        return SCHEME.equals( schemeConfigItem.getValue() );
    }

    @Override
    public Repository newRepository( final ConfigGroup repoConfig ) {
        checkNotNull( "repoConfig", repoConfig );
        final ConfigItem<String> schemeConfigItem = repoConfig.getConfigItem( EnvironmentParameters.SCHEME );
        checkNotNull( "schemeConfigItem", schemeConfigItem );

        final GitRepository repo = new GitRepository( repoConfig.getName() );

        for ( final ConfigItem item : repoConfig.getItems() ) {
            if ( item instanceof SecureConfigItem ) {
                repo.addEnvironmentParameter( item.getName(), secureService.decrypt( item.getValue().toString() ) );
            } else {
                repo.addEnvironmentParameter( item.getName(), item.getValue() );
            }
        }

        if ( !repo.isValid() ) {
            throw new IllegalStateException( "Repository " + repoConfig.getName() + " not valid" );
        }

        FileSystem fs = null;
        URI uri = null;
        try {
            uri = URI.create( repo.getUri() );
            fs = ioService.newFileSystem( uri, new HashMap<String, Object>( repo.getEnvironment() ) {{
                if ( !repo.getEnvironment().containsKey( "origin" ) ) {
                    put( "init", true );
                }
            }} );
        } catch ( final FileSystemAlreadyExistsException e ) {
            fs = ioService.getFileSystem( uri );
        } catch ( final Throwable ex ) {
            throw new RuntimeException( ex.getCause().getMessage() );
        }

        Path defaultRoot = fs.getRootDirectories().iterator().next();
        for ( final Path path : fs.getRootDirectories() ) {
            if ( path.toUri().toString().contains( "/master@" ) ) {
                defaultRoot = path;
                break;
            }
        }

        repo.setRoot( paths.convert( defaultRoot ) );
        repo.setPublicUri( fs.toString() );

        return repo;
    }
}
