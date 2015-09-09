package org.uberfire.client.mvp;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.backend.vfs.impl.LockResult;
import org.uberfire.client.resources.i18n.WorkbenchConstants;
import org.uberfire.client.workbench.VFSLockServiceProxy;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.UIObject;

/**
 * Default implementation of {@link LockManager} using the
 * {@link VFSLockServiceProxy} for lock management.
 */
@Dependent
public class LockManagerImpl implements LockManager {

    @Inject
    private VFSLockServiceProxy lockService;

    @Inject
    private javax.enterprise.event.Event<ChangeTitleWidgetEvent> changeTitleEvent;

    @Inject
    private javax.enterprise.event.Event<UpdatedLockStatusEvent> updatedLockStatusEvent;

    @Inject
    private javax.enterprise.event.Event<NotificationEvent> lockNotification;

    @Inject 
    private LockDemandDetector lockDemandDetector;
    
    @Inject
    private User user;

    private LockTarget lockTarget;

    private LockInfo lockInfo = LockInfo.unlocked();
    private HandlerRegistration closeHandler;

    private boolean lockRequestPending;
    private boolean unlockRequestPending;

    private boolean lockSyncComplete;
    private List<Runnable> syncCompleteRunnables = new ArrayList<Runnable>();

    private Timer reloadTimer;

    @Override
    public void init( final LockTarget lockTarget ) {
        this.lockTarget = lockTarget;

        final ParameterizedCommand<LockInfo> command = new ParameterizedCommand<LockInfo>() {

            @Override
            public void execute( final LockInfo lockInfo ) {
                if ( !lockRequestPending && !unlockRequestPending ) {
                    updateLockInfo( lockInfo );
                }
            }
        };
        lockService.retrieveLockInfo( lockTarget.getPath(),
                                      command );
    }

    @Override
    public void onFocus() {
        publishJsApi();
        fireChangeTitleEvent();
        fireUpdatedLockStatusEvent();
    }
    
    @Override
    public void acquireLockOnDemand() {
        if ( lockTarget == null )
            return;

        final Element element = lockTarget.getWidget().getElement();
        acquireLockOnDemand( element );

        lockTarget.getWidget().addAttachHandler( new AttachEvent.Handler() {

            @Override
            public void onAttachOrDetach( AttachEvent event ) {
                // Handle widget reattachment/reparenting 
                if ( event.isAttached() ) {
                    acquireLockOnDemand( element );
                }
            }
        } );
    }
    
    public EventListener acquireLockOnDemand(final Element element) {
        Event.sinkEvents( element,
                          lockDemandDetector.getLockDemandEventTypes() );

        EventListener lockDemandListener = new EventListener() {

            @Override
            public void onBrowserEvent( Event event ) {
                if ( isLockedByCurrentUser() ) {
                    return;
                }
                
                if ( lockDemandDetector.isLockRequired( event ) ) {
                    acquireLock();
                }
            }
        };

        Event.setEventListener( element,
                                lockDemandListener );
        
        
        return lockDemandListener;
    }

    private void acquireLock() {
        if ( lockInfo.isLocked() ) {
            handleLockFailure(lockInfo);
        } 
        else if ( !lockRequestPending ) {
            lockRequestPending = true;
            final ParameterizedCommand<LockResult> command = new ParameterizedCommand<LockResult>() {

                @Override
                public void execute( final LockResult result ) {
                    if ( result.isSuccess() ) {
                        updateLockInfo( result.getLockInfo() );
                        releaseLockOnClose();
                    } 
                    else {
                        handleLockFailure(result.getLockInfo());
                    }
                    lockRequestPending = false;
                }
            };
            lockService.acquireLock( lockTarget.getPath(),
                                     command );
        }
    }

    @Override
    public void releaseLock() {
        final Runnable releaseLock = new Runnable() {

            @Override
            public void run() {
                releaseLockInternal();
            }
        };
        if ( lockSyncComplete ) {
            releaseLock.run();
        } 
        else {
            syncCompleteRunnables.add( releaseLock );
        }
    }

    private void releaseLockInternal() {
        if ( isLockedByCurrentUser() && !unlockRequestPending ) {
            unlockRequestPending = true;

            ParameterizedCommand<LockResult> command = new ParameterizedCommand<LockResult>() {

                @Override
                public void execute( final LockResult result ) {
                    updateLockInfo( result.getLockInfo() );

                    if ( result.isSuccess() ) {
                        if ( closeHandler != null ) {
                            closeHandler.removeHandler();
                        }
                    }

                    unlockRequestPending = false;
                }
            };
            lockService.releaseLock( lockTarget.getPath(),
                                     command );
        }
    }

    private void releaseLockOnClose() {
        closeHandler = Window.addWindowClosingHandler( new ClosingHandler() {
            @Override
            public void onWindowClosing( ClosingEvent event ) {
                releaseLock();
            }
        });
    }

    private void handleLockFailure(final LockInfo lockInfo) {
        
        if ( lockInfo != null ) {
            updateLockInfo( lockInfo );
            lockNotification.fire( new NotificationEvent( WorkbenchConstants.INSTANCE.lockedMessage( lockInfo.lockedBy() ),
                                                          NotificationEvent.NotificationType.INFO,
                                                          true,
                                                          lockTarget.getPlace(),
                                                          20 ) );
        }
        else {
            lockNotification.fire( new NotificationEvent( WorkbenchConstants.INSTANCE.lockError(),
                                                          NotificationEvent.NotificationType.ERROR,
                                                          true,
                                                          lockTarget.getPlace(),
                                                          20 ) );
        }
        // Delay reloading slightly in case we're dealing with a flood of events
        if ( reloadTimer == null ) {
            reloadTimer = new Timer() {

                public void run() {
                    reload();
                }
            };
        }

        if ( !reloadTimer.isRunning() ) {
            reloadTimer.schedule( 250 );
        }
    }
    
    private void reload() {
        lockTarget.getReloadRunnable().run();
    }

    private boolean isLockedByCurrentUser() {
        return lockInfo.isLocked() && lockInfo.lockedBy().equals( user.getIdentifier() );
    }

    private void updateLockInfo( @Observes LockInfo lockInfo ) {
        if ( lockInfo.getFile().equals( lockTarget.getPath() ) ) {
            this.lockInfo = lockInfo;
            this.lockSyncComplete = true;

            fireChangeTitleEvent();
            fireUpdatedLockStatusEvent();
            
            for ( Runnable runnable : syncCompleteRunnables ) {
                runnable.run();
            }
            syncCompleteRunnables.clear();
        }
    }

    void onResourceAdded( @Observes ResourceAddedEvent res ) {
        if ( lockTarget != null && res.getPath().equals( lockTarget.getPath() ) ) {
            releaseLock();
        }
    }

    void onResourceUpdated( @Observes ResourceUpdatedEvent res ) {
        if ( lockTarget != null && res.getPath().equals( lockTarget.getPath() ) ) {
            if ( !res.getSessionInfo().getIdentity().equals( user ) ) {
                reload();
            }
            releaseLock();
        }
    }
    
    void onSaveInProgress( @Observes SaveInProgressEvent evt ) {
        if ( lockTarget != null && evt.getPath().equals( lockTarget.getPath() ) ) {
            releaseLock();
        }
    }
    
    void onLockRequired( @Observes LockRequiredEvent event ) {
        if ( isVisible() && !isLockedByCurrentUser() ) {
            acquireLock();
        }
    }

    private native void publishJsApi()/*-{
        var lockManager = this;
        $wnd.isLocked = function () {
            return lockManager.@org.uberfire.client.mvp.LockManagerImpl::isLocked()();
        }
        $wnd.isLockedByCurrentUser = function () {
            return lockManager.@org.uberfire.client.mvp.LockManagerImpl::isLockedByCurrentUser()();
        }
        $wnd.acquireLock = function () {
            lockManager.@org.uberfire.client.mvp.LockManagerImpl::acquireLock()();
        }
        $wnd.releaseLock = function () {
            lockManager.@org.uberfire.client.mvp.LockManagerImpl::releaseLock()();
        }
        $wnd.reload = function () {
            return lockManager.@org.uberfire.client.mvp.LockManagerImpl::reload()();
        }
    }-*/;

    private boolean isLocked() {
        return lockInfo.isLocked();
    }
    
    private void fireChangeTitleEvent() {
        changeTitleEvent.fire( LockTitleWidgetEvent.create( lockTarget,
                                                            lockInfo,
                                                            user) );
    }
    
    private void fireUpdatedLockStatusEvent() {
        if ( isVisible() ) {
            updatedLockStatusEvent.fire( new UpdatedLockStatusEvent( lockInfo.isLocked(),
                                                                     isLockedByCurrentUser() ) );
        }
    }
    
    private boolean isVisible() {
        Element element = lockTarget.getWidget().getElement();
        boolean visible = UIObject.isVisible( element ) && 
                (element.getAbsoluteLeft() != 0) && (element.getAbsoluteTop() != 0);

        return visible;
    }
}