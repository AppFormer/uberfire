/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.client.workbench.widgets.popup;

import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.event.HideEvent;
import com.github.gwtbootstrap.client.ui.event.HideHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Skeleton for popups
 */
public class PopupView
        extends Composite
        implements HasCloseHandlers<PopupView> {

    final Modal modal = new Modal( true, true );

    public PopupView() {
        final SimplePanel panel = new SimplePanel( modal );

        initWidget( panel );
    }

    public void setContent( final IsWidget widget ) {
        modal.add( widget );
    }

    public void setTitle( final String title ) {
        modal.setTitle( title );
    }

    public void show() {
        modal.show();
        modal.addHideHandler( new HideHandler() {
            @Override
            public void onHide( final HideEvent hideEvent ) {
                cleanup();
            }
        } );
    }

    public void hide() {
        modal.hide();
    }

    private void cleanup() {
        CloseEvent.fire( this, this, false );
    }

    @Override
    public HandlerRegistration addCloseHandler( final CloseHandler<PopupView> handler ) {
        return addHandler( handler, CloseEvent.getType() );
    }

}