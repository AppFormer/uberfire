package org.uberfire.client.screens.property;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class PropertyEditorBuilder {

    public static void build( Tree propertyTree,
                              Map map ) {

        Map<String, PropertyEditorFieldInfo> propertyMap = getMap();

        Map<String, TreeItem> categories = new HashMap<String, TreeItem>();
        for ( String key : propertyMap.keySet() ) {
            PropertyEditorFieldInfo property = propertyMap.get( key );
            TreeItem category = findOrCreateCategory( property.getCategory(), categories, propertyTree );
            category.addItem( PropertyEditorWidgetHelper.createFieldWidget(key, property) );
        }

    }

    private static TreeItem findOrCreateCategory( String categoryKey,

                                                  Map<String, TreeItem> categories,
                                                  Tree propertyTree ) {
        TreeItem category = categories.get( categoryKey );
        if ( category == null ) {
            category = new TreeItem( PropertyEditorWidgetHelper.createCategoryWidget( categoryKey ) );
            categories.put( categoryKey, category );
            propertyTree.addItem( category );
        }
        return category;

    }



    private static Map<String, PropertyEditorFieldInfo> getMap() {

        Map<String, PropertyEditorFieldInfo> propertyMap = new HashMap<String, PropertyEditorFieldInfo>();
        propertyMap.put( "Name", new PropertyEditorFieldInfo( "Monitor", PropertyEditorType.TEXT ) );
        propertyMap.put( "Show in Favorites", new PropertyEditorFieldInfo( "Monitor", PropertyEditorType.BOOLEAN ) );
        propertyMap.put( "Link to Summary Report", new PropertyEditorFieldInfo( "Monitor", PropertyEditorType.HTTP_LINK ) );
        propertyMap.put( "Notes (to be show in reports)", new PropertyEditorFieldInfo( "Monitor", PropertyEditorType.TEXT ) );
        propertyMap.put( "Regular Reporting", new PropertyEditorFieldInfo( "Monitor", PropertyEditorType.OBJECT ) );

        propertyMap.put( "This monitor dependes on availability of", new PropertyEditorFieldInfo( "Dependency and Polling Interval", PropertyEditorType.TEXT ) );
        propertyMap.put( "polling interval, seconds", new PropertyEditorFieldInfo( "Dependency and Polling Interval", PropertyEditorType.NUMBER ) );

        propertyMap.put( "URL", new PropertyEditorFieldInfo( "Monitor Definition", PropertyEditorType.HTTP_LINK ) );
        propertyMap.put( "Port", new PropertyEditorFieldInfo( "Monitor Definition", PropertyEditorType.NUMBER ) );
        propertyMap.put( "Request Method", new PropertyEditorFieldInfo( "Monitor Definition", PropertyEditorType.COMBO ) );
        propertyMap.put( "POST Data", new PropertyEditorFieldInfo( "Monitor Definition", PropertyEditorType.TEXT ) );
        propertyMap.put( "Accepted Response Codes", new PropertyEditorFieldInfo( "Monitor Definition", PropertyEditorType.TEXT ) );
        propertyMap.put( "Response Validation", new PropertyEditorFieldInfo( "Monitor Definition", PropertyEditorType.COMBO ) );
        propertyMap.put( "String to Match", new PropertyEditorFieldInfo( "Monitor Definition", PropertyEditorType.TEXT ) );
        propertyMap.put( "Login", new PropertyEditorFieldInfo( "Monitor Definition", PropertyEditorType.TEXT ) );
        propertyMap.put( "Password", new PropertyEditorFieldInfo( "Monitor Definition", PropertyEditorType.SECRET_TEXT ) );
        propertyMap.put( "Proxy Server", new PropertyEditorFieldInfo( "Monitor Definition", PropertyEditorType.COMBO ) );
        propertyMap.put( "Proxy URL", new PropertyEditorFieldInfo( "Monitor Definition", PropertyEditorType.HTTP_LINK ) );

        propertyMap.put( "Action Profile", new PropertyEditorFieldInfo( "Action Profile (Alerting)", PropertyEditorType.TEXT ) );

        propertyMap.put( "Down State Timeout, seconds", new PropertyEditorFieldInfo( "Availability Monitoring", PropertyEditorType.NUMBER ) );
        propertyMap.put( "Number of Repetitions of Timeout for Down", new PropertyEditorFieldInfo( "Availability Monitoring", PropertyEditorType.NUMBER ) );
        propertyMap.put( "Extended Down Latency, seconds", new PropertyEditorFieldInfo( "Availability Monitoring", PropertyEditorType.NUMBER ) );

        return propertyMap;
    }
}
