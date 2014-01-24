package org.uberfire.client.mvp;

import org.uberfire.client.workbench.PanelManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;

public class PlaceManagerImplFake extends PlaceManagerImpl {

    private final Activity activity;
    private final PanelManager panelManagerFake;

    public PlaceManagerImplFake(Activity activity, PanelManager panelManagerFake) {
        this.activity = activity;
        this.panelManagerFake = panelManagerFake;


    }

    @Override public Activity getActivity(PlaceRequest place) {
        return activity;
    }

    PanelDefinition addWorkbenchPanelTo(Position position) {
        return null;
    }

    public void updateHistory( PlaceRequest request ) {

    }

    SplashScreenActivity getSplashScreenInterceptor(PlaceRequest place) {
        return null;
    }



}
