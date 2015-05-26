package org.uberfire.client.workbench.docks;

import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Used by the workbench to lookup for docks in classpath
 */
public interface UberfireDocks {

    void register( UberfireDock... docks );

    void setup( DockLayoutPanel rootContainer );
}
