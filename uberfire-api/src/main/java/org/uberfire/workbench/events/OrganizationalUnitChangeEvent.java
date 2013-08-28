/*
 * Copyright 2012 JBoss Inc
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
package org.uberfire.workbench.events;

import org.uberfire.backend.organizationalunit.OrganizationalUnit;

/**
 * An event raised when the OrganizationalUnit in File Explorer (or any equivalent widget) changes
 */
public class OrganizationalUnitChangeEvent {

    private final OrganizationalUnit organizationalUnit;

    public OrganizationalUnitChangeEvent() {
        this.organizationalUnit = null;
    }

    public OrganizationalUnitChangeEvent( final OrganizationalUnit organizationalUnit ) {
        this.organizationalUnit = organizationalUnit;
    }

    public OrganizationalUnit getOrganizationalUnit() {
        return organizationalUnit;
    }

}
