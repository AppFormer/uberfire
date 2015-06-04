/*
 *
 *  * Copyright 2012 JBoss Inc
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy of
 *  * the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations under
 *  * the License.
 *
 */

package org.uberfire.client.views.bs2.dnd;

import javax.enterprise.context.ApplicationScoped;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.util.CoordinateArea;
import com.allen_sauer.gwt.dnd.client.util.CoordinateLocation;
import com.allen_sauer.gwt.dnd.client.util.Location;
import com.allen_sauer.gwt.dnd.client.util.WidgetArea;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.resources.WorkbenchResources;
import org.uberfire.client.workbench.widgets.dnd.CompassWidget;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;

/**
 * Created by Cristiano Nicolai.
 */
@ApplicationScoped
public class CompassWidgetImpl extends PopupPanel implements CompassWidget {

    private static Element dropTargetHighlight;

    private final Image northWidget = new Image( WorkbenchResources.INSTANCE.images().compassNorth() );
    private final Image southWidget = new Image( WorkbenchResources.INSTANCE.images().compassSouth() );
    private final Image eastWidget = new Image( WorkbenchResources.INSTANCE.images().compassEast() );
    private final Image westWidget = new Image( WorkbenchResources.INSTANCE.images().compassWest() );
    private final Image centreWidget = new Image( WorkbenchResources.INSTANCE.images().compassCentre() );

    private final FlexTable container = new FlexTable();

    private CompassPosition dropTargetPosition = CompassPosition.NONE;

    public CompassWidgetImpl() {
        super();
        this.setStyleName( WorkbenchResources.INSTANCE.CSS().dropTargetCompass() );
        this.container.setCellPadding( 0 );
        this.container.setCellSpacing( 0 );
        this.container.setPixelSize( 100, 100 );

        //Setup drop indicator
        if ( dropTargetHighlight == null ) {
            dropTargetHighlight = Document.get().createDivElement();
            dropTargetHighlight.getStyle().setPosition( Style.Position.ABSOLUTE );
            dropTargetHighlight.getStyle().setVisibility( Style.Visibility.HIDDEN );
            dropTargetHighlight.setClassName( WorkbenchResources.INSTANCE.CSS().dropTargetHighlight() );
            Document.get().getBody().appendChild( dropTargetHighlight );
        }

        northWidget.getElement().getStyle().setOpacity( 0.75 );
        southWidget.getElement().getStyle().setOpacity( 0.75 );
        eastWidget.getElement().getStyle().setOpacity( 0.75 );
        westWidget.getElement().getStyle().setOpacity( 0.75 );

        northWidget.ensureDebugId( "CompassWidget-north" );
        southWidget.ensureDebugId( "CompassWidget-south" );
        eastWidget.ensureDebugId( "CompassWidget-east" );
        westWidget.ensureDebugId( "CompassWidget-west" );
        centreWidget.ensureDebugId( "CompassWidget-centre" );

        container.setWidget( 0, 1, northWidget );
        container.setWidget( 1, 0, westWidget );
        container.setWidget( 1, 1, centreWidget );
        container.setWidget( 1, 2, eastWidget );
        container.setWidget( 2, 1, southWidget );

        setWidget( container );
    }

    @Override
    public void onEnter( DragContext context ) {
//        Fix for preventing the selection of all elements during drag move in Chrome
        Document.get().getBody().addClassName( "uf-no-select" );
        show( context );
    }

    @Override
    public void onLeave( DragContext context ) {
        hide();
        Document.get().getBody().removeClassName( "uf-no-select" );
    }

    @Override
    public void onMove( DragContext context ) {
        final Location l = new CoordinateLocation( context.mouseX,
                context.mouseY );
        final WidgetArea northWidgetArea = new WidgetArea( northWidget,
                null );
        final WidgetArea southWidgetArea = new WidgetArea( southWidget,
                null );
        final WidgetArea eastWidgetArea = new WidgetArea( eastWidget,
                null );
        final WidgetArea westWidgetArea = new WidgetArea( westWidget,
                null );
        final WidgetArea centreWidgetArea = new WidgetArea( centreWidget,
                null );
        CompassPosition p = CompassPosition.NONE;
        if ( northWidgetArea.intersects( l ) ) {
            p = CompassPosition.NORTH;
        } else if ( southWidgetArea.intersects( l ) ) {
            p = CompassPosition.SOUTH;
        } else if ( eastWidgetArea.intersects( l ) ) {
            p = CompassPosition.EAST;
        } else if ( westWidgetArea.intersects( l ) ) {
            p = CompassPosition.WEST;
        } else if ( centreWidgetArea.intersects( l ) ) {
            p = CompassPosition.SELF;
        }
        if ( p != dropTargetPosition ) {
            dropTargetPosition = p;
            showDropTarget( context,
                    p );
        }
    }

    public Position getDropPosition() {
        return this.dropTargetPosition;
    }

    @Override
    public Widget getDropTarget() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onDrop( DragContext context ) {
        this.dropTargetPosition = CompassPosition.NONE;
        hideDropTarget();
    }

    @Override
    public void onPreviewDrop( DragContext context ) throws VetoDragException {
        throw new UnsupportedOperationException();
    }

    private void show( final DragContext context ) {

        //Get centre of DropTarget
        final Widget dropTargetParent = context.dropController.getDropTarget();
        int cxmin = dropTargetParent.getElement().getAbsoluteLeft();
        int cymin = dropTargetParent.getElement().getAbsoluteTop();
        int cxmax = dropTargetParent.getElement().getAbsoluteRight();
        int cymax = dropTargetParent.getElement().getAbsoluteBottom();

        final CoordinateArea ca = new CoordinateArea( cxmin,
                cymin,
                cxmax,
                cymax );

        //Display Compass if not already visible
        if ( !isAttached() ) {
            setPopupPositionAndShow( new PositionCallback() {

                @Override
                public void setPosition( int offsetWidth,
                                         int offsetHeight ) {
                    setPopupPosition( ca.getCenter().getLeft() - ( offsetWidth / 2 ),
                            ca.getCenter().getTop() - ( offsetHeight / 2 ) );
                }

            } );

        } else {
            setPopupPosition( ca.getCenter().getLeft() - ( getOffsetWidth() / 2 ),
                    ca.getCenter().getTop() - ( getOffsetHeight() / 2 ) );
        }

    }

    private void showDropTarget( final DragContext context,
                                 final CompassPosition p ) {
        int x = 0;
        int y = 0;
        int width = 0;
        int height = 0;
        final Widget dropTargetParent = context.dropController.getDropTarget();
        switch ( p ) {
            case SELF:
                x = dropTargetParent.getAbsoluteLeft();
                y = dropTargetParent.getAbsoluteTop();
                width = dropTargetParent.getOffsetWidth();
                height = dropTargetParent.getOffsetHeight();
                showDropTarget( x,
                        y,
                        width,
                        height );
                break;
            case NORTH:
                x = dropTargetParent.getAbsoluteLeft();
                y = dropTargetParent.getAbsoluteTop();
                width = dropTargetParent.getOffsetWidth();
                height = (int) ( dropTargetParent.getOffsetHeight() * 0.50 );
                showDropTarget( x,
                        y,
                        width,
                        height );
                break;
            case SOUTH:
                x = dropTargetParent.getAbsoluteLeft();
                height = (int) ( dropTargetParent.getOffsetHeight() * 0.50 );
                y = dropTargetParent.getOffsetHeight() + dropTargetParent.getAbsoluteTop() - height;
                width = dropTargetParent.getOffsetWidth();
                showDropTarget( x,
                        y,
                        width,
                        height );
                break;
            case EAST:
                width = (int) ( dropTargetParent.getOffsetWidth() * 0.50 );
                x = dropTargetParent.getOffsetWidth() + dropTargetParent.getAbsoluteLeft() - width;
                y = dropTargetParent.getAbsoluteTop();
                height = dropTargetParent.getOffsetHeight();
                showDropTarget( x,
                        y,
                        width,
                        height );
                break;
            case WEST:
                x = dropTargetParent.getAbsoluteLeft();
                y = dropTargetParent.getAbsoluteTop();
                width = (int) ( dropTargetParent.getOffsetWidth() * 0.50 );
                height = dropTargetParent.getOffsetHeight();
                showDropTarget( x,
                        y,
                        width,
                        height );
                break;
            default:
                hideDropTarget();
        }
    }

    private void showDropTarget( int x,
                                 int y,
                                 int width,
                                 int height ) {
        dropTargetHighlight.getStyle().setLeft( x, Style.Unit.PX );
        dropTargetHighlight.getStyle().setWidth( width, Style.Unit.PX );
        dropTargetHighlight.getStyle().setTop( y, Style.Unit.PX );
        dropTargetHighlight.getStyle().setHeight( height, Style.Unit.PX );
        dropTargetHighlight.getStyle().setVisibility( Style.Visibility.VISIBLE );
        dropTargetHighlight.getStyle().setDisplay( Style.Display.BLOCK );
    }

    private void hideDropTarget() {
        dropTargetHighlight.getStyle().setVisibility( Style.Visibility.HIDDEN );
        dropTargetHighlight.getStyle().setDisplay( Style.Display.NONE );
        dropTargetHighlight.getStyle().setLeft( 0, Style.Unit.PX );
        dropTargetHighlight.getStyle().setWidth( 0, Style.Unit.PX );
        dropTargetHighlight.getStyle().setTop( 0, Style.Unit.PX );
        dropTargetHighlight.getStyle().setHeight( 0, Style.Unit.PX );
        dropTargetPosition = CompassPosition.NONE;
    }

}
