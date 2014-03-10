//package org.uberfire.propertyEditor;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class OldBeanInformation {
//
//    private String FQCN;
//
//    private Class<?> targetClass;
//
//    private List<FieldInformation> fields = new ArrayList<FieldInformation>(  );
//
//    public OldBeanInformation( String fqcn ) {
//        this.FQCN = fqcn;
//
//    }
//
//    public OldBeanInformation extract( ) {
//        extractTargetClass();
//        extractFieldInformation();
//        return this;
//    }
//
//    private void extractFieldInformation() {
//        final Field[] declaredFields = targetClass.getDeclaredFields();
//        for ( Field field: declaredFields ){
//            fields.add( new FieldInformation(field.getName()));
//        }
//    }
//
//    private  void extractTargetClass() {
//        try {
//            targetClass = Class.forName( FQCN );
//        } catch ( ClassNotFoundException e ) {
//            //ederignFIXME
//            e.printStackTrace();
//        }
//    }
//
//    void setFQCN( String FQCN ) {
//        this.FQCN = FQCN;
//    }
//
//    String getFQCN() {
//        return FQCN;
//    }
//
//    List<FieldInformation> getFields() {
//        return fields;
//    }
//
//    public Map<String, String> toMap() {
//        Map<String,String> map = new HashMap<String, String>(  );
//        for ( FieldInformation field: fields ){
//            map.put( field.getName(), field.getName() );
//        }
//        return map;
//    }
//}
