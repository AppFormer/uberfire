package org.uberfire.client.views.pfly.menu;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.DropDown;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.base.AbstractListItem;
import org.gwtbootstrap3.client.ui.constants.Toggle;

/**
 * A container for menu items. The contents are initially hidden. They appear when the title text is clicked.
 */
public class Bs3DropDownMenu extends DropDown implements HasMenuItems {

    DropDownMenu items = new DropDownMenu();

    public Bs3DropDownMenu(String text) {
        Anchor anchor = new Anchor();
        anchor.setText( text );
        anchor.setDataToggle( Toggle.DROPDOWN );
        add( anchor );
        add( items );
    }

    @Override
    public void addMenuItem( AbstractListItem listItem ) {
        items.add( listItem );
    }

    @Override
    public int getMenuItemCount() {
        return items.getWidgetCount();
    }

}
