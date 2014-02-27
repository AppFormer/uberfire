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
import javax.inject.Named;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;


@Dependent
@Named("WorkbenchPartTemplateView")
public class WorkbenchPartTemplateView
        extends SimpleLayoutPanel
        implements WorkbenchPartPresenter.View {

    private WorkbenchPartPresenter presenter;

    private final FlowPanel sp = new FlowPanel();

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
       // return sp.getWidget();
        return sp;
    }

    public WorkbenchPartTemplateView() {
        setWidget( sp );
    }

    @Override
    public void onResize() {
//        final Widget parent = getParent();
//        if ( parent != null ) {
//            sp.setPixelSize( parent.getOffsetWidth(),
//                             parent.getOffsetHeight() );
//        }
//        super.onResize();
    }
}
