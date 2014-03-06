package org.uberfire.client.screens.property;

import java.util.HashMap;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.shared.PropertyEditorService;

@Dependent
@Templated
@WorkbenchScreen(identifier = "PropertyEditorScreen")
public class PropertyEditorScreen extends Composite {

    @DataField
    @Inject
    private TextBox searchItem;

    @DataField
    @Inject
    private Tree propertyTree;

//    @Inject
//    private Caller<PropertyEditorService> propertyEditor;

    @Override
    @WorkbenchPartTitle
    public String getTitle() {
        return "Property Editor";
    }

    @EventHandler("searchItem")
    private void onKeyDown( KeyDownEvent event ) {

//        final String text = searchItem.getText();
        final String text = "org.uberfire.propertyEditor.SamplePlanBean";
        if ( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
            /*
            TreeItem outerRoot = new TreeItem("Item 1");
            outerRoot.addItem("Item 1-1");
            outerRoot.addItem("Item 1-2");
            outerRoot.addItem("Item 1-3");
            outerRoot.addItem(new CheckBox("Item 1-4"));
            propertyTree.addItem(outerRoot);

            TreeItem innerRoot = new TreeItem("Item 1-5");
            innerRoot.addItem("Item 1-5-1");
            innerRoot.addItem("Item 1-5-2");
            innerRoot.addItem("Item 1-5-3");
            innerRoot.addItem("Item 1-5-4");
            innerRoot.addItem(new CheckBox("Item 1-5-5"));

            outerRoot.addItem(innerRoot);
            */
//            propertyEditor.call( new RemoteCallback<Map<String, String>>() {
//                @Override
//                public void callback( final Map<String, String> response ) {
//                    Label label = new Label(text + "{" +response.keySet().size() +"}");
//                    TreeItem outerRoot = new TreeItem(label);
//                    for ( String key : response.keySet() ) {
//                        Label labelInner = new Label(key+" "+response.get( key ));
//                        TreeItem tree = new TreeItem( labelInner);
//                        outerRoot.addItem(tree);
//                    }
//                    propertyTree.addItem( outerRoot );
//                }
//            } ).getInformation( text );
            PropertyEditorBuilder.build( propertyTree, new HashMap() );
        }
        searchItem.setText( "" );
    }

}
