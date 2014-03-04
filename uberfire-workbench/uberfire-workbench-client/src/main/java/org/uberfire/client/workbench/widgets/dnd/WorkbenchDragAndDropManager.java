package org.uberfire.client.workbench.widgets.dnd;

import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;

public interface WorkbenchDragAndDropManager {

    void makeDraggable( IsWidget draggable,
                        IsWidget dragHandle );

    void registerDropController( WorkbenchPanelView owner,
                                 DropController dropController );

    void unregisterDropController( WorkbenchPanelView view );

    void unregisterDropControllers();

    void setWorkbenchContext( WorkbenchDragContext workbenchContext );

    WorkbenchDragContext getWorkbenchContext();
}
