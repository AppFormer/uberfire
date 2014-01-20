/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.workbench;

import java.util.Collection;
import java.util.Collections;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

import static org.uberfire.workbench.model.PanelType.*;

@Dependent
@Named("StandaloneEditorPerspective")
public class StandaloneEditorPerspective extends AbstractWorkbenchPerspectiveActivity {

    @Inject
    public StandaloneEditorPerspective( final PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl( ROOT_SIMPLE );
        p.setName( "Standalone Editor Perspective" );
        p.setTransient( true );
        return p;
    }

    @Override
    public String getIdentifier() {
        return "StandaloneEditorPerspective";
    }

    @Override
    public String getSignatureId() {
        return StandaloneEditorPerspective.class.getName();
    }

    @Override
    public Collection<String> getRoles() {
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getTraits() {
        return Collections.emptyList();
    }
}
