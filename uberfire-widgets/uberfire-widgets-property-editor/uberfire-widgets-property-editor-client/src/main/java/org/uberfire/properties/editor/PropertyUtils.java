package org.uberfire.properties.editor;

import java.util.List;
import java.util.Map;

import org.uberfire.properties.editor.temp.fields.PropertyEditorFieldType;
import org.uberfire.properties.editor.temp.model.PropertyEditorCategory;
import org.uberfire.properties.editor.temp.model.PropertyEditorFieldInfo;

public class PropertyUtils {

    public static PropertyEditorCategory convertMapToCategory( Map<String, List<String>> map ) {
        if ( map != null && !map.keySet().isEmpty() ) {
            String categoryName = map.keySet().iterator().next();
            PropertyEditorCategory category = new PropertyEditorCategory( categoryName );
            List<String> fields = map.get( categoryName );
            for ( String field : fields ) {
                category.withField( new PropertyEditorFieldInfo( field, PropertyEditorFieldType.TEXT ) );
            }

            return category;
        }
        return null;
    }

}
