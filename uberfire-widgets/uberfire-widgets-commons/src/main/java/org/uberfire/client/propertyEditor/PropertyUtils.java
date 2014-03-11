package org.uberfire.client.propertyEditor;

import java.util.List;
import java.util.Map;

import org.uberfire.client.propertyEditor.api.PropertyEditorCategory;
import org.uberfire.client.propertyEditor.api.PropertyEditorFieldInfo;
import org.uberfire.client.propertyEditor.fields.PropertyEditorType;

public class PropertyUtils {

    public static PropertyEditorCategory mapToCategory( Map<String, List<String>> map ) {
        if ( !map.keySet().isEmpty() ) {
            String categoryName = map.keySet().iterator().next();
            PropertyEditorCategory category = new PropertyEditorCategory( categoryName );
            List<String> fields = map.get( categoryName );
            for ( String field : fields ) {
                category.withField( new PropertyEditorFieldInfo( field, PropertyEditorType.TEXT ) );
            }

            return category;
        }
        return null;
    }

}
