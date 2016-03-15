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
package org.uberfire.security.impl.authz;

import org.junit.Test;
import org.uberfire.security.authz.Permission;

import static org.junit.Assert.*;

public class PermissionTest {

    @Test
    public void testChildGranted() {
        Permission p1 = new DotNamedPermission("resource.view", true);
        Permission p2 = new DotNamedPermission("resource.view.id1", true);
        assertTrue(p1.implies(p2));
    }

    @Test
    public void testChildAbstain() {
        Permission p1 = new DotNamedPermission("resource.view", true);
        Permission p2 = new DotNamedPermission("resource.view.id1");
        assertTrue(p1.implies(p2));
    }

    @Test
    public void testChildDenied() {
        Permission p1 = new DotNamedPermission("resource.view", true);
        Permission p2 = new DotNamedPermission("resource.view.id1", false);
        assertFalse(p1.implies(p2));
    }

    @Test
    public void testEqualsGranted() {
        Permission p1 = new DotNamedPermission("resource.view", true);
        Permission p2 = new DotNamedPermission("resource.view", true);
        assertTrue(p1.implies(p2));
    }

    @Test
    public void testEqualsAbstain() {
        Permission p1 = new DotNamedPermission("resource.view", true);
        Permission p2 = new DotNamedPermission("resource.view");
        assertTrue(p1.implies(p2));
    }

    @Test
    public void testEqualsDenied() {
        Permission p1 = new DotNamedPermission("resource.view", true);
        Permission p2 = new DotNamedPermission("resource.view", false);
        assertFalse(p1.implies(p2));
    }

    @Test
    public void testParentDenied() {
        Permission p1 = new DotNamedPermission("resource.view", false);
        Permission p2 = new DotNamedPermission("resource.view", false);
        Permission p3 = new DotNamedPermission("resource.view", true);
        Permission p4 = new DotNamedPermission("resource.view");
        assertTrue(p1.implies(p2));
        assertFalse(p1.implies(p3));
        assertFalse(p1.implies(p4));
    }

    @Test
    public void testParentAbstain() {
        Permission p1 = new DotNamedPermission("resource.view");
        Permission p2 = new DotNamedPermission("resource.view", false);
        Permission p3 = new DotNamedPermission("resource.view", true);
        Permission p4 = new DotNamedPermission("resource.view");
        assertFalse(p1.implies(p2));
        assertFalse(p1.implies(p3));
        assertTrue(p1.implies(p4));
    }

    @Test
    public void testParentGranted() {
        Permission p1 = new DotNamedPermission("resource.view", true);
        Permission p2 = new DotNamedPermission("resource.view", false);
        Permission p3 = new DotNamedPermission("resource.view", true);
        Permission p4 = new DotNamedPermission("resource.view");
        assertFalse(p1.implies(p2));
        assertTrue(p1.implies(p3));
        assertTrue(p1.implies(p4));
    }

    @Test
    public void testPrefixNotImply() {
        Permission p1 = new DotNamedPermission("resource.view.r", true);
        Permission p2 = new DotNamedPermission("resource.view.r2", true);
        assertFalse(p1.implies(p2));
    }

    @Test
    public void testEmptyNotImply() {
        Permission p1 = new DotNamedPermission("resource.view.", true);
        Permission p2 = new DotNamedPermission("resource.view.r2", true);
        assertFalse(p1.implies(p2));
    }

    @Test
    public void testLengthNotImply() {
        Permission p1 = new DotNamedPermission("resource.view.r1", true);
        Permission p2 = new DotNamedPermission("perspective.view.r2", true);
        assertFalse(p1.implies(p2));
    }
}
