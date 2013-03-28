/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.editors.repository.create;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.backend.FileExplorerRootService;
import org.uberfire.backend.Root;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.vfs.FileSystem;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.shared.mvp.impl.PathPlaceRequest;

import static org.uberfire.backend.vfs.PathFactory.*;

@Dependent
public class CreateRepositoryForm
        extends Composite
        implements HasCloseHandlers<CreateRepositoryForm> {

    interface CreateRepositoryFormBinder
            extends
            UiBinder<Widget, CreateRepositoryForm> {

    }

    private static CreateRepositoryFormBinder uiBinder = GWT.create( CreateRepositoryFormBinder.class );

    @Inject
    private Caller<VFSService> vfsService;

    @Inject
    private Caller<FileExplorerRootService> rootService;

    @Inject
    private Caller<RepositoryService> repositoryService;

    @Inject
    private Event<Root> event;

    @UiField
    ControlGroup nameGroup;

    @UiField
    TextBox nameTextBox;

    @UiField
    HelpInline nameHelpInline;

    @UiField
    TextBox usernameTextBox;

    @UiField
    PasswordTextBox passwordTextBox;

    @UiField
    Modal popup;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
        popup.setDynamicSafe( true );
        nameTextBox.addKeyPressHandler( new KeyPressHandler() {
            @Override
            public void onKeyPress( final KeyPressEvent event ) {
                nameGroup.setType( ControlGroupType.NONE );
                nameHelpInline.setText( "" );
            }
        } );
    }

    @Override
    public HandlerRegistration addCloseHandler( final CloseHandler<CreateRepositoryForm> handler ) {
        return addHandler( handler, CloseEvent.getType() );
    }

    @UiHandler("create")
    public void onCreateClick( final ClickEvent e ) {

        boolean hasError = false;
        if ( nameTextBox.getText() == null || nameTextBox.getText().trim().isEmpty() ) {
            nameGroup.setType( ControlGroupType.ERROR );
            nameHelpInline.setText( "Repository Name is mandatory" );
            hasError = true;
        } else {
            nameGroup.setType( ControlGroupType.NONE );
        }

        if ( hasError ) {
            return;
        }

        final String scheme = "git";
        final String alias = nameTextBox.getText();
        final String username = usernameTextBox.getText();
        final String password = passwordTextBox.getText();
        final Map<String, Object> env = new HashMap<String, Object>( 3 );
        env.put( "username", username );
        env.put( "password", password );
        env.put( "init", true );
        final String uri = scheme + "://" + alias;

        vfsService.call( new RemoteCallback<FileSystem>() {
            @Override
            public void callback( final FileSystem v ) {
                Window.alert( "The repository is created successfully" );
                hide();

                final Path rootPath = newPath( v, nameTextBox.getText(), uri );
                final Root newRoot = new Root( rootPath, new PathPlaceRequest( rootPath, "RepositoryEditor" ) );

                rootService.call( new RemoteCallback<Root>() {
                    @Override
                    public void callback( Root response ) {
                        event.fire( newRoot );
                    }
                } ).addRoot( newRoot );

                repositoryService.call( new RemoteCallback<Void>() {
                    @Override
                    public void callback( Void response ) {
                        //Nothing to do
                    }
                } ).createRepository( scheme,
                                      alias,
                                      username,
                                      password );

            }
        } ).newFileSystem( uri, env );

    }

    @UiHandler("cancel")
    public void onCancelClick( final ClickEvent e ) {
        hide();
    }

    public void hide() {
        popup.hide();
        CloseEvent.fire( this, this );
    }

    public void show() {
        popup.show();
    }

}
