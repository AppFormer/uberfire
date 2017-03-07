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

package org.uberfire.ext.security.management.client.widgets.management.editor.user;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.widgets.management.editor.AssignedEntitiesEditor;
import org.uberfire.ext.security.management.client.widgets.management.events.OnUpdateUserRolesEvent;
import org.uberfire.ext.security.management.client.widgets.management.explorer.EntitiesExplorerView;
import org.uberfire.ext.security.management.client.widgets.management.explorer.ExplorerViewContext;
import org.uberfire.ext.security.management.client.widgets.management.explorer.RolesExplorer;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class UserAssignedRolesEditorTest {

    @Mock
    ClientUserSystemManager userSystemManager;
    @Mock
    EventSourceMock<OnUpdateUserRolesEvent> updateUserRolesEvent;
    @Mock
    RolesExplorer rolesExplorer;
    @Mock
    AssignedEntitiesEditor<UserAssignedRolesEditor> view;
    @Mock
    User user;
    private UserAssignedRolesEditor tested;

    @Before
    public void setup() {
        Map<String, String> userAttributes = new HashMap<String, String>(1);
        userAttributes.put("attr1",
                           "value1");
        when(user.getIdentifier()).thenReturn("user1");
        when(user.getProperties()).thenReturn(userAttributes);
        final Set<Role> roles = new HashSet<Role>();
        final Role role = mock(Role.class);
        when(role.getName()).thenReturn("role1");
        roles.add(role);
        when(user.getRoles()).thenReturn(roles);
        doAnswer(new Answer<Role>() {
            @Override
            public Role answer(InvocationOnMock invocationOnMock) throws Throwable {
                Role _role = mock(Role.class);
                return _role;
            }
        }).when(userSystemManager).createRole(anyString());
        tested = new UserAssignedRolesEditor(userSystemManager,
                                             rolesExplorer,
                                             view,
                                             updateUserRolesEvent);
    }

    @Test
    public void testInit() {
        tested.init();
        verify(view,
               times(1)).init(tested);
        verify(view,
               times(1)).configure(any(EntitiesExplorerView.class));
        verify(view,
               times(1)).configureClose(anyString(),
                                        any(Command.class));
        verify(view,
               times(1)).configureSave(anyString(),
                                       any(Command.class));
        verify(rolesExplorer,
               times(1)).setPageSize(anyInt());
        verify(view,
               times(0)).show(anyString());
        verify(view,
               times(0)).hide();
    }

    @Test
    public void testClear() {
        tested.clear();
        verify(rolesExplorer,
               times(1)).clear();
        assertTrue(tested.entities.isEmpty());
        assertNoViewCalls();
    }

    @Test
    public void testHide() {
        tested.hide();
        verify(view,
               times(1)).hide();
        verify(view,
               times(0)).init(any(UserAssignedRolesEditor.class));
        verify(view,
               times(0)).configure(any(EntitiesExplorerView.class));
        verify(view,
               times(0)).show(anyString());
        verify(view,
               times(0)).configureClose(anyString(),
                                        any(Command.class));
        verify(view,
               times(0)).configureSave(anyString(),
                                       any(Command.class));
    }

    @Test
    public void testShow() {
        tested.show(user);
        assertFalse(tested.isEditMode);
        assertTrue(tested.entities.size() == 1);
        verify(rolesExplorer,
               times(1)).show(any(ExplorerViewContext.class));
        verify(view,
               times(1)).show(anyString());
        verify(view,
               times(0)).init(any(UserAssignedRolesEditor.class));
        verify(view,
               times(0)).configure(any(EntitiesExplorerView.class));
        verify(view,
               times(0)).hide();
        verify(view,
               times(0)).configureClose(anyString(),
                                        any(Command.class));
        verify(view,
               times(0)).configureSave(anyString(),
                                       any(Command.class));
    }

    @Test
    public void testEdit() {
        tested.edit(user);
        assertTrue(tested.isEditMode);
        assertTrue(tested.entities.size() == 1);
        verify(rolesExplorer,
               times(1)).show(any(ExplorerViewContext.class));
        verify(view,
               times(1)).show(anyString());
        verify(view,
               times(0)).init(any(UserAssignedRolesEditor.class));
        verify(view,
               times(0)).configure(any(EntitiesExplorerView.class));
        verify(view,
               times(0)).hide();
        verify(view,
               times(0)).configureClose(anyString(),
                                        any(Command.class));
        verify(view,
               times(0)).configureSave(anyString(),
                                       any(Command.class));
    }

    @Test
    public void testCloseEditorCallback() {
        tested.closeEditorCallback.execute();
        verify(view,
               times(1)).hide();
        verify(view,
               times(0)).init(any(UserAssignedRolesEditor.class));
        verify(view,
               times(0)).configure(any(EntitiesExplorerView.class));
        verify(view,
               times(0)).show(anyString());
        verify(view,
               times(0)).configureClose(anyString(),
                                        any(Command.class));
        verify(view,
               times(0)).configureSave(anyString(),
                                       any(Command.class));
    }

    @Test
    public void testSaveEditorCallback() {
        final Set<String> selectedRoles = new HashSet<String>();
        selectedRoles.add("roleE1");
        when(rolesExplorer.getSelectedEntities()).thenReturn(selectedRoles);
        tested.saveEditorCallback.execute();
        assertTrue(tested.entities.size() == 1);
        verify(rolesExplorer,
               times(1)).getSelectedEntities();
        verify(rolesExplorer,
               times(1)).clear();
        verify(updateUserRolesEvent,
               times(1)).fire(any(OnUpdateUserRolesEvent.class));
        verify(view,
               times(1)).hide();
        verify(view,
               times(0)).init(any(UserAssignedRolesEditor.class));
        verify(view,
               times(0)).configure(any(EntitiesExplorerView.class));
        verify(view,
               times(0)).show(anyString());
        verify(view,
               times(0)).configureClose(anyString(),
                                        any(Command.class));
        verify(view,
               times(0)).configureSave(anyString(),
                                       any(Command.class));
    }

    private void assertNoViewCalls() {
        verify(view,
               times(0)).init(any(UserAssignedRolesEditor.class));
        verify(view,
               times(0)).configure(any(EntitiesExplorerView.class));
        verify(view,
               times(0)).show(anyString());
        verify(view,
               times(0)).hide();
        verify(view,
               times(0)).configureClose(anyString(),
                                        any(Command.class));
        verify(view,
               times(0)).configureSave(anyString(),
                                       any(Command.class));
    }
}
