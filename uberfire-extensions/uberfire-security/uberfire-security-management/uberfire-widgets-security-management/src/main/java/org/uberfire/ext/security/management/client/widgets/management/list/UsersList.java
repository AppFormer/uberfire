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

package org.uberfire.ext.security.management.client.widgets.management.list;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.security.management.client.widgets.popup.LoadingBox;

@Dependent
public class UsersList extends EntitiesList<User> {

    @Inject
    public UsersList(LoadingBox loadingBox,
                     View view) {
        super(loadingBox,
              view);
    }
}
