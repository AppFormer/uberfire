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

package org.uberfire.ext.widgets.common.client.tables;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.ListBox;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.widgets.table.client.PagedTableHelper;
import org.uberfire.ext.widgets.table.client.UberfireSimplePager;

/**
 * Paged Table Widget that stores user preferences.
 * If you doesn't need persist the preferences,
 * take a look at UberfirePagedTable.
 */
public class PagedTable<T>
        extends SimpleTable<T> {

    public static final int DEFAULT_PAGE_SIZE = 10;
    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    public UberfireSimplePager pager;

    @UiField
    public ListBox pageSizesSelector;

    @UiField
    public Column topToolbar;

    protected boolean showPageSizesSelector = false;
    private int pageSize;
    private AbstractDataProvider<T> dataProvider;

    public PagedTable() {
        this(DEFAULT_PAGE_SIZE);
    }

    public PagedTable(final int pageSize) {
        this(pageSize,
             null);
    }

    public PagedTable(final int pageSize,
                      final ProvidesKey<T> providesKey) {
        this(pageSize,
             providesKey,
             null);
    }

    public PagedTable(final int pageSize,
                      final ProvidesKey<T> providesKey,
                      final GridGlobalPreferences gridGlobalPreferences) {
        this(pageSize,
             providesKey,
             gridGlobalPreferences,
             false);
    }

    public PagedTable(final int pageSize,
                      final ProvidesKey<T> providesKey,
                      final GridGlobalPreferences gridGlobalPreferences,
                      final boolean showPageSizesSelector) {

        this(pageSize,
             providesKey,
             gridGlobalPreferences,
             showPageSizesSelector,
             false,
             false);
    }

    public PagedTable(final int pageSize,
                      final ProvidesKey<T> providesKey,
                      final GridGlobalPreferences gridGlobalPreferences,
                      final boolean showPageSizesSelector,
                      final boolean showFFButton,
                      final boolean showLButton) {
        super(providesKey,
              gridGlobalPreferences);
        this.showPageSizesSelector = showPageSizesSelector;
        this.pageSize = pageSize;
        this.dataGrid.setPageSize(pageSize);
        PagedTableHelper.setSelectedValue(pageSizesSelector,
                                          String.valueOf(pageSize));
        this.pager.setDisplay(dataGrid);
        this.pageSizesSelector.setVisible(this.showPageSizesSelector);
        setShowFastFordwardPagerButton(showFFButton);
        setShowLastPagerButton(showLButton);
        createPageSizesListBox(5,
                               20,
                               5);
    }

    @Override
    protected Widget makeWidget() {
        return uiBinder.createAndBindUi(this);
    }

    public AbstractDataProvider<T> getDataProvider() {
        return dataProvider;
    }

    public void setDataProvider(final AbstractDataProvider<T> dataProvider) {
        this.dataProvider = dataProvider;
        this.dataProvider.addDataDisplay(this);
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public int getPageStart() {
        return this.pager.getPageStart();
    }

    public final void loadPageSizePreferences() {
        pageSize = getPageSizeStored();
        this.dataGrid.setPageSize(pageSize);
        this.pager.setPageSize(pageSize);
        this.dataGrid.setHeight(((pageSize == 0 ? 1 : pageSize) * 30 + 10) + "px");
    }

    public void createPageSizesListBox(int minPageSize,
                                       int maxPageSize,
                                       int incPageSize) {
        pageSizesSelector.clear();
        PagedTableHelper.setSelectIndexOnPageSizesSelector(minPageSize,
                                                           maxPageSize,
                                                           incPageSize,
                                                           pageSizesSelector,
                                                           pageSize);

        pageSizesSelector.addChangeHandler(event -> {
            storePageSizeInGridPreferences(Integer.parseInt(pageSizesSelector.getSelectedValue()));
            loadPageSizePreferences();
        });

        loadPageSizePreferences();
    }

    public void setShowLastPagerButton(boolean showLastPagerButton) {
        this.pager.setShowLastPageButton(showLastPagerButton);
    }

    public void setShowFastFordwardPagerButton(boolean showFastFordwardPagerButton) {
        this.pager.setShowFastFordwardPageButton(showFastFordwardPagerButton);
    }

    private void storePageSizeInGridPreferences(int pageSize) {
        GridPreferencesStore gridPreferencesStore = super.getGridPreferencesStore();
        if (gridPreferencesStore != null) {
            gridPreferencesStore.setPageSizePreferences(pageSize);
            super.saveGridPreferences();
        }
        this.pageSize = pageSize;
    }

    private int getPageSizeStored() {
        GridPreferencesStore gridPreferencesStore = super.getGridPreferencesStore();
        if (gridPreferencesStore != null) {
            return gridPreferencesStore.getPageSizePreferences();
        }
        return pageSize;
    }

    private void resetPageSize() {
        GridPreferencesStore gridPreferencesStore = super.getGridPreferencesStore();

        if (gridPreferencesStore != null) {
            gridPreferencesStore.resetPageSizePreferences();
            storePageSizeInGridPreferences(gridPreferencesStore.getGlobalPreferences().getPageSize());
            loadPageSizePreferences();
        }
    }

    public HasWidgets getTopToolbar() {
        return topToolbar;
    }

    interface Binder
            extends
            UiBinder<Widget, PagedTable> {

    }
}