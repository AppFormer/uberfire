package org.uberfire.client.screens.property;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class PropertyEditorBuilder {

    public static Tree build( Map<String, PropertyEditorFieldInfo> propertyMap ) {

        Tree propertyTree = new Tree();
        Map<String, TreeItem> categories = new HashMap<String, TreeItem>();
        for ( String key : propertyMap.keySet() ) {
            PropertyEditorFieldInfo property = propertyMap.get( key );
            TreeItem category = findOrCreateCategory( property.getCategory(), categories, propertyTree );
            category.addItem( createFieldWidget( key, property ) );
        }
        return propertyTree;
    }

    private static TreeItem findOrCreateCategory( String categoryKey,
                                                  Map<String, TreeItem> categories,
                                                  Tree propertyTree ) {
        TreeItem category = categories.get( categoryKey );
        if ( category == null ) {
            category = new TreeItem( createCategoryWidget( categoryKey ) );
            categories.put( categoryKey, category );
            propertyTree.addItem( category );
        }
        return category;

    }

    static Widget createCategoryWidget( String categoryKey ) {
        Label label = new Label( categoryKey );
        return label;
    }

    public static Widget createFieldWidget( String field,
                                            PropertyEditorFieldInfo property ) {
        FlexTable t = new FlexTable();
        Label fieldLabel = new Label( field );
        Widget fieldWidget = property.getType().widget( property );
        t.setWidget( 0, 0, fieldLabel );
        t.setWidget( 0, 1, fieldWidget );
        return t;
    }
}