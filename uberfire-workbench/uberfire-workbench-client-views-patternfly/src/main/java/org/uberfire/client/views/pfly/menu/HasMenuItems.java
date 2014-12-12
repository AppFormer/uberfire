package org.uberfire.client.views.pfly.menu;

import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.base.AbstractListItem;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Widgets that can contain menu items implement this interface.
 */
public interface HasMenuItems extends IsWidget {

    /**
     * Adds a new menu item to the end of the current list of menu items.
     *
     * @param menuContent
     *            the content that should appear in the given menu item. Should have an Anchor element as its only
     *            direct child, or should be an {@link AnchorListItem} which is a convenient shorthand for an Anchor
     *            inside a ListItem.
     */
    void addMenuItem( AbstractListItem menuContent );

    /**
     * Returns the number of menu items directly within this container. Counts direct children only, not grandchildren.
     *
     * @return the number of menu items directly within this container.
     */
    int getMenuItemCount();
}
