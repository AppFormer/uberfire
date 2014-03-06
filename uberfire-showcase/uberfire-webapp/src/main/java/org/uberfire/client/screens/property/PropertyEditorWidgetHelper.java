package org.uberfire.client.screens.property;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PropertyEditorWidgetHelper {

    static Widget createCategoryWidget( String categoryKey ) {
        Label label = new Label( categoryKey );
        return label;
    }

    public static Widget createFieldWidget( String field,
                                            PropertyEditorFieldInfo property ) {
        Grid g = new Grid( 1, 3 );
        Label label = new Label( field );
        Label separador = new Label( " : " );

        Widget editableField;
        switch ( property.getType() ) {
            case BOOLEAN:
                editableField = new CheckBox();
                break;
            default:
                editableField = new TextBox();

        }
        g.setWidget( 0, 0, label );
        g.setWidget( 0, 1, separador );
        g.setWidget( 0, 2, editableField );
        return g;
    }
}
