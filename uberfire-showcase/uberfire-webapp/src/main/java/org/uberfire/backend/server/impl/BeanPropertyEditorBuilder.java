//package org.uberfire.backend.server.impl;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import javax.enterprise.context.Dependent;
//
//import org.jboss.errai.bus.server.annotations.Service;
//
//
//@Service
//@Dependent
//public class BeanPropertyEditorBuilder implements BeanPropertyEditorBuilderService {
//
//    private Class<?> targetClass;
//
//    @Override
//    public Map<String, List<String>> extract( String fqcn ) {
//        HashMap<String, List<String>> map = new HashMap<String, List<String>>();
//        PropertyEditorCategory category = extractBeanInfo( fqcn );
//        List<String> fields = new ArrayList<String>();
//        for ( PropertyEditorFieldInfo field : category.getFields() ) {
//            fields.add( field.getKey() );
//        }
//        map.put( category.getName(), fields );
//        return map;
//    }
//
//    public PropertyEditorCategory extractBeanInfo( String fqcn ) {
//        PropertyEditorCategory beanCategory = new PropertyEditorCategory( fqcn );
//        extractTargetClass( fqcn );
//        extractFieldInformation( beanCategory );
//        return beanCategory;
//    }
//
//    private void extractFieldInformation( PropertyEditorCategory beanCategory ) {
//        final Field[] declaredFields = targetClass.getDeclaredFields();
//        for ( Field field : declaredFields ) {
//            beanCategory.withField(
//                    new PropertyEditorFieldInfo( field.getName(), PropertyEditorType.TEXT ) );
//        }
//    }
//
//    private void extractTargetClass( String fqcn ) {
//        try {
//            targetClass = Class.forName( fqcn );
//        } catch ( ClassNotFoundException e ) {
//            //ederignFIXME
//            e.printStackTrace();
//        }
//    }
//
//}
