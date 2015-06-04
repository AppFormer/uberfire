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
package org.uberfire.client.views.pfly.tab;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.util.Layouts;
import org.uberfire.client.workbench.panels.MultiPartWidget;
import org.uberfire.client.workbench.panels.impl.AbstractMultiPartWorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter;

@Dependent
@Named( "MultiTabWorkbenchPanelView" )
public class MultiTabWorkbenchPanelView
        extends AbstractMultiPartWorkbenchPanelView<MultiTabWorkbenchPanelPresenter> {

    private UberTabPanel uberTabPanel;

    @Inject
    public MultiTabWorkbenchPanelView( final UberTabPanel uberTabPanel ) {
        this.uberTabPanel = uberTabPanel;
    }

    @Override
    protected MultiPartWidget setupWidget() {
        uberTabPanel.addStyleName( "uf-multitab-panel" );
        Layouts.setToFillParent( uberTabPanel );
        addOnFocusHandler( uberTabPanel );
        addSelectionHandler( uberTabPanel );

        return uberTabPanel;
    }

}
