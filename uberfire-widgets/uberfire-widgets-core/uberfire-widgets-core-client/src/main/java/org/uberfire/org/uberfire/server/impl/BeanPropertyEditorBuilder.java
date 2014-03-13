package org.uberfire.org.uberfire.server.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.client.propertyEditor.PropertyEditorException;
import org.uberfire.client.propertyEditor.api.PropertyEditorCategory;
import org.uberfire.client.propertyEditor.api.PropertyEditorFieldInfo;
import org.uberfire.client.propertyEditor.fields.PropertyEditorType;
import org.uberfire.shared.propertyEditor.BeanPropertyEditorBuilderService;

@Service
@Dependent
public class BeanPropertyEditorBuilder implements BeanPropertyEditorBuilderService {

    private Class<?> targetClass;

    @Override
    public Map<String, List<String>> extract( String fqcn ) {
        HashMap<String, List<String>> map = new HashMap<String, List<String>>();
        PropertyEditorCategory category = extractBeanInfo( fqcn );
        List<String> fields = new ArrayList<String>();
        for ( PropertyEditorFieldInfo field : category.getFields() ) {
            fields.add( field.getLabel() );
        }
        map.put( category.getName(), fields );
        return map;
    }

    @Override
    public PropertyEditorCategory extractCategories( String fqcn ) {
        return extractBeanInfo( fqcn );
    }

    private PropertyEditorCategory extractBeanInfo( String fqcn ) {
        PropertyEditorCategory beanCategory = new PropertyEditorCategory( fqcn );
        extractTargetClass( fqcn );
        extractFieldInformation( beanCategory );
        return beanCategory;
    }

    private void extractFieldInformation( PropertyEditorCategory beanCategory ) {
        final Field[] declaredFields = targetClass.getDeclaredFields();
        for ( Field field : declaredFields ) {
            beanCategory.withField(
                    new PropertyEditorFieldInfo( field.getName(), PropertyEditorType.TEXT ) );
        }
    }

    private void extractTargetClass( String fqcn ) {
        try {
            targetClass = Class.forName( fqcn );
        } catch ( ClassNotFoundException e ) {
            throw new PropertyEditorException( e.getMessage() );
        }
    }

}
