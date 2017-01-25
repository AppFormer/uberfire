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

package org.uberfire.backend.server.cdi;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.cdi.workspace.WorkspaceScoped;

/**
 * Just for testing purposes
 */
@WorkspaceScoped
public class WorkspaceBuilderService implements Serializable {

    private Logger logger = LoggerFactory.getLogger( WorkspaceBuilderService.class );

    public void build( String gav ) {
        try {
            logger.info( "Building {} ...", gav );
            Thread.currentThread().sleep( 5000l );
            logger.info( "Building finished {}", gav );
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }

    }

}
