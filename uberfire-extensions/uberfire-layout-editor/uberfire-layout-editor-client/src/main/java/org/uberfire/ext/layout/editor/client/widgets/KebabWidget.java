package org.uberfire.ext.layout.editor.client.widgets;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class KebabWidget extends Composite {

    @Inject
    @DataField
    private Anchor remove;

    @Inject
    @DataField
    private Anchor edit;

    @Inject
    @DataField( "le-kebab" )
    private Div leKebab;

    private Command editCommand;
    private Command removeCommand;

    public void init( Command remove,
                      Command edit ) {

        this.removeCommand = remove;
        this.editCommand = edit;

    }

    @SinkNative( Event.ONCLICK )
    @EventHandler( "remove" )
    public void removeClick( Event e ) {
        removeCommand.execute();
    }

    @SinkNative( Event.ONCLICK )
    @EventHandler( "edit" )
    public void editClick( Event e ) {
        editCommand.execute();
    }


}
