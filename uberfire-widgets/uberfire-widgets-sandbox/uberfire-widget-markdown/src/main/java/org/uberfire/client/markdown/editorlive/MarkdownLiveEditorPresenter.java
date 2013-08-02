/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.client.markdown.editorlive;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@Dependent
@WorkbenchScreen(identifier = "MarkdownLiveEditor")
public class MarkdownLiveEditorPresenter {

    public interface View
            extends
            IsWidget {

        void setContent(String content);
    }

    @Inject
    public View view;

    @OnStartup
    public void onStartup() {
        view.setContent("");
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Markdown Live Editor";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

}