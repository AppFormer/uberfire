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

import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class HtmlEditorView extends Composite
        implements HtmlEditorPresenter.View {

    private HtmlEditorPresenter presenter;

    private HtmlEditorLibraryLoader libraryLoader;

    @Inject
    @DataField("html-editor")
    Div htmlEditor;

    @Inject
    @DataField("html-editor-toolbar")
    Div toolbar;

    @Inject
    public HtmlEditorView( final HtmlEditorLibraryLoader libraryLoader ) {
        super();
        this.libraryLoader = libraryLoader;
    }

    @Override
    public void init( final HtmlEditorPresenter presenter ) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void postConstruct() {
        libraryLoader.ensureLibrariesAreAvailable();
    }

    @Override
    public void initialize() {
        final String identifier = String.valueOf( System.currentTimeMillis() );

        final String editorId = "html-editor-" + identifier;
        final String toolbarId = "html-editor-toolbar-" + identifier;

        htmlEditor.setId( editorId );
        toolbar.setId( toolbarId );

        initEditor( editorId, toolbarId );
    }

    @Override
    public void setContent( final String content ) {
        htmlEditor.setInnerHTML( content );
    }

    @Override
    public String getContent() {
        return htmlEditor.getInnerHTML();
    }

    public native void initEditor( String editorId, String toolbarId ) /*-{
        var editor = new $wnd.wysihtml.Editor( editorId, {
            toolbar : $wnd.document.getElementById( toolbarId ),
            parserRules : $wnd.wysihtmlParserRules
        } );
    }-*/;
}
