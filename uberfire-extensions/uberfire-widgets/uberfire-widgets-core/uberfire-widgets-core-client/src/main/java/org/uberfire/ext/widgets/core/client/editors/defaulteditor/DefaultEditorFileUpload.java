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

package org.uberfire.ext.widgets.core.client.editors.defaulteditor;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants;

public class DefaultEditorFileUpload
        extends DefaultEditorFileUploadBase {

    private Path path;

    @Override
    protected Map<String, String> getParameters() {
        HashMap<String, String> parameters = new HashMap<String, String>();

        parameters.put("path",
                       path.toURI());

        return parameters;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void download() {

        Window.open(getFileDownloadURL(),
                    CoreConstants.INSTANCE.Downloading(),
                    "resizable=no,scrollbars=yes,status=no");
    }

    private String getFileDownloadURL() {
        return GWT.getModuleBaseURL() + "defaulteditor/download?path=" + path.toURI();
    }
}
