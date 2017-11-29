/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.authz;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.impl.authz.DefaultPermissionManager;
import org.uberfire.workbench.model.ActivityResourceType;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PerspectivePermissionTest {

    PermissionManager permissionManager;

    @Before
    public void setUp() {
        permissionManager = new DefaultPermissionManager();
    }

    @Test
    public void testPermissionCreation() {
        Permission readPermission = permissionManager.createPermission(ActivityResourceType.PERSPECTIVE,
                                                                       PerspectiveAction.READ,
                                                                       true);
        Permission updatePermission = permissionManager.createPermission(ActivityResourceType.PERSPECTIVE,
                                                                         PerspectiveAction.UPDATE,
                                                                         true);
        Permission deletePermission = permissionManager.createPermission(ActivityResourceType.PERSPECTIVE,
                                                                         PerspectiveAction.DELETE,
                                                                         true);
        Permission createPermission = permissionManager.createPermission(ActivityResourceType.PERSPECTIVE,
                                                                         PerspectiveAction.CREATE,
                                                                         true);

        assertEquals(readPermission.getName(),
                     "perspective.read");
        assertEquals(updatePermission.getName(),
                     "perspective.update");
        assertEquals(deletePermission.getName(),
                     "perspective.delete");
        assertEquals(createPermission.getName(),
                     "perspective.create");
    }
}