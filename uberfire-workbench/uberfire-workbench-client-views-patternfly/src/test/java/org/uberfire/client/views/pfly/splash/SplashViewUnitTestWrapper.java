package org.uberfire.client.views.pfly.splash;

import static org.mockito.Mockito.*;

import org.uberfire.client.views.pfly.modal.Bs3Modal;
import org.uberfire.client.views.pfly.splash.SplashViewImpl;

public class SplashViewUnitTestWrapper extends SplashViewImpl {

    private Bs3Modal mock;

    @Override
    Bs3Modal getModal() {
        if ( mock == null ) {
            mock = mock( Bs3Modal.class );
        }
        return mock;
    }

    void cleanup() {
    }

}
