package org.uberfire.client.propertyEditor.fields;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.propertyEditor.api.PropertyEditorCategory;
import org.uberfire.client.propertyEditor.api.PropertyEditorFieldInfo;

public abstract class AbstractField {

    public abstract Widget widget( PropertyEditorFieldInfo property );

}
