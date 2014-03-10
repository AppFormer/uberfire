package org.uberfire.shared.screens.property.api;

import java.util.Comparator;
import java.util.List;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

import static java.util.Collections.sort;

public class PropertyEditorBuilder {

    public static Tree build( PropertyEditorEvent event  ) {
        List<PropertyEditorCategory> properties = event.getProperties();
        sortCategoriesAndFieldsByPriority( properties );

        Tree propertyTree = new Tree();

        for ( PropertyEditorCategory category : properties ) {
            TreeItem treeItemCategory = createTreeItemCategory( category, propertyTree );
            createCategoryFields( category, treeItemCategory );
        }

        return propertyTree;

    }

    private static void createCategoryFields( PropertyEditorCategory category,
                                              TreeItem treeItemCategory ) {
        for ( PropertyEditorFieldInfo field : category.getFields() ) {
            treeItemCategory.addItem( createFieldWidget( field ) );
        }
    }

    private static void sortCategoriesAndFieldsByPriority( List<PropertyEditorCategory> properties ) {
        sort( properties, new Comparator<PropertyEditorCategory>() {
            @Override
            public int compare( final PropertyEditorCategory o1,
                                final PropertyEditorCategory o2 ) {

                if ( o1.getPriority() < o2.getPriority() ) {
                    return -1;
                } else if ( o1.getPriority() > o2.getPriority() ) {
                    return 1;
                } else {
                    return 0;
                }
            }
        } );

        sortEditorFieldInfoByPriority( properties );

    }

    private static void sortEditorFieldInfoByPriority( List<PropertyEditorCategory> properties ) {
        for ( PropertyEditorCategory category : properties ) {
            sort( category.getFields(), new Comparator<PropertyEditorFieldInfo>() {
                @Override
                public int compare( final PropertyEditorFieldInfo o1,
                                    final PropertyEditorFieldInfo o2 ) {

                    if ( o1.getPriority() < o2.getPriority() ) {
                        return -1;
                    } else if ( o1.getPriority() > o2.getPriority() ) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            } );
        }

    }

    private static TreeItem createTreeItemCategory( PropertyEditorCategory propertyEditorCategory,
                                                    Tree propertyTree ) {
        TreeItem category = new TreeItem( createCategoryWidget( propertyEditorCategory.getName() ) );
        propertyTree.addItem( category );
        return category;
    }

    static Widget createCategoryWidget( String categoryKey ) {
        Label label = new Label( categoryKey );
        return label;
    }

    public static Widget createFieldWidget( PropertyEditorFieldInfo property ) {
        FlexTable t = new FlexTable();
        Label fieldLabel = new Label( property.getKey() );
        Widget fieldWidget = property.getType().widget( property );
        t.setWidget( 0, 0, fieldLabel );
        t.setWidget( 0, 1, fieldWidget );

        t.getCellFormatter().setWidth( 0, 0, "50%" );
        t.getCellFormatter().setWidth( 0, 1, "50%" );
        return t;
    }
}