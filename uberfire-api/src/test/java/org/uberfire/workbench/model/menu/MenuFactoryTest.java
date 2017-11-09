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

package org.uberfire.workbench.model.menu;

import org.junit.Test;

import static org.junit.Assert.*;

public class MenuFactoryTest {

    @Test
    public void testMenuFactorySecurityInfosNamespace() {

        final String caption = "caption";
        final String namespace = "namespace";

        final Menus build = MenuFactory
                .newSimpleItem( caption )
                .withNamespace( namespace )
                .endMenu()
                .build();

        final MenuItem menuItem = build.getItems().get( 0 );
        final String expectedSignature = "org.uberfire.workbench.model.menu.impl.MenuBuilderImpl$CurrentContext$4#namespace#caption";
        final String actualSignature = menuItem.getSignatureId();

        assertEquals( expectedSignature, actualSignature );
    }
}
