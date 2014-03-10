package org.uberfire.shared.screens.property.api;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

import static java.util.Collections.sort;

@Portable
public class PropertyEditorEvent {

    private String idEvent;
    private List<PropertyEditorCategory> properties = new ArrayList<PropertyEditorCategory>();

    public PropertyEditorEvent() {
    }

    ;

    public PropertyEditorEvent( String idEvent,
                                List<PropertyEditorCategory> properties ) {
        this.idEvent = idEvent;
        this.properties = properties;
    }

    public PropertyEditorEvent( String idEvent,
                                PropertyEditorCategory properties ) {
        this.idEvent = idEvent;
        this.properties.add( properties );
    }

    public List<PropertyEditorCategory> getProperties() {
        sortCategoriesAndFieldsByPriority( properties );
        return properties;
    }

    private static void sortCategoriesAndFieldsByPriority( List<PropertyEditorCategory> properties ) {
        sortCategoriesByPriority( properties );

        sortEditorFieldInfoByPriority( properties );

    }

    private static void sortCategoriesByPriority( List<PropertyEditorCategory> properties ) {
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


    public String getIdEvent() {
        return idEvent;
    }

}
