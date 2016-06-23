/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.widgets.common.client.htmleditor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.mvp.UberView;

@Dependent
public class HtmlEditorPresenter {

    public interface View extends UberView<HtmlEditorPresenter>,
                                  /* Added temporally due to compatibility with existing components */
                                  IsWidget {

        void setContent( String content );

        String getContent();

        void initialize();
    }

    private final View view;

    private boolean editorInitialized = false;

    @Inject
    public HtmlEditorPresenter( final View view ) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public void show() {
        if ( !editorInitialized ) {
            view.initialize();

            editorInitialized = true;
        }
    }

    public View getView() {
        return view;
    }

    public String getContent() {
        return view.getContent();
    }

    public void setContent( final String content ) {
        view.setContent( content );
    }
}
