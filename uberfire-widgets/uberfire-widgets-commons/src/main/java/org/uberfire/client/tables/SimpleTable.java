/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.uberfire.client.tables;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.RowCountChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import org.uberfire.client.resources.CommonResources;

/**
 * A composite Widget that shows rows of data (not-paged) and a "column picker"
 * to allow columns to be hidden from view. Columns can also be sorted.
 */
public class SimpleTable<T>
        extends Composite
        implements HasData<T> {

    interface Binder
            extends
            UiBinder<Widget, SimpleTable> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField(provided = true)
    public Button columnPickerButton;

    @UiField(provided = true)
    public DataGrid<T> dataGrid;

    @UiField
    public HorizontalPanel toolbarContainer;

    private String emptyTableCaption;

    private ColumnPicker<T> columnPicker;

    public SimpleTable() {
        dataGrid = new DataGrid<T>();
        setupGridTable();
    }

    public SimpleTable( final ProvidesKey<T> providesKey ) {
        dataGrid = new DataGrid<T>( Integer.MAX_VALUE,
                                    providesKey );
        setupGridTable();
    }

    private void setupGridTable() {
        dataGrid.setStriped( true );
        dataGrid.setBordered( true );
        dataGrid.setSkipRowHoverCheck( false );
        dataGrid.setSkipRowHoverStyleUpdate( false );
        dataGrid.setWidth( "100%" );
        dataGrid.setHeight( "300px" );
        dataGrid.addStyleName( CommonResources.INSTANCE.CSS().dataGrid() );

        setEmptyTableWidget();

        columnPicker = new ColumnPicker<T>( dataGrid );
        columnPickerButton = columnPicker.createToggleButton();

        initWidget( makeWidget() );
    }

    protected Widget makeWidget() {
        return uiBinder.createAndBindUi( this );
    }

    public void setEmptyTableCaption( final String emptyTableCaption ) {
        this.emptyTableCaption = emptyTableCaption;
        setEmptyTableWidget();
    }

    private void setEmptyTableWidget() {
        String caption = "-----";
        if ( !( emptyTableCaption == null || emptyTableCaption.trim().isEmpty() ) ) {
            caption = emptyTableCaption;
        }
        dataGrid.setEmptyTableWidget( new Label( caption ) );
    }

    public void redraw() {
        dataGrid.redraw();
    }

    public void refresh() {
        dataGrid.setVisibleRangeAndClearData( dataGrid.getVisibleRange(),
                                              true );
    }

    @Override
    public HandlerRegistration addCellPreviewHandler( final Handler<T> handler ) {
        return dataGrid.addCellPreviewHandler( handler );
    }

    @Override
    public HandlerRegistration addRangeChangeHandler( final RangeChangeEvent.Handler handler ) {
        return dataGrid.addRangeChangeHandler( handler );
    }

    @Override
    public HandlerRegistration addRowCountChangeHandler( final RowCountChangeEvent.Handler handler ) {
        return dataGrid.addRowCountChangeHandler( handler );
    }

    /**
     * Link a column sort handler to the table
     * @param handler
     */
    public HandlerRegistration addColumnSortHandler( final ColumnSortEvent.Handler handler ) {
        return this.dataGrid.addColumnSortHandler( handler );
    }

    @Override
    public int getRowCount() {
        return dataGrid.getRowCount();
    }

    @Override
    public Range getVisibleRange() {
        return dataGrid.getVisibleRange();
    }

    @Override
    public boolean isRowCountExact() {
        return dataGrid.isRowCountExact();
    }

    @Override
    public void setRowCount( final int count ) {
        dataGrid.setRowCount( count );
    }

    @Override
    public void setRowCount( final int count,
                             final boolean isExact ) {
        dataGrid.setRowCount( count,
                              isExact );
    }

    @Override
    public void setVisibleRange( final int start,
                                 final int length ) {
        dataGrid.setVisibleRange( start,
                                  length );
    }

    @Override
    public void setVisibleRange( final Range range ) {
        dataGrid.setVisibleRange( range );
    }

    @Override
    public SelectionModel<? super T> getSelectionModel() {
        return dataGrid.getSelectionModel();
    }

    @Override
    public T getVisibleItem( final int indexOnPage ) {
        return dataGrid.getVisibleItem( indexOnPage );
    }

    @Override
    public int getVisibleItemCount() {
        return dataGrid.getVisibleItemCount();
    }

    @Override
    public Iterable<T> getVisibleItems() {
        return dataGrid.getVisibleItems();
    }

    @Override
    public void setRowData( final int start,
                            final List<? extends T> values ) {
        dataGrid.setRowData( start,
                             values );
    }

    public void setRowData( final List<? extends T> values ) {
        dataGrid.setRowData( values );
    }

    @Override
    public void setSelectionModel( final SelectionModel<? super T> selectionModel ) {
        dataGrid.setSelectionModel( selectionModel );
    }

    public void setSelectionModel( final SelectionModel<? super T> selectionModel,
                                   final CellPreviewEvent.Handler<T> selectionEventManager ) {
        dataGrid.setSelectionModel( selectionModel,
                                    selectionEventManager );
    }

    @Override
    public void setVisibleRangeAndClearData( final Range range,
                                             final boolean forceRangeChangeEvent ) {
        dataGrid.setVisibleRangeAndClearData( range,
                                              forceRangeChangeEvent );
    }

    public void addColumn( final Column<T, ?> column,
                           final String caption ) {
        addColumn( column,
                   caption,
                   true );
    }

    public void addColumn( final Column<T, ?> column,
                           final String caption,
                           final boolean visible ) {
        final Header header = new ResizableMovableHeader<T>( caption,
                                                             dataGrid,
                                                             columnPicker,
                                                             column ) {
            @Override
            protected int getTableBodyHeight() {
                return dataGrid.getOffsetHeight();
            }
        };
        column.setDataStoreName( caption );
        columnPicker.addColumn( column,
                                header,
                                visible );
    }

    public void setColumnWidth( final Column<T, ?> column,
                                final double width,
                                final Style.Unit unit ) {
        dataGrid.setColumnWidth( column,
                                 width,
                                 unit );
    }

    @Override
    public void setHeight( String height ) {
        dataGrid.setHeight( height );
    }

    @Override
    public void setPixelSize( int width,
                              int height ) {
        dataGrid.setPixelSize( width, height );
    }

    @Override
    public void setSize( String width,
                         String height ) {
        dataGrid.setSize( width, height );
    }

    @Override
    public void setWidth( String width ) {
        dataGrid.setWidth( width );
    }

    public ColumnSortList getColumnSortList() {
        return dataGrid.getColumnSortList();
    }

    public HasWidgets getToolbar() {
        return toolbarContainer;
    }

}
