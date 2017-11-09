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

package org.uberfire.workbench.model.menu.impl;

import org.junit.Test;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.MenuItemPerspective;
import org.uberfire.workbench.model.menu.MenuItemPlain;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MenuBuilderImplTest {

    @Test
    public void testMenuGroupGetSignatureId() {

        final MenuBuilderImpl.CurrentContext context = new MenuBuilderImpl.CurrentContext() {{

            contributionPoint = "contributionPoint";

            namespace = "namespace";

            caption = "caption";

            menuItems.add( mock( MenuItem.class ) );
        }};

        final MenuItem build = context.build();

        assertTrue( build instanceof MenuGroup );

        final String expectedSignature = "org.uberfire.workbench.model.menu.impl.MenuBuilderImpl$CurrentContext$1#contributionPoint#namespace#caption";
        final String actualSignature = build.getSignatureId();

        assertEquals( expectedSignature, actualSignature );
    }

    @Test
    public void testMenuItemCommandGetSignatureId() {

        final MenuBuilderImpl.CurrentContext context = new MenuBuilderImpl.CurrentContext() {{

            contributionPoint = "contributionPoint";

            namespace = "namespace";

            caption = "caption";

            command = command();
        }};

        final MenuItem build = context.build();

        assertTrue( build instanceof MenuItemCommand );

        final String expectedSignature = "org.uberfire.workbench.model.menu.impl.MenuBuilderImpl$CurrentContext$2#contributionPoint#namespace#caption";
        final String actualSignature = build.getSignatureId();

        assertEquals( expectedSignature, actualSignature );
    }

    @Test
    public void testMenuItemPerspectiveGetSignatureId() {

        final MenuBuilderImpl.CurrentContext context = new MenuBuilderImpl.CurrentContext() {{

            contributionPoint = "contributionPoint";

            namespace = "namespace";

            caption = "caption";

            placeRequest = mock( PlaceRequest.class );
        }};

        final MenuItem build = context.build();

        assertTrue( build instanceof MenuItemPerspective );

        final String expectedSignature = "org.uberfire.workbench.model.menu.impl.MenuBuilderImpl$CurrentContext$3#contributionPoint#namespace#caption";
        final String actualSignature = build.getSignatureId();

        assertEquals( expectedSignature, actualSignature );
    }

    @Test
    public void testMenuItemPlainGetSignatureId() {

        final MenuBuilderImpl.CurrentContext context = new MenuBuilderImpl.CurrentContext() {{

            contributionPoint = "contributionPoint";

            namespace = "namespace";

            caption = "caption";
        }};

        final MenuItem build = context.build();

        assertTrue( build instanceof MenuItemPlain );

        final String expectedSignature = "org.uberfire.workbench.model.menu.impl.MenuBuilderImpl$CurrentContext$4#contributionPoint#namespace#caption";
        final String actualSignature = build.getSignatureId();

        assertEquals( expectedSignature, actualSignature );
    }

    private Command command() {
        return new Command() {
            public void execute() {
                // empty
            }
        };
    }
}
