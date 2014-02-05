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
package org.uberfire.client.mvp;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.workbench.WorkbenchServicesProxy;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.client.workbench.widgets.splash.SplashView;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.SplashScreenFilter;

/**
 * Base class for Pop-up Activities
 */
public abstract class AbstractSplashScreenActivity extends AbstractActivity
        implements SplashScreenActivity {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private WorkbenchServicesProxy wbServices;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    private SplashView splash = new SplashView();

    private Boolean showAgain;
    private SplashScreenFilter splashFilter;

    @Inject
    public AbstractSplashScreenActivity( final PlaceManager placeManager ) {
        super( placeManager );
    }

    @PostConstruct
    private void initialize() {
        this.splashFilter = getFilter();
    }

    @Override
    public void launch( final PlaceRequest place,
                        final Command callback ) {
        super.launch( place,
                      callback );

        wbServices.loadSplashScreenFilter( getFilter().getName(), new ParameterizedCommand<SplashScreenFilter>() {
            @Override
            public void execute( final SplashScreenFilter response ) {
                if ( response != null ) {
                    splashFilter = response;
                }
                init();

            }
        } );
    }

    public void init() {
        if ( !splashFilter.displayNextTime() ) {
            return;
        }
        forceShow();
    }

    @Override
    public abstract String getTitle();

    @Override
    public abstract IsWidget getWidget();

    @Override
    public Integer getBodyHeight() {
        return null;
    }

    @Override
    public abstract SplashScreenFilter getFilter();

    @Override
    public IsWidget getTitleDecoration() {
        return null;
    }

    @Override
    public void forceShow() {
        onStartup( place );

        final IsWidget widget = getWidget();

        splash.setContent( widget, getBodyHeight() );
        splash.setTitle( getTitle() );
        splash.show();
        splash.addCloseHandler( new CloseHandler<SplashView>() {
            @Override
            public void onClose( final CloseEvent<SplashView> event ) {
                AbstractSplashScreenActivity.this.onClose();
            }
        } );

        onOpen();
    }

    @Override
    public void onStartup() {
        //Do nothing.
    }

    @Override
    public void onStartup( final PlaceRequest place ) {
        //Do nothing.  
    }

    @Override
    public void onClose() {
        saveState();
    }

    @Override
    public void onShutdown() {
        //Do nothing.
    }

    @Override
    public Boolean intercept( final PlaceRequest intercepted ) {
        if ( splashFilter == null ) {
            return false;
        }
        for ( final String interceptPoint : splashFilter.getInterceptionPoints() ) {
            if ( intercepted.getIdentifier().equals( interceptPoint ) ) {
                return true;
            }
        }

        return false;
    }

    private void saveState() {
        showAgain = splash.showAgain();
        if ( showAgain != null ) {
            splashFilter.setDisplayNextTime( showAgain );
            wbServices.save( splashFilter );
        }
    }
}