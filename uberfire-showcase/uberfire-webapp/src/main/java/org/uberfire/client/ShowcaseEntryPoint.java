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
package org.uberfire.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.uberfire.backend.FileExplorerRootService;
import org.uberfire.backend.Root;
import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.menu.MenuFactory;
import org.uberfire.client.workbench.widgets.menu.MenuItem;
import org.uberfire.client.workbench.widgets.menu.MenuPosition;
import org.uberfire.client.workbench.widgets.menu.MenuSearchItem;
import org.uberfire.client.workbench.widgets.menu.Menus;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBar;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import static java.util.Arrays.*;
import static org.uberfire.client.workbench.widgets.menu.MenuFactory.*;

/**
 * GWT's Entry-point for Uberfire-showcase
 */
@EntryPoint
public class ShowcaseEntryPoint {

    @Inject
    private IOCBeanManager manager;

    @Inject
    private WorkbenchMenuBar menubar;

    @Inject
    private Caller<FileExplorerRootService> rootService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private IOCBeanManager iocManager;

    @Inject
    private ActivityManager activityManager;

    private String[] menuItems = new String[]{ "FileExplorer", "Chart", "MiscellaneousFeatures", "MultiPage", "Duplicate" };

    @AfterInitialization
    public void startApp() {
        setupMenu();
        setupFileSystems();
        hideLoadingPopup();
    }

    private void setupMenu() {

        final AbstractWorkbenchPerspectiveActivity defaultPerspective = getDefaultPerspectiveActivity();

        final Menus menus =
                newTopLevelMenu( "Home" )
                        .respondsWith( new Command() {
                            @Override
                            public void execute() {
                                if ( defaultPerspective != null ) {
                                    placeManager.goTo( new DefaultPlaceRequest( defaultPerspective.getIdentifier() ) );
                                } else {
                                    Window.alert( "Default perspective not found." );
                                }
                            }
                        } )
                        .endMenu()
                        .newTopLevelMenu( "Perspectives" )
                        .withItems( getPerspectives() )
                        .endMenu()
                        .newTopLevelMenu( "Screens" )
                        .withItems( getScreens() )
                        .endMenu()
                        .newTopLevelMenu( "Logout" )
                        .respondsWith( new Command() {
                            @Override
                            public void execute() {
                                redirect( GWT.getModuleBaseURL() + "uf_logout" );
                            }
                        } )
                        .endMenu()
                        .newSearchItem( "search" )
                        .position( MenuPosition.RIGHT )
                        .respondsWith( new MenuSearchItem.SearchCommand() {
                            @Override
                            public void execute( final String term ) {
                                Window.alert( "Search:" + term );
                            }
                        } )
                        .endMenu()
                        .build();

        menubar.aggregateWorkbenchMenus( menus );
    }

    private void setupFileSystems() {
        rootService.call( new RemoteCallback<Collection<Root>>() {

            @Override
            public void callback( final Collection<Root> roots ) {
                //Nothing to do; this just ensures FileSystems have been initialized
            }
        } ).listRoots();
    }

    private List<MenuItem> getScreens() {
        final List<MenuItem> screens = new ArrayList<MenuItem>();

        sort( menuItems );

        for ( final String menuItem : menuItems ) {
            final MenuItem item = MenuFactory.newSimpleItem( menuItem )
                    .respondsWith( new Command() {
                        @Override
                        public void execute() {
                            placeManager.goTo( new DefaultPlaceRequest( menuItem ) );
                        }
                    } ).endMenu().build().getItems().get( 0 );
            screens.add( item );
        }

        return screens;
    }

    private List<MenuItem> getPerspectives() {
        final List<MenuItem> perspectives = new ArrayList<MenuItem>();
        for ( final AbstractWorkbenchPerspectiveActivity perspective : getPerspectiveActivities() ) {
            final String name = perspective.getPerspective().getName();
            final Command cmd = new Command() {

                @Override
                public void execute() {
                    placeManager.goTo( new DefaultPlaceRequest( perspective.getIdentifier() ) );
                }

            };
            final MenuItem item = MenuFactory.newSimpleItem( name ).respondsWith( cmd ).endMenu().build().getItems().get( 0 );
            perspectives.add( item );
        }

        return perspectives;
    }

    private AbstractWorkbenchPerspectiveActivity getDefaultPerspectiveActivity() {
        AbstractWorkbenchPerspectiveActivity defaultPerspective = null;
        final Collection<IOCBeanDef<AbstractWorkbenchPerspectiveActivity>> perspectives = iocManager.lookupBeans( AbstractWorkbenchPerspectiveActivity.class );
        final Iterator<IOCBeanDef<AbstractWorkbenchPerspectiveActivity>> perspectivesIterator = perspectives.iterator();
        outer_loop:
        while ( perspectivesIterator.hasNext() ) {
            final IOCBeanDef<AbstractWorkbenchPerspectiveActivity> perspective = perspectivesIterator.next();
            final AbstractWorkbenchPerspectiveActivity instance = perspective.getInstance();
            if ( instance.isDefault() ) {
                defaultPerspective = instance;
                break outer_loop;
            } else {
                iocManager.destroyBean( instance );
            }
        }
        return defaultPerspective;
    }

    private List<AbstractWorkbenchPerspectiveActivity> getPerspectiveActivities() {

        //Get Perspective Providers
        final Set<AbstractWorkbenchPerspectiveActivity> activities = activityManager.getActivities( AbstractWorkbenchPerspectiveActivity.class );

        //Sort Perspective Providers so they're always in the same sequence!
        List<AbstractWorkbenchPerspectiveActivity> sortedActivities = new ArrayList<AbstractWorkbenchPerspectiveActivity>( activities );
        Collections.sort( sortedActivities,
                          new Comparator<AbstractWorkbenchPerspectiveActivity>() {

                              @Override
                              public int compare( AbstractWorkbenchPerspectiveActivity o1,
                                                  AbstractWorkbenchPerspectiveActivity o2 ) {
                                  return o1.getPerspective().getName().compareTo( o2.getPerspective().getName() );
                              }

                          } );

        return sortedActivities;
    }

    //Fade out the "Loading application" pop-up
    private void hideLoadingPopup() {
        final Element e = RootPanel.get( "loading" ).getElement();

        new Animation() {

            @Override
            protected void onUpdate( double progress ) {
                e.getStyle().setOpacity( 1.0 - progress );
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility( Style.Visibility.HIDDEN );
            }
        }.run( 500 );
    }

    public static native void redirect( String url )/*-{
        $wnd.location = url;
    }-*/;

}