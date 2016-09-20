package org.uberfire.ext.layout.editor.client.components.columns;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.infra.ColumnDrop;
import org.uberfire.ext.layout.editor.client.infra.ContainerResizeEvent;
import org.uberfire.ext.layout.editor.client.infra.DragComponentEndEvent;
import org.uberfire.ext.layout.editor.client.infra.DragHelperComponentColumn;
import org.uberfire.ext.layout.editor.client.widgets.KebabWidget;
import org.uberfire.mvp.Command;

import static org.uberfire.ext.layout.editor.client.infra.CSSClassNameHelper.*;
import static org.uberfire.ext.layout.editor.client.infra.DomUtil.*;
import static org.uberfire.ext.layout.editor.client.infra.HTML5DnDHelper.*;

@Dependent
@Templated
public class ComponentColumnView
        implements UberElement<ComponentColumn>,
        ComponentColumn.View, IsElement {

    public static final String COL_CSS_CLASS = "col-md-";

    private ComponentColumn presenter;

    @Inject
    @DataField
    private Div col;

    @Inject
    @DataField
    private Div colUp;

    @Inject
    @DataField
    private Div row;

    @Inject
    @DataField
    private Div colDown;

    @Inject
    @DataField
    private Div left;

    @Inject
    @DataField( "resize-left" )
    private Button resizeLeft;

    @Inject
    @DataField
    private Div right;

    @Inject
    @DataField( "resize-right" )
    private Button resizeRight;

    @Inject
    @DataField
    private FlowPanel content;

    @Inject
    private KebabWidget kebabWidget;

    @Inject
    private Document document;


    String cssSize = "";

    private final int originalLeftRightWidth = 15;

    private ColumnDrop.Orientation contentDropOrientation;

    @Inject
    private DragHelperComponentColumn helper;

    @Override
    public void init( ComponentColumn presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setupWidget() {
        setupEvents();
        setupKebabWidget();
        setupResize();
        setupOnResize();
    }

    private void setupOnResize() {
        document.getBody().setOnresize( event -> calculateSize() );
    }

    @Override
    public void setupResize() {
        resizeLeft.getStyle().setProperty( "display", "none" );
        resizeRight.getStyle().setProperty( "display", "none" );
    }

    public void dockSelectEvent( @Observes UberfireDocksInteractionEvent event ) {
        calculateSize();
    }

    private void setupKebabWidget() {
        kebabWidget.init( () -> presenter.remove(),
                          () -> presenter.edit() );
    }

    private void setupEvents() {
        setupLeftEvents();
        setupRightEvents();
        setupColUpEvents();
        setupColDownEvents();
        setupContentEvents();
        setupColEvents();
        setupRowEvents();
        setupResizeEvents();
    }

    private void setupRowEvents() {
        row.setOnmouseout( event -> {
            removeClassName( colUp, "componentDropInColumnPreview" );
            removeClassName( colDown, "componentDropInColumnPreview" );
        } );
    }

    private void setupResizeEvents() {
        resizeLeft.setOnclick( event -> presenter.resizeLeft() );
        resizeRight.setOnclick( event -> presenter.resizeRight() );
    }

    private void setupColEvents() {
        col.setOnmouseup( e -> {
            e.preventDefault();
            if ( hasClassName( col, "rowDndPreview" ) ) {
                removeClassName( col, "rowDndPreview" );
            }
        } );
        col.setOnmouseover( e -> {
            e.preventDefault();
        } );
        col.setOnmouseout( event -> {
            removeClassName( colUp, "componentDropInColumnPreview" );
            removeClassName( colDown, "componentDropInColumnPreview" );
        } );
    }

    private void setupColUpEvents() {

        colUp.setOndragleave( event -> {
            removeClassName( colUp, "componentDropInColumnPreview" );
        } );
        colUp.setOndragexit( event -> {
            removeClassName( colUp, "componentDropInColumnPreview" );
        } );

        colUp.setOndragover( event -> {
            event.preventDefault();
            if ( presenter.shouldPreviewDrop() ) {
                contentDropOrientation = ColumnDrop.Orientation.UP;
                addClassName( colUp, "componentDropInColumnPreview" );
            }
        } );
        colUp.setOndrop( e -> {
            if ( contentDropOrientation != null ) {
                presenter.onDrop( contentDropOrientation, extractDndData( e ) );
            }
            removeClassName( colUp, "componentDropInColumnPreview" );
            removeClassName( colDown, "componentDropInColumnPreview" );
        } );
        colUp.setOnmouseout( event -> {
            removeClassName( colUp, "componentDropInColumnPreview" );
        } );
    }


    private void setupColDownEvents() {
        colDown.setOndrop( e -> {
            if ( contentDropOrientation != null ) {
                presenter.onDrop( contentDropOrientation, extractDndData( e ) );
            }
            removeClassName( colUp, "componentDropInColumnPreview" );
            removeClassName( colDown, "componentDropInColumnPreview" );
        } );
    }

    private void setupRightEvents() {
        right.setOndragenter( e -> {
            e.preventDefault();
            if ( presenter.shouldPreviewDrop() && presenter.enableSideDnD() ) {
                addClassName( right, "columnDropPreview dropPreview" );
                content.getElement().addClassName( "centerPreview" );
                removeClassName( colUp, "componentDropInColumnPreview" );
            }
        } );
        right.setOndragleave( e -> {
            e.preventDefault();
            removeClassName( right, "columnDropPreview" );
            removeClassName( right, "dropPreview" );
            content.getElement().removeClassName( "centerPreview" );
        } );
        right.setOndragover( event -> event.preventDefault() );
        right.setOndrop( e -> {
            e.preventDefault();
            if ( presenter.enableSideDnD() && presenter.shouldPreviewDrop() ) {
                removeClassName( right, "columnDropPreview" );
                removeClassName( right, "dropPreview" );
                content.getElement().removeClassName( "centerPreview" );
                presenter.onDrop( ColumnDrop.Orientation.RIGHT, extractDndData( e ) );
            }
        } );
        right.setOnmouseover( e -> {
            e.preventDefault();
            if ( presenter.canResizeRight() ) {
                resizeRight.getStyle().setProperty( "display", "block" );
            }
        } );
        right.setOnmouseout( e -> {
            e.preventDefault();
            if ( presenter.canResizeRight() ) {
                resizeRight.getStyle().setProperty( "display", "none" );
            }
        } );
    }

    private void setupContentEvents() {
        content.addDomHandler( e -> {
            e.preventDefault();
            if ( presenter.shouldPreviewDrop() ) {
                if ( dragOverUp( content, e ) ) {
                    addClassName( colUp, "componentDropInColumnPreview" );
                    removeClassName( colDown, "componentDropInColumnPreview" );
                    contentDropOrientation = ColumnDrop.Orientation.UP;

                } else {
                    addClassName( colDown, "componentDropInColumnPreview" );
                    removeClassName( colUp, "componentDropInColumnPreview" );
                    contentDropOrientation = ColumnDrop.Orientation.DOWN;
                }
            }
        }, DragOverEvent.getType() );
        content.addDomHandler( e -> {
            e.preventDefault();
            //ederign
            removeClassName( colDown, "componentDropInColumnPreview" );
            contentDropOrientation = null;
        }, DragLeaveEvent.getType() );

        content.addDomHandler( e -> {
            if ( contentDropOrientation != null ) {
                presenter.onDrop( contentDropOrientation, e.getData( "text" ) );
            }
            removeClassName( colUp, "componentDropInColumnPreview" );
            removeClassName( colDown, "componentDropInColumnPreview" );
        }, DropEvent.getType() );

        content.addDomHandler( mouseOutEvent -> content.getElement().removeClassName( "componentMovePreview" ), MouseOutEvent.getType() );

        content.addDomHandler( e -> {
            e.preventDefault();
            content.getElement().addClassName( "componentMovePreview" );
        }, MouseOverEvent.getType() );

        content.addDomHandler( e -> {
            e.stopPropagation();
            removeClassName( row, "rowDndPreview" );
            presenter.dragEndComponent();
        }, DragEndEvent.getType() );

        content.addDomHandler( e -> {
            e.stopPropagation();
            addClassName( row, "rowDndPreview" );
            presenter.dragStartComponent();
        }, DragStartEvent.getType() );
    }


    private void setupLeftEvents() {
        left.setOndragleave( e -> {
            e.preventDefault();
            removeClassName( left, "columnDropPreview" );
            removeClassName( left, "dropPreview" );
            content.getElement().removeClassName( "centerPreview" );
        } );
        left.setOndrop( e -> {
            e.preventDefault();
            if ( presenter.enableSideDnD() && presenter.shouldPreviewDrop() ) {
                removeClassName( left, "columnDropPreview" );
                removeClassName( left, "dropPreview" );
                content.getElement().removeClassName( "centerPreview" );
                presenter.onDrop( ColumnDrop.Orientation.LEFT, extractDndData( e ) );
            }
        } );

        left.setOndragover( event -> {
            if ( presenter.enableSideDnD() && presenter.shouldPreviewDrop() ) {
                event.preventDefault();
            }
        } );
        left.setOndragexit( event -> {
            event.preventDefault();
            removeClassName( left, "columnDropPreview" );
            removeClassName( left, "dropPreview" );
            content.getElement().removeClassName( "centerPreview" );
        } );
        left.setOndragenter( e -> {
            e.preventDefault();
            if ( presenter.enableSideDnD() && presenter.shouldPreviewDrop() ) {
                addClassName( left, "columnDropPreview dropPreview" );
                content.getElement().addClassName( "centerPreview" );
                removeClassName( colUp, "componentDropInColumnPreview" );
            }
        } );
        left.setOnmouseover( e -> {
            e.preventDefault();
            if ( presenter.canResizeLeft() ) {
                resizeLeft.getStyle().setProperty( "display", "block" );
            }
        } );
        left.setOnmouseout( e -> {
            e.preventDefault();
            if ( presenter.canResizeLeft() ) {
                resizeLeft.getStyle().setProperty( "display", "none" );
            }
        } );
    }

    public void resizeEventObserver( @Observes ContainerResizeEvent event ) {
        calculateSize();
    }

    @Override
    public void calculateSize() {
        Scheduler.get().scheduleDeferred( () -> {
            controlPadding();
            calculateLeftRightWidth();
            calculateContentWidth();
            addClassName( col, "container" );
        } );
    }

    private void controlPadding() {
        if ( !presenter.isInnerColumn() ) {
            addClassName( col, "no-padding" );
        } else {
            if ( hasClassName( col, "no-padding" ) ) {
                removeClassName( col, "no-padding" );
            }
        }
    }

    private void calculateLeftRightWidth() {
        if ( originalLeftRightWidth >= 0 ) {
            left.getStyle().setProperty( "width", originalLeftRightWidth + "px" );
            right.getStyle().setProperty( "width", originalLeftRightWidth + "px" );
        }
    }


    private void calculateContentWidth() {
        int smallSpace = 2;
        final int colWidth = Integer.parseInt( extractOffSetWidth( col ) );
        final int contentWidth = colWidth - ( originalLeftRightWidth * 2 ) - smallSpace;
        if ( contentWidth >= 0 ) {
            content.getElement().getStyle().setProperty( "width", contentWidth + "px" );
            colDown.getStyle().setProperty( "width", "100%" );
            colUp.getStyle().setProperty( "width", "100%" );
        }
    }

    @Override
    public void setSize( String size ) {
        if ( !col.getClassName().isEmpty() ) {
            removeClassName( col, cssSize );
        }
        cssSize = COL_CSS_CLASS + size;
        addClassName( col, cssSize );
    }


    @Override
    public void clearContent() {
        content.clear();
    }

    @Override
    public void setContent() {
        Scheduler.get().scheduleDeferred( () -> {
            content.clear();
            Widget previewWidget = getPreviewWidget();
            previewWidget.getElement().getStyle().setProperty( "cursor", "default" );
            previewWidget.getElement().setClassName( "le-widget" );
            content.getElement().appendChild( kebabWidget.getElement() );
            content.add( previewWidget );
        } );
    }


    @Override
    public void showConfigComponentModal( Command configurationFinish, Command configurationCanceled ) {
        helper.showConfigModal( configurationFinish, configurationCanceled );
    }

    @Override
    public boolean hasModalConfiguration() {
        return helper.hasModalConfiguration();
    }

    @Override
    public void setup( LayoutComponent layoutComponent ) {
        helper.setLayoutComponent( layoutComponent );
    }


    private Widget getPreviewWidget() {
        return helper.getPreviewWidget( content ).asWidget();
    }


    private boolean hasColPreview( HTMLElement element ) {
        return hasClassName( element, "componentDropInColumnPreview" );
    }


    private boolean dragOverUp( Widget div, DragOverEvent e ) {
        final int absoluteTop = div.getElement().getAbsoluteTop();
        final int absoluteBottom = div.getElement().getAbsoluteBottom();
        int dragOverY = e.getNativeEvent().getClientY();

        return ( dragOverY - absoluteTop ) < ( absoluteBottom - dragOverY );
    }

    public void cleanUp( @Observes DragComponentEndEvent dragComponentEndEvent ) {
        removeClassName( colUp, "componentDropInColumnPreview" );
    }
}
