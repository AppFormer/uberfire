/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.keycloak;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.commons.config.ConfigProperties;
import org.uberfire.ext.security.management.UberfireRoleManager;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KCCredentialsUserManagementServiceTest {

    @Mock
    KeyCloakUserManager userManager;
    @Mock
    KeyCloakGroupManager groupManager;
    @Mock
    CredentialsClientFactory clientFactory;
    @Mock
    UberfireRoleManager roleManager;
    private KCCredentialsUserManagementService tested;

    @Before
    public void setup() throws Exception {
        this.tested = new KCCredentialsUserManagementService(userManager,
                                                             groupManager,
                                                             clientFactory,
                                                             roleManager);
    }

    @Test
    public void testInit() {
        this.tested.init();
        verify(clientFactory,
               times(1)).init(any(ConfigProperties.class));
        verify(userManager,
               times(1)).init(clientFactory);
        verify(groupManager,
               times(1)).init(clientFactory);
    }

    @Test
    public void testGetUsersManager() {
        Assert.assertEquals(userManager,
                            tested.users());
    }

    @Test
    public void testGetGroupsManager() {
        Assert.assertEquals(groupManager,
                            tested.groups());
    }
}
