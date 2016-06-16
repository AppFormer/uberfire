/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.screens.explorer;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWorkbenchConstants;
import org.uberfire.ext.security.management.client.screens.editor.GroupEditorScreen;
import org.uberfire.ext.security.management.client.screens.editor.RoleEditorScreen;
import org.uberfire.ext.security.management.client.screens.editor.UserEditorScreen;
import org.uberfire.ext.security.management.client.widgets.management.events.NewGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.NewUserEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnErrorEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.ReadGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.ReadRoleEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.ReadUserEvent;
import org.uberfire.ext.security.management.client.widgets.management.explorer.GroupsExplorer;
import org.uberfire.ext.security.management.client.widgets.management.explorer.RolesExplorer;
import org.uberfire.ext.security.management.client.widgets.management.explorer.UsersExplorer;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
@WorkbenchScreen(identifier = SecurityExplorerScreen.SCREEN_ID )
public class SecurityExplorerScreen {

    public static final String SCREEN_ID = "SecurityExplorerScreen";

    public interface View extends UberView<SecurityExplorerScreen> {

        void init(SecurityExplorerScreen presenter,
                  IsWidget rolesExplorer,
                  IsWidget groupsExplorer,
                  IsWidget usersExplorer);

        void rolesEnabled(boolean enabled);

        void groupsEnabled(boolean enabled);

        void usersEnabled(boolean enabled);
    }

    @Inject
    View view;

    @Inject
    RolesExplorer rolesExplorer;

    @Inject
    GroupsExplorer groupsExplorer;

    @Inject
    UsersExplorer usersExplorer;

    @Inject
    ErrorPopupPresenter errorPopupPresenter;

    @Inject
    PlaceManager placeManager;

    @Inject
    ClientUserSystemManager userSystemManager;

    @WorkbenchPartTitle
    public String getTitle() {
        return UsersManagementWorkbenchConstants.INSTANCE.securityExplorer();
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

    @PostConstruct
    public void init() {
        view.init(this, rolesExplorer, groupsExplorer, usersExplorer);
        rolesExplorer.show();
        view.rolesEnabled(true);
        view.groupsEnabled(false);
        view.usersEnabled(false);
    }

    @OnStartup
    public void onStartup() {
        userSystemManager.waitForInitialization(() -> {
            if (userSystemManager.isActive()) {
                groupsExplorer.show();
                usersExplorer.show();
                view.groupsEnabled(true);
                view.usersEnabled(true);
            }
        });
    }

    @OnClose
    public void onClose() {
        rolesExplorer.clear();
        groupsExplorer.clear();
        usersExplorer.clear();
    }

    // Event processing

    void onRoleRead(@Observes final ReadRoleEvent readRoleEvent) {
        checkNotNull("event", readRoleEvent);
        final String name = readRoleEvent.getName();
        final Map<String, String> params = new HashMap(1);
        params.put(RoleEditorScreen.ROLE_NAME, name);
        placeManager.goTo(new DefaultPlaceRequest(RoleEditorScreen.SCREEN_ID, params));
    }

    void onGroupRead(@Observes final ReadGroupEvent readGroupEvent) {
        final String name = readGroupEvent.getName();
        final Map<String, String> params = new HashMap<String, String>(1);
        params.put(GroupEditorScreen.GROUP_NAME, name);
        placeManager.goTo(new DefaultPlaceRequest(GroupEditorScreen.SCREEN_ID, params));
    }

    void onUserRead(@Observes final ReadUserEvent readUserEvent) {
        checkNotNull("event", readUserEvent);
        final String id = readUserEvent.getIdentifier();
        final Map<String, String> params = new HashMap<String, String>(1);
        params.put(UserEditorScreen.USER_ID, id);
        placeManager.goTo(new DefaultPlaceRequest(UserEditorScreen.SCREEN_ID, params));
    }

    void onGroupCreate(@Observes final NewGroupEvent newGroupEvent) {
        checkNotNull("event", newGroupEvent);
        final Map<String, String> params = new HashMap(1);
        params.put(GroupEditorScreen.ADD_GROUP, "true");
        placeManager.goTo(new DefaultPlaceRequest(GroupEditorScreen.SCREEN_ID, params));
    }

    void onUserCreate(@Observes final NewUserEvent newUserEvent) {
        checkNotNull("event", newUserEvent);
        final Map<String, String> params = new HashMap(1);
        params.put(UserEditorScreen.ADD_USER, "true");
        placeManager.goTo(new DefaultPlaceRequest(UserEditorScreen.SCREEN_ID, params));
    }

    void onErrorEvent(@Observes final OnErrorEvent onErrorEvent) {
        checkNotNull("event", onErrorEvent);
        final Throwable cause = onErrorEvent.getCause();
        final String message = onErrorEvent.getMessage();
        final String m = message != null ? message : cause.getMessage();
        errorPopupPresenter.showMessage(m);
    }
}
