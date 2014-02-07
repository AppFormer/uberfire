package org.uberfire.client.workbench.panels.impl;

import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.Position;

public interface BaseWorkbenchPanelViewHelper {

    public void addPanel( final PanelDefinition panel,
                          final WorkbenchPanelView view,
                          final Position position , WorkbenchPanelView parent);

    public void removePanel(WorkbenchPanelView pview,  WorkbenchDragAndDropManager dndManager);

}
