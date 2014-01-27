package org.uberfire.client.mvp;

import java.util.HashSet;

import org.junit.Test;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.Position;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class AbstractPopupActivityTest  extends BaseWorkbenchTest {


    @Test
    public void testPopUpLaunch() throws Exception {

        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );

        final AbstractPopupActivity activity = mock( AbstractPopupActivity.class );
        HashSet<Activity> activities = new HashSet<Activity>( 1 ) {{
            add( activity );
        }};

        when( activityManager.getActivities( somewhere ) ).thenReturn( activities );

        placeManager = new PlaceManagerImplUnitTestWrapper( activity, panelManager );

        placeManager.goTo( somewhere );

        verify( activity ).launch( eq( somewhere ), any(Command.class));

    }

}
