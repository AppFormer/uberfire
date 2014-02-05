package org.uberfire.client.mvp;

import java.util.HashSet;

import com.google.gwt.event.shared.EventBus;
import org.junit.Test;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.Position;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;

public class PlaceManagerImplTest extends BaseWorkbenchTest {

    @Test
    public void testGoToSomeWhere() throws Exception {
        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );
        final WorkbenchEditorActivity activity = mock( WorkbenchEditorActivity.class );
        HashSet<Activity> activities = new HashSet<Activity>( 1 ) {{
            add( activity );
        }};

        when( activity.getDefaultPosition() ).thenReturn( Position.ROOT );
        when( activityManager.getActivities( somewhere ) ).thenReturn( activities );

        placeManager = new PlaceManagerImplUnitTestWrapper( activity, panelManager );

        placeManager.goTo( somewhere );

        verify( activity ).launch( any( AcceptItem.class ),
                                   eq( somewhere ),
                                   isNull( Command.class ) );

    }

    @Test
    //Test going no where doesn't throw any errors
    public void testGoToNoWhere() throws Exception {
        placeManager.goTo( DefaultPlaceRequest.NOWHERE );

        assertTrue( "Just checking we get no NPEs",
                    true );
    }

    @Test
    public void testPlaceManagerGetInitializedToADefaultPlace() throws Exception {
        placeManager = new PlaceManagerImplUnitTestWrapper( placeHistoryHandler );

        placeManager.initPlaceHistoryHandler();

        verify( placeHistoryHandler ).register( any( PlaceManager.class ),
                                                any( EventBus.class ),
                                                any( PlaceRequest.class ) );
    }

    @Test
    //Test PlaceManager only calls an Activities launch method once if re-visited
    public void testGoToPreviouslyOpenedPlace() throws Exception {
        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );

        final WorkbenchScreenActivity activity = new MockWorkbenchScreenActivity( placeManager );
        final WorkbenchScreenActivity spy = spy( activity );
        when( activityManager.getActivities( somewhere ) ).thenReturn( new HashSet<Activity>( 1 ) {{
            add( spy );
        }} );

        placeManager = new PlaceManagerImplUnitTestWrapper( spy, panelManager, selectWorkbenchPartEvent );

        placeManager.goTo( somewhere, panelManager.getRoot() );

        verify( spy,
                times( 1 ) ).launch( any( AcceptItem.class ),
                                     eq( somewhere ),
                                     isNull( Command.class ) );
        verify( selectWorkbenchPartEvent,
                times( 1 ) ).fire( any( SelectPlaceEvent.class ) );

        PlaceRequest somewhereSecondCall = new DefaultPlaceRequest( "Somewhere" );
        placeManager.goTo( somewhereSecondCall, panelManager.getRoot() );

        verify( spy,
                times( 1 ) ).launch( any( AcceptItem.class ),
                                     eq( somewhere ),
                                     isNull( Command.class ) );
        verify( selectWorkbenchPartEvent,
                times( 2 ) ).fire( any( SelectPlaceEvent.class ) );
    }

}
