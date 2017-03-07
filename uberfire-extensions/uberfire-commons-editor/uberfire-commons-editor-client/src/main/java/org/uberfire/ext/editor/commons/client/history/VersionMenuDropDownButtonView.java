/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.editor.commons.client.history;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mvp.Command;

public interface VersionMenuDropDownButtonView
        extends IsWidget,
                HasEnabled {

    void setPresenter(final Presenter presenter);

    void clear();

    void setTextToLatest();

    void setTextToVersion(final int versionIndex);

    void addLabel(final VersionRecord versionRecord,
                  final boolean isSelected,
                  final int versionIndex);

    void addViewAllLabel(final int index,
                         final Command command);

    interface Presenter {

        void onVersionRecordSelected(VersionRecord result);

        void onMenuOpening();

        /**
         * Clears the version and versions from the Presenter's internal state. This is required if
         * you want to reinitialise the VersionRecordManager with history for a different Path to
         * which it was originally initialised for.
         */
        void resetVersions();
    }
}
