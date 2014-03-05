package org.uberfire.propertyEditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.*;

public class BeanInformationTest {

    @Test
    public void extractEmptyBeanInformation() {

        String FQCN = "org.uberfire.propertyEditor.SampleEmptyBean";
        BeanInformation expected = new BeanInformation( FQCN );
        expected.setFQCN( FQCN );
        BeanInformation actual = new BeanInformation( FQCN ).extract();

        assertEquals( expected.getFQCN(), actual.getFQCN() );
        assertTrue( actual.getFields().size() == 0 );
    }

    @Test
    public void extractPlanBeanInformation() {

        String FQCN = "org.uberfire.propertyEditor.SamplePlanBean";

        BeanInformation actual = new BeanInformation( FQCN ).extract();

        assertTrue( actual.getFields().size() == 2 );
        assertTrue( actual.getFields().get( 0 ).getName().equals( "teste1" ) );
        assertTrue( actual.getFields().get( 1 ).getName().equals( "teste2" ) );

        Map<String, String> toMap = new HashMap<String, String>();
        toMap.put( "teste1", "teste1" );
        toMap.put( "teste2", "teste2" );

        assertTrue( toMap.equals( actual.toMap() ) );
    }

}
