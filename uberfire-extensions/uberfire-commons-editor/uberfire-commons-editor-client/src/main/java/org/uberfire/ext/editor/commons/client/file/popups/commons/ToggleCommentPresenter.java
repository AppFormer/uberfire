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

package org.uberfire.ext.editor.commons.client.file.popups.commons;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Element;
import org.uberfire.client.mvp.UberElement;

@Dependent
public class ToggleCommentPresenter {

    private View view;

    @Inject
    public ToggleCommentPresenter(View view) {
        this.view = view;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
    }

    public Element getViewElement() {
        return view.getElement();
    }

    public String getComment() {
        return view.getComment();
    }

    public void setHidden(final boolean hidden) {
        view.getElement().setHidden(hidden);
    }

    public interface View extends UberElement<ToggleCommentPresenter> {

        String getComment();
    }
}
