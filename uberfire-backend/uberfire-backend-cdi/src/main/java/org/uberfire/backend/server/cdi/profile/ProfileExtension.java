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

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.uberfire.commons.services.cdi.Profile;

import static com.google.common.base.Strings.isNullOrEmpty;

public class ProfileExtension implements Extension {

    <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat) {
        final String profile = getProfileAnnotationValue(pat.getAnnotatedType().getJavaClass());
        if(isNullOrEmpty(profile)){
            return;
        }

        if (ProfileManagerImpl.isProfileActive(profile) == false) {
            pat.veto();
        }
    }

    private String getProfileAnnotationValue(final Class<?> aClass) {
        final Profile[] annotationsByType = aClass.getAnnotationsByType(Profile.class);
        if (annotationsByType.length > 0) {
            return annotationsByType[0].value();
        }
        return getProfileAnnotationPackageValue(aClass.getPackage());
    }

    private String getProfileAnnotationPackageValue(final Package pkg) {
        if(pkg == null){
            return null;
        }

        final Profile[] annotationsByType = pkg.getAnnotationsByType(Profile.class);
        if(annotationsByType.length > 0){
            return annotationsByType[0].value();
        } else {
            return null;
        }
    }

}
