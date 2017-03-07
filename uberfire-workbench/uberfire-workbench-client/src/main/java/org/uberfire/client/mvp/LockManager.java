/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.mvp;

import jsinterop.annotations.JsType;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;

/**
 * Provides functionality to lock a file or directory, associated with a widget
 * (i.e a workbench screen or editor).
 */
@JsType
public interface LockManager {

    /**
     * Retrieves the latest lock information for the provided target and fires
     * events to update the corresponding UI.
     * @param lockTarget the {@link LockTarget} providing information about what to
     * lock.
     */
    void init(LockTarget lockTarget);

    /**
     * Notifies this lock manager that the lock target's widget got focus to
     * initialize widget-specific state i.e. to publish JavaScript methods for
     * lock management which can be used by non-native editors (i.e editors that
     * are rendered on the server). The lock manager must be initialized before
     * calling this method (see {@link #init(LockTarget)}).
     */
    void onFocus();

    /**
     * Attempts to acquire a lock where the consuming code knows a lock needs to
     * be acquired. If the target is already locked by another user and the lock
     * cannot be acquired, the user will be notified and the lock target's reload
     * runnable will be executed. Attempts to acquire a lock will always cause
     * an {@link ChangeTitleWidgetEvent} to be fired. Errors in the execution of
     * this method are propagated to the global RPC/MessageBus error handler.
     * The lock manager must be initialized before calling this method (see
     * {@link #init(LockTarget)}).
     */
    void acquireLock();

    /**
     * Registers DOM handlers to detect changes on
     * {@link LockTarget#getWidget()} and, if required (see
     * {@link LockDemandDetector}), automatically tries to acquire a lock. If
     * the target is already locked by another user and the lock can't be
     * acquired, the user will be notified and the lock target's reload runnable
     * will be executed. Errors in the execution of this method are propagated
     * to the global RPC/MessageBus error handler. The lock manager must be
     * initialized before calling this method (see {@link #init(LockTarget)}).
     */
    void acquireLockOnDemand();

    /**
     * Releases the previously acquired lock. Errors in the execution of this
     * method are propagated to the global RPC/MessageBus error handler.
     */
    void releaseLock();
}
