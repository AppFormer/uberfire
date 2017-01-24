/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.wbtest.client.api;

import java.util.Collection;
import java.util.Collections;

import org.uberfire.client.mvp.AbstractWorkbenchScreenActivity;
import org.uberfire.client.mvp.PlaceManager;

/**
 * Provides default implementations for most of the screen activity interface.
 */
public abstract class AbstractTestScreenActivity extends AbstractWorkbenchScreenActivity {

    public AbstractTestScreenActivity( PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public String getTitle() {
        return getClass().getName();
    }

}
