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
package org.uberfire.client.views.bs2.notfound;

import javax.annotation.PostConstruct;

import org.uberfire.client.workbench.widgets.notfound.ActivityNotFoundPresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * View for when an Activity cannot be found
 */
public class ActivityNotFoundView extends SimplePanel
        implements
        ActivityNotFoundPresenter.View {

    interface ActivityNotFoundViewBinder
            extends
            UiBinder<Widget, ActivityNotFoundView> {

    }

    private static ActivityNotFoundViewBinder uiBinder = GWT.create( ActivityNotFoundViewBinder.class );

    private ActivityNotFoundPresenter presenter;

    @UiField
    public Label requestedPlaceIdentifierLabel;

    @PostConstruct
    public void init() {
        setWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final ActivityNotFoundPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setRequestedPlaceIdentifier( String identifier ) {
        requestedPlaceIdentifierLabel.setText( identifier );
    }

    @UiHandler("okButton")
    public void onClickOkButton( final ClickEvent event ) {
        presenter.close();
    }

}
