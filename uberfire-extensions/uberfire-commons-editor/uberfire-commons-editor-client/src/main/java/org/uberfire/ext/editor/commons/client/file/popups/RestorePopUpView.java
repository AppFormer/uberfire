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

package org.uberfire.ext.editor.commons.client.file.popups;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentPresenter;
import org.uberfire.ext.editor.commons.client.resources.i18n.Constants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class RestorePopUpView implements RestorePopUpPresenter.View,
                                         IsElement {

    @Inject
    @DataField("body")
    Div body;

    @Inject
    private TranslationService translationService;

    private RestorePopUpPresenter presenter;

    private BaseModal modal;

    @Override
    public void init( RestorePopUpPresenter presenter ) {
        this.presenter = presenter;
        modalSetup();
        setupComment();
    }

    @Override
    public void show() {
        modal.show();
    }

    @Override
    public void hide() {
        modal.hide();
    }

    private void modalSetup() {
        this.modal = new CommonModalBuilder()
                .addHeader( translate( Constants.RestorePopUpView_ConfirmRestore ) )
                .addBody( body )
                .addFooter( footer() )
                .build();
    }

    private ModalFooter footer() {
        GenericModalFooter footer = new GenericModalFooter();
        footer.addButton( translate( Constants.RestorePopUpView_Cancel ), cancelCommand(), ButtonType.DEFAULT );
        footer.addButton( translate( Constants.RestorePopUpView_Restore ), restoreCommand(), ButtonType.PRIMARY );
        return footer;
    }

    private String translate( final String key ) {
        return translationService.format( key );
    }

    private Command restoreCommand() {
        return () -> presenter.restore();
    }

    private Command cancelCommand() {
        return () -> presenter.cancel();
    }

    private void setupComment() {
        body.appendChild( toggleCommentPresenter().getViewElement() );
    }

    private ToggleCommentPresenter toggleCommentPresenter() {
        return presenter.getToggleCommentPresenter();
    }
}
