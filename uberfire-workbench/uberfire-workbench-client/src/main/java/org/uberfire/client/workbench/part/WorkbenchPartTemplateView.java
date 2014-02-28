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
package org.uberfire.client.workbench.part;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.Workbench;

@Dependent
@Named("WorkbenchPartTemplateView")
public class WorkbenchPartTemplateView
        extends SimpleLayoutPanel
        implements WorkbenchPartPresenter.View {

    private WorkbenchPartPresenter presenter;

    private final FlowPanel sp = new FlowPanel();

    @Inject
    private Workbench workbench;

    @Override
    public void init( WorkbenchPartPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public WorkbenchPartPresenter getPresenter() {
        return this.presenter;
    }

    @Override
    public void setWrappedWidget( final IsWidget widget ) {
        sp.add( widget );
    }

    @Override
    public IsWidget getWrappedWidget() {
        return sp;
    }

    public WorkbenchPartTemplateView() {
        setWidget( sp );
    }

    @Override
    public void onResize() {
        final int width = Window.getClientWidth();
        final int height = Window.getClientHeight();
        sp.setHeight( height + "px" );
        sp.setWidth( width + "px" );
      /*  sp.setPixelSize( width, height );
        DOM.setStyleAttribute( sp.getElement(), "position", "relative" );
        DOM.setStyleAttribute( this.getElement(), "position", "relative" );
        DOM.setStyleAttribute( super.getElement(), "position", "relative" );
        WorkbenchPartTemplateView parent = (WorkbenchPartTemplateView) sp.getParent();
        DOM.setStyleAttribute( parent.getWrappedWidget().asWidget().getElement(), "position", "relative" );*/

        //codigo original
     /*   final Widget parent2 = getParent();
        if ( parent2 != null ) {
            sp.setPixelSize( parent2.getOffsetWidth(),
                             parent2.getOffsetHeight() );
        }
        super.onResize();*/
    }
}