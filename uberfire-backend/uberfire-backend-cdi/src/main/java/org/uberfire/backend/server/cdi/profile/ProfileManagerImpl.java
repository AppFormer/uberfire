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

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.profile.ProfileManager;

import static com.google.common.base.Strings.isNullOrEmpty;

@Service
@ApplicationScoped
public class ProfileManagerImpl implements ProfileManager {

    protected static final String PROFILE_SYSTEM_PROPERTY_NAME = "org.uberfire.profile.active";

    public static boolean isProfileActive(final String profile) {
        if (isNullOrEmpty(profile)) {
            return false;
        }
        final String activeProfile = System.getProperty(PROFILE_SYSTEM_PROPERTY_NAME);
        if (isNullOrEmpty(activeProfile)) {
            return true;
        } else {
            return activeProfile.equals(profile);
        }
    }

    @Override
    public boolean isProfileEnabled(final String profile) {
        return isProfileActive(profile);
    }
}
