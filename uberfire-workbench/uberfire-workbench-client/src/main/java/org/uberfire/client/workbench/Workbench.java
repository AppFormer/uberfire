/*
 * Copyright 2012 JBoss Inc
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
package org.uberfire.client.workbench;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ApplicationReadyEvent;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.sort;

/**
 * Responsible for bootstrapping the client-side Workbench user interface. Normally this happens automatically with no
 * need for assistance or interference from the application. Thus, applications don't usually need to do anything with
 * the Workbench class directly.
 * 
 * <h2>Delaying Workbench Startup</h2>
 * 
 * In special cases, applications may wish to delay the startup of the workbench. For example, an application that
 * relies on global variables (also known as singletons or Application Scoped beans) that are initialized based on
 * response data from the server doesn't want UberFire to start initializing its widgets until that server response has
 * come in.
 * <p>
 * To delay startup, add a <i>Startup Blocker</i> before Errai starts calling {@link AfterInitialization} methods.
 * The best place to do this is in the {@link PostConstruct} method of an {@link EntryPoint} bean. You would then
 * remove the startup blocker from within the callback from the server:
 * 
 * <pre>
 *   {@code @EntryPoint}
 *   public class MyMutableGlobal() {
 *     {@code @Inject private Workbench workbench;}
 *     {@code @Inject private Caller<MyRemoteService> remoteService;}
 * 
 *     // set up by a server call. don't start the app until it's populated!
 *     {@code private MyParams params;}
 * 
 *     {@code @PostConstruct}
 *     private void earlyInit() {
 *       workbench.addStartupBlocker(MyMutableGlobal.class);
 *     }
 * 
 *     {@code @AfterInitialization}
 *     private void lateInit() {
 *       remoteService.call(new {@code RemoteCallback<MyParams>}{
 *         public void callback(MyParams params) {
 *           MyMutableGlobal.this.params = params;
 *           workbench.removeStartupBlocker(MyMutableGlobal.class);
 *         }
 *       }).fetchParameters();
 *     }
 *   }
 * </pre>
 */
@ApplicationScoped
public class Workbench {

    /**
     * List of classes who want to do stuff (often server communication) before the workbench shows up.
     */
    private final Set<Class<?>> startupBlockers = new HashSet<Class<?>>();

    /**
     * Fired when all startup blockers have cleared and just before the workbench starts to build its components.
     */
    @Inject
    private Event<ApplicationReadyEvent> appReady;

    // ------------------------
    // core services

    @Inject
    private SyncBeanManager iocManager;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private WorkbenchServicesProxy wbServices;

    @Inject
    private VFSServiceProxy vfsService;

    // ------------------------
    // Layout handling

    @Inject
    LayoutSelection layoutSelection;

    private WorkbenchLayout layout;

    @Inject
    private PanelManager panelManager;

    // ------------------------


    private boolean isStandaloneMode = false;
    private final Set<String> headersToKeep = new HashSet<String>();

    private final WorkbenchCloseHandler workbenchCloseHandler = GWT.create( WorkbenchCloseHandler.class );

    private final Command workbenchCloseCommand = new Command() {
        @Override
        public void execute() {
            final PerspectiveDefinition perspective = panelManager.getPerspective();
            if ( perspective != null ) {
                wbServices.save( perspective );
            }
        }

    };


    /**
     * Requests that the workbench does not attempt to create any UI parts until the given responsible party has
     * been removed as a startup blocker. Blockers are tracked as a set, so adding the same class more than once has no
     * effect.
     * 
     * @param responsibleParty
     *            any Class object; typically it will be the class making the call to this method.
     *            Must not be null.
     */
    public void addStartupBlocker( Class<?> responsibleParty ) {
        startupBlockers.add( responsibleParty );
        System.out.println( responsibleParty.getName() + " is blocking workbench startup." );
    }

    /**
     * Causes the given responsible party to no longer block workbench initialization.
     * If the given responsible party was not already in the blocking set (either because
     * it was never added, or it has already been removed) then the method call has no effect.
     * <p>
     * After removing the blocker, if there are no more blockers left in the blocking set, the workbench UI is
     * bootstrapped immediately. If there are still one or more blockers left in the blocking set, the workbench UI
     * remains uninitialized.
     * 
     * @param responsibleParty
     *            any Class object that was previously passed to {@link #addStartupBlocker(Class)}.
     *            Must not be null.
     */
    public void removeStartupBlocker( Class<?> responsibleParty ) {
        if (startupBlockers.remove( responsibleParty ) ) {
            System.out.println( responsibleParty.getName() + " is no longer blocking startup." );
        } else {
            System.out.println( responsibleParty.getName() + " tried to unblock startup, but it wasn't blocking to begin with!");
        }

        if ( startupBlockers.isEmpty() ) {
            bootstrap();
        }
    }

    // package-private so tests can call in
    @AfterInitialization
    void startIfNotBlocked() {
        System.out.println(startupBlockers.size() + " workbench startup blockers remain.");
        if ( startupBlockers.isEmpty() ) {
            bootstrap();
        }
    }

    @PostConstruct
    public void setup() {

        layout = layoutSelection.get();

        isStandaloneMode = Window.Location.getParameterMap().containsKey( "standalone" );

        for ( final Map.Entry<String, List<String>> parameter : Window.Location.getParameterMap().entrySet() ) {
            if ( parameter.getKey().equals( "header" ) ) {
                headersToKeep.addAll( parameter.getValue() );
            }
        }
    }

    private <T> void setupMarginWidgets( Class<T> marginType ) {
        final Collection<IOCBeanDef<T>> headerBeans = iocManager.lookupBeans( marginType );
        final List<OrderableIsWidget> instances = new ArrayList<OrderableIsWidget>();
        for ( final IOCBeanDef<T> headerBean : headerBeans ) {
            OrderableIsWidget instance = (OrderableIsWidget)headerBean.getInstance();

            // for regular mode (not standalone) we add every header and footer widget;
            // for standalone mode, we only add the ones requested in the URL
            if ( (!isStandaloneMode) || headersToKeep.contains( instance.getId() ) ) {
                instances.add( instance );
            }
        }
        sort( instances, new Comparator<OrderableIsWidget>() {
            @Override
            public int compare( final OrderableIsWidget o1,
                                final OrderableIsWidget o2 ) {
                if ( o1.getOrder() < o2.getOrder() ) {
                    return 1;
                } else if ( o1.getOrder() > o2.getOrder() ) {
                    return -1;
                } else {
                    return 0;
                }
            }
        } );

        FlowPanel marginContainer = new FlowPanel();

        for ( final OrderableIsWidget widget : instances ) {
            layout.addMargin( marginType, widget.asWidget() );
        }

    }

    public IsWidget getLayoutRoot() {
        return layout.getRoot();
    }

    private void bootstrap() {
        System.out.println("Workbench starting...");
        appReady.fire( new ApplicationReadyEvent() );

        if(!isStandaloneMode) {
            setupMarginWidgets(Header.class);
            setupMarginWidgets(Footer.class);
        }

        // bootstrap the actual layout

        layout.onBootstrap();


        //Size environment - Defer so Widgets have been rendered and hence sizes available
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {

            @Override
            public void execute() {
                final int width = Window.getClientWidth();
                final int height = Window.getClientHeight();
                layout.resizeTo(width, height);
            }

        } );

        //Lookup PerspectiveProviders and if present launch it to set-up the Workbench
        if ( !isStandaloneMode ) {
            final PerspectiveActivity defaultPerspective = getDefaultPerspectiveActivity();
            if ( defaultPerspective != null ) {
                placeManager.goTo( new DefaultPlaceRequest( defaultPerspective.getIdentifier() ) );
            }
        } else {
            handleStandaloneMode( Window.Location.getParameterMap() );
        }

        //Save Workbench state when Window is closed
        Window.addWindowClosingHandler( new ClosingHandler() {

            @Override
            public void onWindowClosing( ClosingEvent event ) {
                workbenchCloseHandler.onWindowClose( workbenchCloseCommand );
            }

        } );

        //Resizing the Window should resize everything
        Window.addResizeHandler( new ResizeHandler() {
            @Override
            public void onResize( ResizeEvent event ) {
                layout.resizeTo(event.getWidth(), event.getHeight());
            }
        } );

        Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                layout.onResize();
            }
        } );

    }

    private void handleStandaloneMode( final Map<String, List<String>> parameters ) {
        if ( parameters.containsKey( "perspective" ) && !parameters.get( "perspective" ).isEmpty() ) {
            placeManager.goTo( new DefaultPlaceRequest( parameters.get( "perspective" ).get( 0 ) ) );
        } else if ( parameters.containsKey( "path" ) && !parameters.get( "path" ).isEmpty() ) {
            placeManager.goTo( new DefaultPlaceRequest( "StandaloneEditorPerspective" ) );
            vfsService.get( parameters.get( "path" ).get( 0 ), new ParameterizedCommand<Path>() {
                @Override
                public void execute( final Path response ) {
                    if ( parameters.containsKey( "editor" ) && !parameters.get( "editor" ).isEmpty() ) {
                        placeManager.goTo( new PathPlaceRequest( response, parameters.get( "editor" ).get( 0 ) ) );
                    } else {
                        placeManager.goTo( new PathPlaceRequest( response ) );
                    }
                }
            } );
        }
    }

    private PerspectiveActivity getDefaultPerspectiveActivity() {
        PerspectiveActivity defaultPerspective = null;
        final Collection<IOCBeanDef<PerspectiveActivity>> perspectives = iocManager.lookupBeans(PerspectiveActivity.class);

        for ( final IOCBeanDef<PerspectiveActivity> perspective : perspectives ) {
            final PerspectiveActivity instance = perspective.getInstance();
            if ( instance.isDefault() ) {
                defaultPerspective = instance;
            } else {
                iocManager.destroyBean( instance );
            }
        }
        return defaultPerspective;
    }
}
