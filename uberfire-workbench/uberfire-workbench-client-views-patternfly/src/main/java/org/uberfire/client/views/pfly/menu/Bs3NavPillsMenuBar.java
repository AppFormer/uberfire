package org.uberfire.client.views.pfly.menu;

import org.gwtbootstrap3.client.ui.NavPills;
import org.gwtbootstrap3.client.ui.base.AbstractListItem;

import com.google.gwt.user.client.ui.Widget;


public class Bs3NavPillsMenuBar extends NavPills implements HasMenuItems {


    @Override
    public void addMenuItem( AbstractListItem item ) {
        add( (Widget) item );
    }

    @Override
    public int getMenuItemCount() {
        return getWidgetCount();
    }

}
