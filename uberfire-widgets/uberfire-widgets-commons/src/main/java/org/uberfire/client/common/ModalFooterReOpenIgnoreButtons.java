/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.common;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.ModalFooter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.mvp.Command;

import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 * A Modal Footer with OK and Cancel buttons
 */
public class ModalFooterReOpenIgnoreButtons extends ModalFooter {

    private static ModalFooterReOpenIgnoreButtonsBinder uiBinder = GWT.create( ModalFooterReOpenIgnoreButtonsBinder.class );

    private final Command actionCommand;
    private final Command ignoreCommand;
    private final Modal panel;

    interface ModalFooterReOpenIgnoreButtonsBinder
            extends
            UiBinder<Widget, ModalFooterReOpenIgnoreButtons> {

    }

    @UiField
    Button actionButton;

    @UiField
    Button ignoreButton;

    public ModalFooterReOpenIgnoreButtons( final Modal panel,
                                           final Command actionCommand,
                                           final Command ignoreCommand,
                                           final String buttonText ) {
        this.actionCommand = checkNotNull( "actionCommand", actionCommand );
        this.ignoreCommand = checkNotNull( "ignoreCommand", ignoreCommand );
        this.panel = checkNotNull( "panel", panel );
        add( uiBinder.createAndBindUi( this ) );
        this.actionButton.setText( buttonText );
    }

    @UiHandler("actionButton")
    public void onActionButtonClick( final ClickEvent e ) {
        if ( actionCommand != null ) {
            actionCommand.execute();
        }
        panel.hide();
    }

    @UiHandler("ignoreButton")
    public void onIgnoreButtonClick( final ClickEvent e ) {
        if ( ignoreCommand != null ) {
            ignoreCommand.execute();
        }
        panel.hide();
    }

}
