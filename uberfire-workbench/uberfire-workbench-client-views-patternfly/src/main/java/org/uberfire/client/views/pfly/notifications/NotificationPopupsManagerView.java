package org.uberfire.client.views.pfly.notifications;

import java.util.ArrayList;
import java.util.List;

import org.uberfire.client.workbench.widgets.animations.LinearFadeOutAnimation;
import org.uberfire.client.workbench.widgets.notifications.NotificationManager;
import org.uberfire.client.workbench.widgets.notifications.NotificationManager.NotificationPopupHandle;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.workbench.events.NotificationEvent.NotificationType;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;


public class NotificationPopupsManagerView implements NotificationManager.View {

    //When true we are in the process of removing a notification message
    private boolean removing = false;

    private final int SPACING = 48;

    private final List<PopupHandle> activeNotifications = new ArrayList<PopupHandle>();

    private final List<PopupHandle> pendingRemovals = new ArrayList<PopupHandle>();

    private static class PopupHandle implements NotificationPopupHandle {
        final NotificationPopupView view;

        PopupHandle( NotificationPopupView view ) {
            this.view = PortablePreconditions.checkNotNull( "view", view );
        }
    }

    @Override
    public NotificationPopupHandle show( NotificationType type,
                                         String message,
                                         Command hideCommand ) {
        final NotificationPopupView view = new NotificationPopupView();
        final PopupHandle popupHandle = new PopupHandle( view );
        activeNotifications.add( popupHandle );
        view.setPopupPosition( getMargin(),
                               activeNotifications.size() * SPACING );
        view.setNotification( message );
        view.setType( type );
        view.setNotificationWidth( getWidth() + "px" );
        view.show( hideCommand );
        return popupHandle;
    }

    @Override
    public void hide( final NotificationPopupHandle handle ) {
        final int removingIndex = activeNotifications.indexOf( handle );
        if ( removingIndex == -1 ) {
            return;
        }
        if ( removing ) {
            pendingRemovals.add((PopupHandle) handle);
            return;
        }
        removing = true;
        final NotificationPopupView view = ((PopupHandle) handle).view;
        final LinearFadeOutAnimation fadeOutAnimation = new LinearFadeOutAnimation( view ) {
            @Override
            public void onUpdate( double progress ) {
                super.onUpdate( progress );
                for ( int i = removingIndex; i < activeNotifications.size(); i++ ) {
                    NotificationPopupView v = activeNotifications.get( i ).view;
                    final int left = v.getPopupLeft();
                    final int top = (int) ( ( ( i + 1 ) * SPACING ) - ( progress * SPACING ) );
                    v.setPopupPosition( left,
                                        top );
                }
            }

            @Override
            public void onComplete() {
                super.onComplete();
                view.hide();
                activeNotifications.remove( handle );
                removing = false;
                if ( pendingRemovals.size() > 0 ) {
                    PopupHandle popupHandle = pendingRemovals.remove( 0 );
                    hide( popupHandle );
                }
            }

        };
        fadeOutAnimation.run( 500 );
    }

    //80% of screen width
    private int getWidth() {
        return (int) ( Window.getClientWidth() * 0.8 );
    }

    //10% of screen width
    private int getMargin() {
        return ( Window.getClientWidth() - getWidth() ) / 2;
    }

}
