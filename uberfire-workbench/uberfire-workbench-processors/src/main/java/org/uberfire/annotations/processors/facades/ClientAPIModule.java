package org.uberfire.annotations.processors.facades;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.annotations.processors.exceptions.GenerationException;

/**
 * A facade for uberfire-client-api.
 * Due to a bug in Eclipse annotation processor and inner projects dependencies,
 * this class handle with the dependencies of uberfire-client-api.
 */
public class ClientAPIModule {

    private static final Logger logger = LoggerFactory.getLogger( ClientAPIModule.class );

    public static final String IDENTIFIER = "identifier";
    public static final String IS_DEFAULT = "isDefault";
    public static final String IS_TEMPLATE = "isTemplate";
    public static final String VALUE = "value";
    private static Class<? extends Annotation> workbenchSplashScreen;
    private static Class<? extends Annotation> workbenchPerspective;
    private static Class<? extends Annotation> workbenchPopup;
    private static Class<? extends Annotation> workbenchScreen;
    private static Class<? extends Annotation> workbenchContext;
    private static Class<? extends Annotation> workbenchEditor;
    private static Class<? extends Annotation> defaultPosition;
    private static Class<? extends Annotation> workbenchPartTitle;
    private static Class<? extends Annotation> workbenchContextId;
    private static Class<? extends Annotation> workbenchPartTitleDecoration;
    private static Class<? extends Annotation> workbenchPartView;
    private static Class<? extends Annotation> workbenchMenu;
    private static Class<? extends Annotation> workbenchToolBar;
    private static Class<? extends Annotation> perspective;
    private static Class<? extends Annotation> splashFilter;
    private static Class<? extends Annotation> splashBodySize;
    private static Class<? extends Annotation> intercept;
    private static Class<? extends Annotation> ufPart;
    private static Class<? extends Annotation> ufParts;
    private static Class<? extends Annotation> ufPanel;

    private ClientAPIModule() {
    }

    static {

        try {
            workbenchSplashScreen = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.WorkbenchSplashScreen" );
            workbenchPerspective = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.WorkbenchPerspective" );
            workbenchPopup = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.WorkbenchPopup" );
            workbenchScreen = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.WorkbenchScreen" );
            workbenchContext = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.WorkbenchContext" );
            workbenchEditor = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.WorkbenchEditor" );
            defaultPosition = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.DefaultPosition" );
            workbenchPartTitle = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.WorkbenchPartTitle" );
            workbenchContextId = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.WorkbenchContextId" );
            workbenchPartTitleDecoration = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.WorkbenchPartTitleDecoration" );
            workbenchPartView = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.WorkbenchPartView" );
            workbenchMenu = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.WorkbenchMenu" );
            workbenchToolBar = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.WorkbenchToolBar" );
            perspective = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.Perspective" );
            splashFilter = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.SplashFilter" );
            splashBodySize = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.SplashBodySize" );
            intercept = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.Intercept" );
            ufPart = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.UFPart" );
            ufParts = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.UFParts" );
            ufPanel = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.UFPanel" );

        } catch ( ClassNotFoundException e ) {
            logger.error( e.getMessage() );
        }
    }

    public static Class<? extends Annotation> getWorkbenchScreenClass() {
        return workbenchScreen;
    }

    public static Class<? extends Annotation> getSplashFilterClass() {
        return splashFilter;
    }

    public static Class<? extends Annotation> getSplashBodySizeClass() {
        return splashBodySize;
    }

    public static Class<? extends Annotation> getInterceptClass() {
        return intercept;
    }

    public static Class<? extends Annotation> getPerspectiveClass() {
        return perspective;
    }

    public static Class<? extends Annotation> getWorkbenchToolBarClass() {
        return workbenchToolBar;
    }

    public static Class<? extends Annotation> getWorkbenchMenuClass() {
        return workbenchMenu;
    }

    public static Class<? extends Annotation> getWorkbenchPartViewClass() {
        return workbenchPartView;
    }

    public static Class<? extends Annotation> getWorkbenchPartTitleDecorationsClass() {
        return workbenchPartTitleDecoration;
    }

    public static Class<? extends Annotation> getWorkbenchContextIdClass() {
        return workbenchContextId;
    }

    public static Class<? extends Annotation> getWorkbenchPartTitleClass() {
        return workbenchPartTitle;
    }

    public static Class<? extends Annotation> getDefaultPositionClass() {
        return defaultPosition;
    }

    public static Class<? extends Annotation> getWorkbenchContextClass() {
        return workbenchContext;
    }

    public static Class<? extends Annotation> getWorkbenchEditorClass() {
        return workbenchEditor;
    }

    public static Class<? extends Annotation> getWorkbenchPopupClass() {
        return workbenchPopup;
    }

    public static Class<? extends Annotation> getWorkbenchSplashScreenClass() {
        return workbenchSplashScreen;
    }

    public static Class<? extends Annotation> getWorkbenchPerspectiveClass() {
        return workbenchPerspective;
    }

    public static Class<? extends Annotation> getUfPart() {
        return ufPart;
    }

    public static Class<? extends Annotation> getUfPanel() {
        return ufPanel;
    }

    private static String getAnnotationIdentifierValueOnClass( TypeElement o,
                                                               String className,
                                                               String annotationName ) throws GenerationException {
        try {
            String identifierValue = "";
            for ( final AnnotationMirror am : o.getAnnotationMirrors() ) {

                if ( className.equals( am.getAnnotationType().toString() ) ) {
                    for ( Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : am.getElementValues().entrySet() ) {
                        if ( annotationName.equals( entry.getKey().getSimpleName().toString() ) ) {
                            AnnotationValue value = entry.getValue();
                            identifierValue = value.getValue().toString();
                        }
                    }
                }
            }
            return identifierValue;
        } catch ( Exception e ) {
            throw new GenerationException( e.getMessage(), e.getCause() );
        }
    }

    public static Boolean getWbPerspectiveScreenIsDefaultValueOnClass( TypeElement classElement ) throws GenerationException {
        String bool = ( getAnnotationIdentifierValueOnClass( classElement, workbenchPerspective.getName(), IS_DEFAULT ) );
        return Boolean.valueOf( bool );
    }

    public static String getWbPerspectiveScreenIdentifierValueOnClass( TypeElement classElement ) throws GenerationException {
        return getAnnotationIdentifierValueOnClass( classElement, workbenchPerspective.getName(), IDENTIFIER );
    }

    public static String getWbPopupScreenIdentifierValueOnClass( TypeElement classElement ) throws GenerationException {
        return getAnnotationIdentifierValueOnClass( classElement, workbenchPopup.getName(), IDENTIFIER );
    }

    public static String getWbSplashScreenIdentifierValueOnClass( TypeElement classElement ) throws GenerationException {
        return getAnnotationIdentifierValueOnClass( classElement, workbenchSplashScreen.getName(), IDENTIFIER );
    }

    public static String getWbScreenIdentifierValueOnClass( TypeElement classElement ) throws GenerationException {
        return getAnnotationIdentifierValueOnClass( classElement, workbenchScreen.getName(), IDENTIFIER );
    }

    public static String getWbContextIdentifierValueOnClass( TypeElement classElement ) throws GenerationException {
        return getAnnotationIdentifierValueOnClass( classElement, workbenchContext.getName(), IDENTIFIER );
    }

    public static boolean getWbPerspectiveScreenIsTemplateValueOnClass( TypeElement classElement ) throws GenerationException {
        String bool = ( getAnnotationIdentifierValueOnClass( classElement, workbenchPerspective.getName(), IS_TEMPLATE ) );
        return Boolean.valueOf( bool );
    }

    public static String getWbPerspectiveScreenUFPanelOnClass( TypeElement classElement ) throws GenerationException {
        //ederign this should return more than one
        String ufPanelFieldName = "";
        for ( Element element : classElement.getEnclosedElements() ) {
            if ( element.getAnnotation( ufPanel ) != null ) {
                ufPanelFieldName = element.getSimpleName().toString();
                return ufPanelFieldName;
            }

        }
        throw new GenerationException( "Missing ufpart on perspective" );
    }

    public static List<String> getWbPerspectiveScreenUFPartsOnClass( TypeElement classElement ) throws GenerationException {
        List<String> parts = new ArrayList<String>();
        if ( thereIsUFParts( classElement ) ) {
            Annotation[] annotations = extractAnnotationsFromAnnotation( classElement, ufParts, VALUE );
            for ( Annotation annotation : annotations ) {
                String value = extractAnnotationStringValue( annotation );
                parts.add( value );
            }
        } else {
            parts.add( extractMethodValueFromAnnotation( classElement, ufPart, VALUE ) );
        }
        return parts;
    }

    private static String extractAnnotationStringValue( Annotation annotation ) throws GenerationException {
        String value;
        try {
            Class<? extends Annotation> aClass = annotation.annotationType();
            Method identifier = aClass.getDeclaredMethod( VALUE );
            value = String.valueOf( identifier.invoke( annotation ) );
        } catch ( Exception e ) {
            throw new GenerationException( e.getMessage(), e.getCause() );
        }
        return value;
    }

    private static boolean thereIsUFParts( TypeElement classElement ) {
        for ( Element element : classElement.getEnclosedElements() ) {
            if ( element.getAnnotation( ufParts ) != null ) {
                return true;
            }
        }
        return false;
    }

    public static String getWbPerspectiveScreenUFPanelTypeOnClass( TypeElement classElement ) throws GenerationException {
        return extractMethodValueFromAnnotation( classElement, ufPanel, VALUE );
    }

    private static Annotation[] extractAnnotationsFromAnnotation( TypeElement classElement,
                                                                  Class<? extends Annotation> annotation,
                                                                  String methodName ) throws GenerationException {
        Annotation[] annotations = { };
        for ( Element element : classElement.getEnclosedElements() ) {
            if ( element.getAnnotation( annotation ) != null ) {
                try {
                    Method identifier = annotation.getDeclaredMethod( methodName );
                    annotations = (Annotation[]) identifier.invoke( element.getAnnotation( annotation ) );
                } catch ( Exception e ) {
                    throw new GenerationException( e.getMessage(), e.getCause() );
                }
            }
        }
        return annotations;
    }

    private static String extractMethodValueFromAnnotation( TypeElement classElement,

                                                            Class<? extends Annotation> annotation,
                                                            String methodName ) throws GenerationException {
        String identifierValue = "";
        for ( Element element : classElement.getEnclosedElements() ) {
            if ( element.getAnnotation( annotation ) != null ) {
                identifierValue = getElementAnnotationStringValue( annotation, methodName, element );
            }
        }
        return identifierValue;
    }

    private static String getElementAnnotationStringValue( Class<? extends Annotation> annotation,
                                                           String methodName,
                                                           Element element ) throws GenerationException {
        String identifierValue;
        try {
            Method identifier = annotation.getDeclaredMethod( methodName );
            identifierValue = String.valueOf( identifier.invoke( element.getAnnotation( annotation ) ) );
        } catch ( Exception e ) {
            throw new GenerationException( e.getMessage(), e.getCause() );
        }
        return identifierValue;
    }

}
