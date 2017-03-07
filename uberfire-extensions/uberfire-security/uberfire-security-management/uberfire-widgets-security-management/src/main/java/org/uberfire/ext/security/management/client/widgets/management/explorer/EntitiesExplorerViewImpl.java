/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.widgets.management.explorer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.LabelType;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.security.management.client.widgets.management.list.EntitiesList;

@Dependent
public class EntitiesExplorerViewImpl extends Composite
        implements
        EntitiesExplorerView {

    private static EntitiesExplorerViewImplBinder uiBinder = GWT.create(EntitiesExplorerViewImplBinder.class);
    @UiField
    FlowPanel mainPanel;
    @UiField
    org.gwtbootstrap3.client.ui.Label mainLabel;
    @UiField
    Container mainContainer;
    @UiField
    Heading heading;
    @UiField
    Row searchRow;
    @UiField
    TextBox searchBox;
    @UiField
    Button searchButton;
    @UiField
    Button clearSearchButton;
    @UiField
    Button createButton;
    @UiField
    Button refreshButton;
    @UiField(provided = true)
    EntitiesList.View entitiesListView;
    private String entityType;
    private EntitiesExplorerView.ViewContext context;
    private EntitiesExplorerView.ViewCallback callback;

    @PostConstruct
    public void init() {

    }

    @Override
    public EntitiesExplorerView configure(final String entityType,
                                          final EntitiesList.View entitiesListView) {
        this.entitiesListView = entitiesListView;
        this.entityType = entityType;

        initWidget(uiBinder.createAndBindUi(this));
        searchBox.addKeyDownHandler(new KeyDownHandler() {

            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    doSearch(searchBox.getText());
                }
            }
        });
        searchButton.addClickHandler(e -> doSearch(searchBox.getText()));

        clearSearchButton.addDomHandler(new ClickHandler() {
                                            @Override
                                            public void onClick(final ClickEvent clickEvent) {
                                                doSearch("");
                                            }
                                        },
                                        ClickEvent.getType());
        clearSearch();
        return this;
    }

    @Override
    public EntitiesExplorerView show(ViewContext context,
                                     ViewCallback callback) {
        // Clear current view.
        clear();

        this.context = context;
        this.callback = callback;

        // Configure available features.
        if (this.context.canSearch()) {
            searchRow.setVisible(true);
        }
        if (this.context.canCreate()) {
            createButton.setVisible(true);
            String createText = UsersManagementWidgetsConstants.INSTANCE.newEntity(entityType);
            createButton.setText(createText);
            createButton.setTitle(createText);
        }

        // Configure titles and texts using the title for the entity type.
        final String searchForEntities = getTitleWithEntityType(UsersManagementWidgetsConstants.INSTANCE.searchFor(),
                                                                true);
        searchButton.setTitle(searchForEntities);
        return this;
    }

    @Override
    public EntitiesExplorerView showMessage(final LabelType labelType,
                                            final String message) {
        clear();
        mainLabel.setText(message);
        mainLabel.setType(labelType);
        mainLabel.setVisible(true);
        mainContainer.setVisible(false);

        return this;
    }

    @Override
    public EntitiesExplorerView clearSearch() {
        final String allEntitiesHeader = getTitleWithEntityType(UsersManagementWidgetsConstants.INSTANCE.all(),
                                                                true);
        heading.setText(allEntitiesHeader);
        searchBox.setText("");
        searchBox.setPlaceholder(allEntitiesHeader);
        clearSearchButton.setEnabled(false);
        return this;
    }

    @Override
    public EntitiesExplorerView clear() {
        searchRow.setVisible(false);
        createButton.setVisible(false);
        context = null;
        callback = null;
        return this;
    }

    void doSearch(final String pattern) {
        final String pEsc = SafeHtmlUtils.htmlEscape(pattern);
        heading.setText(UsersManagementWidgetsConstants.INSTANCE.searchResultsFor() + " " + pEsc);
        clearSearchButton.setEnabled(true);
        if (callback != null) {
            callback.onSearch(pattern);
        }
    }

    private String getTitleWithEntityType(final String text,
                                          final boolean plural) {
        final String t = entityType != null ? plural ? entityType + "s" : entityType : null;
        if (t != null) {
            return text + " " + t;
        } else {
            return text;
        }
    }

    @UiHandler("createButton")
    public void onCreateButtonClick(final ClickEvent event) {
        if (callback != null) {
            callback.onCreate();
        }
    }

    @UiHandler("refreshButton")
    public void onRefreshButtonClick(final ClickEvent event) {
        if (callback != null) {
            callback.onRefresh();
        }
    }

    interface EntitiesExplorerViewImplBinder
            extends
            UiBinder<FlowPanel, EntitiesExplorerViewImpl> {

    }
}