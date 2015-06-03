package org.uberfire.client.docks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@ApplicationScoped
public class UberfireDocksImpl implements UberfireDocks {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private SyncBeanManager iocManager;

    private DockLayoutPanel rootContainer;

    DocksBar southCollapsed;
    DocksExpandedBar southExpanded;

    DocksBar westCollapsed;
    DocksExpandedBar westExpanded;

    DocksBar eastCollapsed;
    DocksExpandedBar eastExpanded;

    Map<String, Set<UberfireDock>> docksPerPerspective = new HashMap<String, Set<UberfireDock>>();

    Set<UberfireDock> avaliableDocks = new HashSet<UberfireDock>();

    @PostConstruct
    public void init() {
        createSouthDock();
        createEastDock();
        createWestDock();
    }

    @Override
    public void setup( DockLayoutPanel rootContainer ) {
        this.rootContainer = rootContainer;
        //layoutPanel Has To Have At Least One Displayed Component
        rootContainer.addSouth( new FlowPanel(), 1 );

        rootContainer.addSouth( southCollapsed, southCollapsed.widgetSize() );
        rootContainer.addSouth( southExpanded, southExpanded.widgetSize() );

        rootContainer.addWest( new FlowPanel(), 1 );
        rootContainer.addWest( westCollapsed, westCollapsed.widgetSize() );
        rootContainer.addWest( westExpanded, westExpanded.widgetSize() );

        rootContainer.addEast( new FlowPanel(), 1 );
        rootContainer.addEast( eastCollapsed, eastCollapsed.widgetSize() );
        rootContainer.addEast( eastExpanded, eastExpanded.widgetSize() );

        collapseAll();
    }

    @Override
    public void register( UberfireDock... docks ) {
        for ( UberfireDock dock : docks ) {
            avaliableDocks.add( dock );
            if ( dock.getAssociatedPerspective() != null ) {
                Set<UberfireDock> uberfireDocks = docksPerPerspective.get( dock.getAssociatedPerspective() );
                if ( uberfireDocks == null ) {
                    uberfireDocks = new HashSet<UberfireDock>();
                }
                uberfireDocks.add( dock );
                docksPerPerspective.put( dock.getAssociatedPerspective(), uberfireDocks );
            }
        }
        updateAvaliableDocksMenu();
    }

    private void updateAvaliableDocksMenu() {
        southCollapsed.updateAvaliableDocksMenu( avaliableDocks, createOpenDockLink() );
    }

    public void perspectiveChangeEvent( @Observes PerspectiveChange perspectiveChange ) {
        clearAndCollapseAllDocks();
        updateDockContent( perspectiveChange );
    }

    private void clearAndCollapseAllDocks() {
        collapseAll();
        southCollapsed.clearDocks();
        westCollapsed.clearDocks();
        eastCollapsed.clearDocks();
    }

    private void updateDockContent( PerspectiveChange perspectiveChange ) {
        Set<UberfireDock> docks = docksPerPerspective.get( perspectiveChange.getIdentifier() );

        if ( docks != null && !docks.isEmpty() ) {
            for ( UberfireDock dock : docks ) {
                DocksBar docksBar = resolveDockBar( dock.getDockPosition().name() );
                DocksExpandedBar docksExpandedBar = resolveDockExpandedBar( dock.getDockPosition().name() );
                if ( docksBar != null ) {
                    docksBar.addDock( dock, createDockSelectCommand( docksBar, docksExpandedBar ), createDockDeselectCommand( docksBar, docksExpandedBar ) );
                }
            }
            expandAllCollapsed();
        }
    }

    private void createEastDock() {
        eastExpanded = new DocksExpandedBar( UberfireDockPosition.EAST );
        eastCollapsed = new DocksBar( UberfireDockPosition.EAST, createDropHandler( UberfireDockPosition.EAST ) );
    }

    private void createWestDock() {
        westExpanded = new DocksExpandedBar( UberfireDockPosition.WEST );
        westCollapsed = new DocksBar( UberfireDockPosition.WEST, createDropHandler( UberfireDockPosition.WEST ) );
    }

    private void createSouthDock() {
        southExpanded = new DocksExpandedBar( UberfireDockPosition.SOUTH );
        southCollapsed = new DocksBar( UberfireDockPosition.SOUTH, createDropHandler( UberfireDockPosition.SOUTH ) );
    }

    private ParameterizedCommand<String> createDropHandler( final UberfireDockPosition targetDock ) {
        return new ParameterizedCommand<String>() {
            @Override
            public void execute( String dropName ) {
                UberfireDock dock = searchForDockByDockItemName( dropName );
                if ( dock != null ) {
                    moveDock( dock, targetDock );
                }
            }
        };
    }

    void moveDock( UberfireDock dock,
                   UberfireDockPosition targetDock ) {
        DocksBar oldDockBar = resolveDockBar( dock.getDockPosition().name() );
        oldDockBar.removeDock( dock );

        dock.setUberfireDockPosition( targetDock );
        avaliableDocks.add( dock );

        if ( dock.getAssociatedPerspective() != null ) {
            Set<UberfireDock> uberfireDocks = docksPerPerspective.get( dock.getAssociatedPerspective() );
            uberfireDocks.add( dock );
        }

        DocksBar targetDockBar = resolveDockBar( targetDock.name() );
        DocksExpandedBar targetExpandedDockBar = resolveDockExpandedBar( targetDock.name() );
        targetDockBar.addDock( dock, createDockSelectCommand( targetDockBar, targetExpandedDockBar ), createDockDeselectCommand( targetDockBar, targetExpandedDockBar ) );
    }

    private ParameterizedCommand<String> createDockSelectCommand( final DocksBar dockBar,
                                                                  final DocksExpandedBar dockExpandedBar ) {
        return new ParameterizedCommand<String>() {
            @Override
            public void execute( String clickDockName ) {
                UberfireDock targetDock = searchForDockByDockItemName( clickDockName );
                if ( targetDock != null ) {
                    selectDock( targetDock, dockBar, dockExpandedBar );
                }
            }
        };
    }

    private void selectDock( UberfireDock targetDock,
                             DocksBar dockBar,
                             DocksExpandedBar dockExpandedBar ) {
        dockBar.setDockSelected( targetDock.getIdentifier() );
        dockExpandedBar.clear();
        expandPanel( dockExpandedBar );
        //https://issues.jboss.org/browse/UF-185
        dockExpandedBar.setPanelSize( 1000, 1000 );
        dockExpandedBar.setup( targetDock.getIdentifier(), createDockDeselectCommand( dockBar, dockExpandedBar ) );
        placeManager.goTo( new DefaultPlaceRequest( targetDock.getIdentifier() ), dockExpandedBar.targetPanel() );
    }

    private ParameterizedCommand<String> createDockDeselectCommand( final DocksBar dockBar,
                                                                    final DocksExpandedBar dockExpandedBar ) {
        return new ParameterizedCommand<String>() {
            @Override
            public void execute( String clickDockName ) {
                UberfireDock targetDock = searchForDockByDockItemName( clickDockName );
                if ( targetDock != null ) {
                    deselectDock( dockBar, dockExpandedBar );
                }
            }
        };
    }

    private void deselectDock( DocksBar dockBar,
                               DocksExpandedBar dockExpandedBar ) {
        dockBar.deselectAllDocks();
        dockExpandedBar.clear();
        collapse( dockExpandedBar );
    }

    private ParameterizedCommand<String> createOpenDockLink() {
        return new ParameterizedCommand<String>() {
            @Override
            public void execute( String clickDockName ) {
                UberfireDock targetDock = searchForDockByDockItemName( clickDockName );
                if ( targetDock != null ) {
                    DocksBar dockBar = resolveDockBar( targetDock.getDockPosition().name() );
                    DocksExpandedBar dockExpandedBar = resolveDockExpandedBar( targetDock.getDockPosition().name() );
                    if ( dockBar != null && dockExpandedBar != null ) {
                        selectDock( targetDock, dockBar, dockExpandedBar );
                    }
                }
            }
        };
    }

    private DocksExpandedBar resolveDockExpandedBar( String dockPosition ) {
        if ( dockPosition.equalsIgnoreCase( UberfireDockPosition.SOUTH.name() ) ) {
            return southExpanded;
        } else if ( dockPosition.equalsIgnoreCase( UberfireDockPosition.WEST.name() ) ) {
            return westExpanded;
        } else {
            return eastExpanded;
        }
    }

    private DocksBar resolveDockBar( String dockPosition ) {
        if ( dockPosition.equalsIgnoreCase( UberfireDockPosition.SOUTH.name() ) ) {
            return southCollapsed;
        } else if ( dockPosition.equalsIgnoreCase( UberfireDockPosition.WEST.name() ) ) {
            return westCollapsed;
        } else {
            return eastCollapsed;
        }
    }

    UberfireDock searchForDockByDockItemName( String clickDockName ) {
        UberfireDock targetDock = null;
        for ( UberfireDock avaliableDock : avaliableDocks ) {
            if ( avaliableDock.getIdentifier().equalsIgnoreCase( clickDockName ) ) {
                targetDock = avaliableDock;
            }
        }
        return targetDock;
    }

    private void collapseAll() {
        collapse( southExpanded );
        collapse( eastExpanded );
        collapse( westExpanded );
        collapse( southCollapsed );
        collapse( eastCollapsed );
        collapse( westCollapsed );
    }

    private void expandAllCollapsed() {
        expandPanel( eastCollapsed );
        expandPanel( westCollapsed );
        expandPanel( southCollapsed );
    }

    private void collapse( DocksBar dock ) {
        rootContainer.setWidgetHidden( dock, true );
    }

    private void expandPanel( DocksBar dock ) {
        rootContainer.setWidgetHidden( dock, false );
    }

    private void collapse( DocksExpandedBar dock ) {
        dock.clear();
        rootContainer.setWidgetHidden( dock, true );
    }

    private void expandPanel( DocksExpandedBar dock ) {
        rootContainer.setWidgetHidden( dock, false );
    }

}
