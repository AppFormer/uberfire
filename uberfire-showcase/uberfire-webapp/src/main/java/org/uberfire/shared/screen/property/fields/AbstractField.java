package org.uberfire.shared.screen.property.fields;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.shared.screens.property.api.PropertyEditorFieldInfo;

public abstract class AbstractField {

    public abstract Widget widget( PropertyEditorFieldInfo property );

}
