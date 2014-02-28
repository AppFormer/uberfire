package org.uberfire.annotations.processors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.uberfire.annotations.processors.exceptions.GenerationException;
import org.uberfire.annotations.processors.facades.ClientAPIModule;
import org.uberfire.annotations.processors.facades.WorkbenchPanelInformation;

public class TemplateInformationHelper {

    public static final String VALUE = "value";
    public static final String PANEL_TYPE = "panelType";
    public static final String IS_DEFAULT = "isDefault";

    public static TemplateInformation extractWbTemplatePerspectiveInformation( TypeElement classElement ) throws GenerationException {

        TemplateInformation template = new TemplateInformation();

        for ( Element element : classElement.getEnclosedElements() ) {
            if ( element.getAnnotation( ClientAPIModule.getWorkbenchPanel() ) != null ) {
                extractInformationFromWorkbenchPanel( template, element );
            }

        }
        if ( !template.thereIsTemplateFields() ) {
            return template;
        }
        throw new GenerationException( "The Template WorkbenchPerspective must provide a @WorkbenchPanel annotated field." );
    }

    private static void extractInformationFromWorkbenchPanel( TemplateInformation template,
                                                              Element element ) throws GenerationException {
        WorkbenchPanelInformation wbPanel = new WorkbenchPanelInformation();
        if ( workbenchPanelIsDefault( element ) ) {
            wbPanel.setDefault( true );
        }
        wbPanel.setFieldName( element.getSimpleName().toString() );
        wbPanel.setWbParts( getWorkbenchPartsFrom( element ) );
        wbPanel.setPanelType( extractPanelType( element ) );
        if ( wbPanel.isDefault() ) {
            if ( shouldHaveOnlyOneDefaultPanel( template ) ) {
                throw new GenerationException( "The Template WorkbenchPerspective must provide only one @WorkbenchPanel annotated field." );
            }
            template.setDefaultPanel( wbPanel );
        } else {
            template.addTemplateField( wbPanel );
        }
    }

    private static boolean shouldHaveOnlyOneDefaultPanel( TemplateInformation template ) {
        return template.getDefaultPanel() != null;
    }

    private static String extractPanelType( Element element ) throws GenerationException {
        Annotation annotation = element.getAnnotation( ClientAPIModule.getWorkbenchPanel() );
        return extractAnnotationPropertyValue( annotation, PANEL_TYPE );
    }

    private static boolean workbenchPanelIsDefault( Element element ) throws GenerationException {
        Annotation annotation = element.getAnnotation( ClientAPIModule.getWorkbenchPanel() );
        return Boolean.valueOf( extractAnnotationPropertyValue( annotation, IS_DEFAULT ) );
    }

    private static List<String> getWorkbenchPartsFrom( Element wbPanel ) throws GenerationException {
        List<String> parts = new ArrayList<String>();
        if ( thereIsWbParts( wbPanel ) ) {
            extractWbPartFromWbParts( wbPanel, parts );
        } else {
            parts.add( extractMethodValueFromAnnotation( wbPanel, ClientAPIModule.getWorkbenchPart(), VALUE ) );
        }
        return parts;
    }

    private static boolean thereIsWbParts( Element element ) {
        if ( element.getAnnotation( ClientAPIModule.getWorkbenchParts() ) != null ) {
            return true;
        }
        return false;
    }

    private static String extractAnnotationPropertyValue( Annotation annotation,
                                                          String annotationProperty ) throws GenerationException {
        String value;
        try {
            Class<? extends Annotation> aClass = annotation.annotationType();
            Method identifier = aClass.getDeclaredMethod( annotationProperty );
            value = String.valueOf( identifier.invoke( annotation ) );
        } catch ( Exception e ) {
            throw new GenerationException( e.getMessage(), e.getCause() );
        }
        return value;
    }

    private static void extractWbPartFromWbParts( Element ufPanel,
                                                  List<String> parts ) throws GenerationException {
        Annotation[] annotations = extractAnnotationsFromAnnotation( ufPanel, ClientAPIModule.getWorkbenchParts(), VALUE );
        for ( Annotation annotation : annotations ) {
            String value = extractAnnotationStringValue( annotation );
            parts.add( value );
        }
    }

    private static String extractMethodValueFromAnnotation( Element element,

                                                            Class<? extends Annotation> annotation,
                                                            String methodName ) throws GenerationException {
        String identifierValue = "";
        if ( element.getAnnotation( annotation ) != null ) {
            identifierValue = getElementAnnotationStringValue( annotation, methodName, element );
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

    private static String extractAnnotationStringValue( Annotation annotation ) throws GenerationException {
        return extractAnnotationPropertyValue( annotation, VALUE );
    }

    private static Annotation[] extractAnnotationsFromAnnotation( Element element,
                                                                  Class<? extends Annotation> annotation,
                                                                  String methodName ) throws GenerationException {
        Annotation[] annotations = { };

        if ( element.getAnnotation( annotation ) != null ) {
            try {
                Method identifier = annotation.getDeclaredMethod( methodName );
                annotations = (Annotation[]) identifier.invoke( element.getAnnotation( annotation ) );
            } catch ( Exception e ) {
                throw new GenerationException( e.getMessage(), e.getCause() );
            }
        }
        return annotations;
    }

}
