package org.uberfire.client.docks;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.resources.WebAppResource;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.mvp.ParameterizedCommand;

public class DocksBar
        extends Composite {

    private UberfireDockPosition position;

    private final ParameterizedCommand<String> dropHandler;

    interface ViewBinder
            extends
            UiBinder<Widget, DocksBar> {
    }

    private ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @UiField
    FlowPanel allDocksMenuPanel;

    @UiField
    FlowPanel docksBarPanel;

    private List<DocksItem> docksItems = new ArrayList<DocksItem>();

    private static WebAppResource CSS = GWT.create( WebAppResource.class );

    private AllDocksMenu allDocksMenu;

    public DocksBar( UberfireDockPosition position,
                     ParameterizedCommand<String> dropHandler ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.position = position;
        this.dropHandler = dropHandler;
        setCSS( position );
        if ( position == UberfireDockPosition.SOUTH ) {
            createAvaliableDocksButton();
        }
        createDragHandler();
    }

    private void createDragHandler() {
        addDragOverHandler( new DragOverHandler() {
            @Override
            public void onDragOver( DragOverEvent event ) {
                dragOverHandler();
            }
        } );
        addDragLeaveHandler( new DragLeaveHandler() {
            @Override
            public void onDragLeave( DragLeaveEvent event ) {
                dragLeaveHandler();
            }
        } );
        addDropHandler( new DropHandler() {
            @Override
            public void onDrop( DropEvent event ) {
                dropHandler( event );
            }
        } );
    }

    void dropHandler( DropEvent event ) {
        event.preventDefault();
        dropHandler.execute( event.getData( DocksItem.class.getSimpleName() ) );
        dragLeaveHandler();
    }

    void dragOverHandler() {
//        addCSSClass( WebAppResource.INSTANCE.CSS().dropBorder() );
    }

    void dragLeaveHandler() {
//        removeCSSClass( WebAppResource.INSTANCE.CSS().dropBorder() );
    }

    public void updateAvaliableDocksMenu( Set<UberfireDock> avaliableDocks,
                                          ParameterizedCommand<String> openAvaliableDocks ) {
        allDocksMenu.clearDocksMenu();
        for ( UberfireDock avaliableDock : avaliableDocks ) {
            allDocksMenu.createDocksMenu( avaliableDock.getIdentifier(), openAvaliableDocks );
        }
    }

    public void createAvaliableDocksButton() {
        allDocksMenu = new AllDocksMenu();
        Button allDocksButton = GWT.create( Button.class );
        allDocksButton.setIcon( IconType.COGS );
        allDocksButton.setSize( ButtonSize.MINI );
        allDocksButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                if ( allDocksMenu.isShowing() ) {
                    allDocksMenu.hide();
                } else {
                    allDocksMenu.show( DocksBar.this );
                }
            }
        } );
        allDocksMenuPanel.add( allDocksButton );
    }

    public void clearDocks() {
        docksBarPanel.clear();
        docksItems = new ArrayList<DocksItem>(  );
    }

    public void setDockSelected( String identifier ) {
        for ( DocksItem docksItem : docksItems ) {
            if ( docksItem.getIdentifier().equalsIgnoreCase( identifier ) ) {
                docksItem.selected();
            } else {
                docksItem.deselect();
            }
        }
    }

    public void deselectAllDocks() {
        for ( DocksItem docksItem : docksItems ) {
            docksItem.deselect();
        }
    }

    public void addDock( UberfireDock dock,
                         final ParameterizedCommand<String> selectCommand,
                         final ParameterizedCommand<String> deselectCommand ) {
        DocksItem dockItem = new DocksItem( dock.getIdentifier(), selectCommand, deselectCommand );
        docksItems.add( dockItem );
        docksBarPanel.add( dockItem );
    }

    public void removeDock( UberfireDock dock ) {

        DocksItem toRemove = null;
        for ( DocksItem docksItem : docksItems ) {
            if ( docksItem.getIdentifier().equalsIgnoreCase( dock.getIdentifier() ) ) {
                toRemove = docksItem;
            }
        }
        if ( toRemove != null ) {
            docksItems.remove( toRemove );
            docksBarPanel.remove( toRemove );
        }
    }

    public double widgetSize() {
        return 30;
    }

    private void setCSS( UberfireDockPosition position ) {

        String style;
        if ( position == UberfireDockPosition.SOUTH ) {
            style = CSS.CSS().southDockInnerPanel();
        } else if ( position == UberfireDockPosition.WEST ) {
            style = CSS.CSS().westDockInnerPanel();
        } else {
            style = CSS.CSS().eastDockInnerPanel();
        }
        addStyleName( CSS.CSS().dock() );
        docksBarPanel.addStyleName( style );
    }

    private HandlerRegistration addDropHandler( DropHandler handler ) {
        return addBitlessDomHandler( handler, DropEvent.getType() );
    }

    private HandlerRegistration addDragOverHandler( DragOverHandler handler ) {
        return addBitlessDomHandler( handler, DragOverEvent.getType() );
    }

    private HandlerRegistration addDragLeaveHandler( DragLeaveHandler handler ) {
        return addBitlessDomHandler( handler, DragLeaveEvent.getType() );
    }

    public UberfireDockPosition getPosition() {
        return position;
    }

    public List<DocksItem> getDocksItems() {
        return docksItems;
    }

    public AllDocksMenu getAllDocksMenu() {
        return allDocksMenu;
    }
}
