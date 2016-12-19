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

package org.uberfire.wbtest.client.headfoot;

import org.jboss.errai.databinding.client.api.Bindable;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;

@Bindable
public class NewPanelBuilder {

    private String partPlace;
    private String type;
    private String position;

    public String getPartPlace() {
        return partPlace;
    }

    public void setPartPlace( String partPlace ) {
        this.partPlace = partPlace;
    }

    public String getType() {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition( String position ) {
        this.position = position;
    }

    public void makePanel( PlaceManager placeManager, PanelManager panelManager ) {
        PlaceRequest place = DefaultPlaceRequest.parse( partPlace );
        PanelDefinition panel = new PanelDefinitionImpl( type );
        panelManager.addWorkbenchPanel( panelManager.getRoot(),
                                        panel,
                                        CompassPosition.valueOf( position.toUpperCase() ) );
        placeManager.goTo( place, panel );
    }
}
