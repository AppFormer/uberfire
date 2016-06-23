/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.plugin.client.perspective.editor.layout.editor.popups;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.shared.event.ModalHiddenEvent;
import org.gwtbootstrap3.client.shared.event.ModalHiddenHandler;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.uberfire.ext.layout.editor.client.components.ModalConfigurationContext;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent;
import org.uberfire.ext.plugin.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.ButtonPressed;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;
import org.uberfire.ext.widgets.common.client.htmleditor.HtmlEditorPresenter;

public class EditHTML
        extends BaseModal {

    private ModalConfigurationContext configContext;

    private HtmlEditorPresenter htmlEditor;

    private static final String DEFAULT_HTML = CommonConstants.INSTANCE.HTMLplaceHolder();

    @UiField
    Container container;

    public interface Listener {

        void onSave();

        void onClose();
    }

    interface Binder
            extends
            UiBinder<Widget, EditHTML> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    private ButtonPressed buttonPressed = ButtonPressed.CLOSE;

    public EditHTML( final ModalConfigurationContext ctx,
                     final HtmlEditorPresenter htmlEditor ) {
        this.configContext = ctx;
        this.htmlEditor = htmlEditor;

        setTitle( CommonConstants.INSTANCE.EditHtml() );
        add( new ModalBody() {{
            add( uiBinder.createAndBindUi( EditHTML.this ) );
        }} );
        setupHTMLEditor();

        add( new ModalFooterOKCancelButtons(
                new Command() {
                    @Override
                    public void execute() {
                        okButton();
                    }
                },
                new Command() {
                    @Override
                    public void execute() {
                        cancelButton();
                    }
                } )
           );

        addHiddenHandler();
    }

    protected KeyDownHandler getEnterDomHandler() {
        return new KeyDownHandler() {
            @Override
            public void onKeyDown( KeyDownEvent event ) {
                if ( !isInsideEditHTMLWidget( event ) && event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
                    if ( handleDefaultAction() ) {
                        event.preventDefault();
                        event.stopPropagation();
                    }
                }
            }

            private boolean isInsideEditHTMLWidget( KeyDownEvent event ) {
                return event.getSource() == EditHTML.this;
            }
        };
    }

    private void setupHTMLEditor() {
        setWidth( "1010px" );

        String html = configContext.getComponentProperty( HTMLLayoutDragComponent.HTML_CODE_PARAMETER );

        if ( html == null || html.isEmpty() ) {
            html =  DEFAULT_HTML;
        }

        htmlEditor.setContent( html );
        container.add( htmlEditor.getView() );
    }

    public void show() {
        super.show();
        htmlEditor.show();
    }

    public void hide() {
        super.hide();
    }

    protected void addHiddenHandler() {
        addHiddenHandler( new ModalHiddenHandler() {
            @Override
            public void onHidden( ModalHiddenEvent hiddenEvent ) {
                if ( userPressedCloseOrCancel() ) {
                    configContext.configurationCancelled();
                }
            }
        } );
    }

    private boolean userPressedCloseOrCancel() {
        return ButtonPressed.CANCEL.equals( buttonPressed ) || ButtonPressed.CLOSE.equals( buttonPressed );
    }

    void cancelButton() {
        buttonPressed = ButtonPressed.CANCEL;

        hide();
    }

    void okButton() {
        buttonPressed = ButtonPressed.OK;

        hide();
        configContext.setComponentProperty( HTMLLayoutDragComponent.HTML_CODE_PARAMETER, htmlEditor.getContent() );
        configContext.configurationFinished();
    }

    protected ModalConfigurationContext getConfigContext() {
        return this.configContext;
    }
}
