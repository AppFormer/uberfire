/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.backend.server.cdi.profile;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.uberfire.backend.server.cdi.profile.ProfileManagerImpl.PROFILE_SYSTEM_PROPERTY_NAME;

@RunWith(MockitoJUnitRunner.class)
public class ProfileManagerImplTest {

    private static String systemProfile = null;

    @InjectMocks
    ProfileManagerImpl profileManager;

    @BeforeClass
    public static void before(){
        systemProfile = System.getProperty(PROFILE_SYSTEM_PROPERTY_NAME);
    }

    @AfterClass
    public static void after(){
        System.setProperty(PROFILE_SYSTEM_PROPERTY_NAME, systemProfile);
    }

    @Test
    public void testProfileActive(){
        assertFalse(profileManager.isProfileEnabled(null));
        assertFalse(profileManager.isProfileEnabled(""));
        assertTrue(profileManager.isProfileEnabled("runtime"));
        System.setProperty(PROFILE_SYSTEM_PROPERTY_NAME, "authoring");
        assertTrue(profileManager.isProfileEnabled("authoring"));
        assertFalse(profileManager.isProfileEnabled("Authoring"));
    }

}
