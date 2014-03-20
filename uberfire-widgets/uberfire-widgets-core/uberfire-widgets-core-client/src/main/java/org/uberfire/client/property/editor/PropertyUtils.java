package org.uberfire.client.property.editor;

import java.util.List;
import java.util.Map;

import org.uberfire.client.property.editor.api.PropertyEditorCategory;
import org.uberfire.client.property.editor.api.PropertyEditorFieldInfo;
import org.uberfire.client.property.editor.api.fields.PropertyEditorType;

public class PropertyUtils {

    public static PropertyEditorCategory convertMapToCategory( Map<String, List<String>> map ) {
        if ( map != null && !map.keySet().isEmpty() ) {
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
