//package org.uberfire.server.screens.property.api;
//
//import org.junit.Test;
//import org.uberfire.backend.server.impl.BeanPropertyEditorBuilder;
//import org.uberfire.shared.screen.property.fields.PropertyEditorType;
//import org.uberfire.shared.screens.property.api.PropertyEditorCategory;
//import org.uberfire.shared.screens.property.api.PropertyEditorFieldInfo;
//
//import static junit.framework.Assert.assertEquals;
//
//public class BeanPropertyEditorBuilderTest {
//
//    @Test
//    public void extractEmptyBeanInformation() {
//
//        String FQCN = "org.uberfire.server.screens.property.api.beans.SampleEmptyBean";
//
//        BeanPropertyEditorBuilder bean = new BeanPropertyEditorBuilder(  );
//
//        PropertyEditorCategory expected = new PropertyEditorCategory( FQCN );
//
//        PropertyEditorCategory actual = bean.extractBeanInfo( FQCN );
//        assertEquals( expected.getName(), actual.getName() );
//    }
//
//    @Test
//    public void extractPlanBeanInformation() {
//        String FQCN = "org.uberfire.server.screens.property.api.beans.SamplePlanBean";
//
//        BeanPropertyEditorBuilder bean = new BeanPropertyEditorBuilder(  );
//
//        PropertyEditorFieldInfo teste1 = new PropertyEditorFieldInfo( "teste1", PropertyEditorType.TEXT );
//        PropertyEditorFieldInfo teste2 = new PropertyEditorFieldInfo( "teste2", PropertyEditorType.TEXT );
//        PropertyEditorCategory expected = new PropertyEditorCategory( FQCN )
//                .withField( teste1 )
//                .withField( teste2 );
//
//        PropertyEditorCategory actual = bean.extractBeanInfo(FQCN);
//        assertEquals( expected.getName(), actual.getName() );
//        assertEquals(teste1.getKey(),actual.getFields().get( 0 ).getKey() );
//        assertEquals(teste2.getKey(),actual.getFields().get( 1 ).getKey() );
//
//    }
//
//}
