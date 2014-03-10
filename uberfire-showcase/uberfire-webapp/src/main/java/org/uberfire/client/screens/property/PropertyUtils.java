package org.uberfire.client.screens.property;

import java.util.List;
import java.util.Map;

import org.uberfire.shared.screen.property.fields.PropertyEditorType;
import org.uberfire.shared.screens.property.api.PropertyEditorCategory;
import org.uberfire.shared.screens.property.api.PropertyEditorFieldInfo;

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
