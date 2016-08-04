/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.layout.editor.client.infra;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Modal;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.api.HasModalConfiguration;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.ModalConfigurationContext;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;
import org.uberfire.mvp.Command;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class DragHelperComponentColumn {

    @Inject
    LayoutDragComponentHelper helper;

    private LayoutDragComponent layoutDragComponent;

    private LayoutComponent layoutComponent;

    public LayoutDragComponent getLayoutDragComponent() {
        if ( layoutDragComponent == null ) {
            layoutDragComponent =
                    helper.lookupDragTypeBean( layoutComponent.getDragTypeName() );
        }
        return layoutDragComponent;
    }

    public boolean hasModalConfiguration() {
        return getLayoutDragComponent() instanceof HasModalConfiguration;
    }

    public void setLayoutComponent( LayoutComponent layoutComponent ) {
        this.layoutComponent = layoutComponent;
    }

    public IsWidget getPreviewWidget( Widget context ) {
        return getLayoutDragComponent().getPreviewWidget( new RenderingContext( layoutComponent, context ) );
    }

    public void showConfigModal( Command configurationFinish, Command configurationCanceled ) {
        ModalConfigurationContext ctx = new ModalConfigurationContext( layoutComponent,
                                                                       configurationFinish,
                                                                       configurationCanceled
        );
        Modal configModal = ( ( HasModalConfiguration )
                getLayoutDragComponent() ).getConfigurationModal( ctx );
        configModal.show();
    }
}
