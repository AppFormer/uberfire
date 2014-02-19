package org.uberfire.annotations.processors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.uberfire.annotations.processors.exceptions.GenerationException;
import org.uberfire.annotations.processors.facades.ClientAPIModule;
import org.uberfire.annotations.processors.facades.UFPanelInformation;

public class TemplateInformationHelper {

    public static final String VALUE = "value";
    public static final String PANEL_TYPE = "panelType";
    public static final String IS_DEFAULT = "isDefault";

    public static TemplateInformation extractWbTemplatePerspectiveInformation( TypeElement classElement ) throws GenerationException {

        TemplateInformation template = new TemplateInformation();

        for ( Element element : classElement.getEnclosedElements() ) {
            if ( element.getAnnotation( ClientAPIModule.getUfPanel() ) != null ) {
                extractInformationFromUFPanel( template, element );
            }

        }
        if ( !template.getTemplateFields().isEmpty() ) {
            return template;
        }
        throw new GenerationException( "Missing ufpart on perspective" );
    }

    private static void extractInformationFromUFPanel( TemplateInformation template,
                                                       Element element ) throws GenerationException {
        UFPanelInformation ufPanel = new UFPanelInformation();
        if ( ufPanelIsDefault( element ) ) {
            ufPanel.setDefault( true );
        }
        ufPanel.setFieldName( element.getSimpleName().toString() );
        ufPanel.setUFParts( getUFPartsFrom( element ) );
        ufPanel.setPanelType( extractPanelType( element ) );
        if (ufPanel.isDefault()){
            template.setDefaultPanel(ufPanel);
        }
        else{
            template.addTemplateField( ufPanel );
        }
    }

    private static String extractPanelType( Element element ) throws GenerationException {
        Annotation annotation = element.getAnnotation( ClientAPIModule.getUfPanel() );
        return extractAnnotationPropertyValue( annotation, PANEL_TYPE );
    }

    private static boolean ufPanelIsDefault( Element element ) throws GenerationException {
        Annotation annotation = element.getAnnotation( ClientAPIModule.getUfPanel() );
        return Boolean.valueOf( extractAnnotationPropertyValue( annotation, IS_DEFAULT ) );
    }

    private static List<String> getUFPartsFrom( Element ufPanel ) throws GenerationException {
        List<String> parts = new ArrayList<String>();
        if ( thereIsUFParts( ufPanel ) ) {
            extractUFPartFromUFParts( ufPanel, parts );
        } else {
            parts.add( extractMethodValueFromAnnotation( ufPanel, ClientAPIModule.getUfPart(), VALUE ) );
        }
        return parts;
    }

    private static boolean thereIsUFParts( Element element ) {
        if ( element.getAnnotation( ClientAPIModule.getUfParts() ) != null ) {
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

    private static void extractUFPartFromUFParts( Element ufPanel,
                                                  List<String> parts ) throws GenerationException {
        Annotation[] annotations = extractAnnotationsFromAnnotation( ufPanel, ClientAPIModule.getUfParts(), VALUE );
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
