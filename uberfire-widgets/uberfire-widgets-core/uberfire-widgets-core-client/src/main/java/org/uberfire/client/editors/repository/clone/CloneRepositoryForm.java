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

package org.uberfire.client.editors.repository.clone;

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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.bus.client.api.ErrorCallback;
import org.jboss.errai.bus.client.api.Message;
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
public class CloneRepositoryForm
        extends PopupPanel {

    interface CloneRepositoryFormBinder
            extends
            UiBinder<Widget, CloneRepositoryForm> {

    }

    private static CloneRepositoryFormBinder uiBinder = GWT.create( CloneRepositoryFormBinder.class );

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
    ControlGroup urlGroup;

    @UiField
    TextBox gitURLTextBox;

    @UiField
    HelpInline urlHelpInline;

    @UiField
    TextBox usernameTextBox;

    @UiField
    PasswordTextBox passwordTextBox;

    @UiField
    Modal popup;

    @PostConstruct
    public void init() {
        setWidget( uiBinder.createAndBindUi( this ) );
        popup.setDynamicSafe( true );
        nameTextBox.addKeyPressHandler( new KeyPressHandler() {
            @Override
            public void onKeyPress( final KeyPressEvent event ) {
                nameGroup.setType( ControlGroupType.NONE );
                nameHelpInline.setText( "" );
            }
        } );
        gitURLTextBox.addKeyPressHandler( new KeyPressHandler() {
            @Override
            public void onKeyPress( final KeyPressEvent event ) {
                urlGroup.setType( ControlGroupType.NONE );
                urlHelpInline.setText( "" );
            }
        } );
    }

//    @Override
//    public HandlerRegistration addCloseHandler( final CloseHandler<CloneRepositoryForm> handler ) {
//        return addHandler( handler, CloseEvent.getType() );
//    }

    @UiHandler("clone")
    public void onCloneClick( final ClickEvent e ) {

        boolean hasError = false;
        if ( nameTextBox.getText() == null || nameTextBox.getText().trim().isEmpty() ) {
            nameGroup.setType( ControlGroupType.ERROR );
            nameHelpInline.setText( "Repository Name is mandatory" );
            hasError = true;
        } else {
            nameGroup.setType( ControlGroupType.NONE );
        }

        if ( gitURLTextBox.getText() == null || gitURLTextBox.getText().trim().isEmpty() ) {
            urlGroup.setType( ControlGroupType.ERROR );
            urlHelpInline.setText( "URL is mandatory" );
            hasError = true;
        } else {
            urlGroup.setType( ControlGroupType.NONE );
        }

        if ( hasError ) {
            return;
        }

        final String scheme = "git";
        final String alias = nameTextBox.getText();
        final String origin = gitURLTextBox.getText();
        final String username = usernameTextBox.getText();
        final String password = passwordTextBox.getText();
        final Map<String, Object> env = new HashMap<String, Object>( 3 );
        env.put( "username", username );
        env.put( "password", password );
        env.put( "origin", origin );
        final String uri = scheme + "://" + alias;

        vfsService.call( new RemoteCallback<FileSystem>() {
                             @Override
                             public void callback( final FileSystem fs ) {
                                 Window.alert( "The repository is cloned successfully" );
                                 hide();

                                 final Path rootPath = newPath( fs, nameTextBox.getText(), uri );
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
                                 } ).cloneRepository( scheme,
                                                      alias,
                                                      origin,
                                                      username,
                                                      password );
                             }
                         },
                         new ErrorCallback() {
                             @Override
                             public boolean error( final Message message,
                                                   final Throwable throwable ) {
                                 Window.alert( "Can't clone repository, please check error message. \n" + message.toString() );

                                 return false;
                             }
                         }
                       ).newFileSystem( uri, env );

    }

    @UiHandler("cancel")
    public void onCancelClick( final ClickEvent e ) {
        hide();
    }

    public void hide() {
        popup.hide();
        super.hide();
    }

    public void show() {
        popup.show();
    }

}
