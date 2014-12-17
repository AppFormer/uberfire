/*
 * Copyright 2012 JBoss Inc
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
package org.uberfire.client.views.pfly.menu;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Navbar;
import org.gwtbootstrap3.client.ui.NavbarCollapse;
import org.gwtbootstrap3.client.ui.NavbarNav;
import org.gwtbootstrap3.client.ui.base.AbstractListItem;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.menu.Menus;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Menu Bar widget
 */
public class WorkbenchMenuBarView extends Composite
        implements
        WorkbenchMenuBarPresenter.View {

    @Inject
    private AuthorizationManager authzManager;

    @Inject
    private User identity;

    public NavbarNav menuBarLeft = new NavbarNav();
    public NavbarNav menuBarCenter = new NavbarNav();
    public NavbarNav menuBarRight = new NavbarNav();

    public WorkbenchMenuBarView() {
        Navbar container = new Navbar();
        NavbarCollapse collapsibleContainer = new NavbarCollapse();

        menuBarLeft.setPull( Pull.LEFT );
        menuBarRight.setPull( Pull.RIGHT );

        collapsibleContainer.add( menuBarLeft );
        collapsibleContainer.add( menuBarRight );
        collapsibleContainer.add( menuBarCenter );

        container.add( collapsibleContainer );

        initWidget( container );
    }

    @Override
    public void addMenuItems( final Menus menus ) {
        HasMenuItems topLevelContainer = new HasMenuItems() {

            @Override
            public Widget asWidget() {
                return WorkbenchMenuBarView.this;
            }

            @Override
            public int getMenuItemCount() {
                return menuBarLeft.getWidgetCount() + menuBarCenter.getWidgetCount() + menuBarRight.getWidgetCount();
            }

            @Override
            public void addMenuItem( AbstractListItem menuContent ) {
                switch ( menuContent.getPull() ) {
                    case LEFT:
                    menuBarLeft.add( menuContent );
                    break;
                    case NONE:
                    menuBarCenter.add( menuContent );
                    break;
                    case RIGHT:
                    menuBarRight.add( menuContent );
                }
            }
        };
        Bs3Menus.constructMenuView( menus, authzManager, identity, topLevelContainer );
    }

    @Override
    public void clear() {
        menuBarLeft.clear();
        menuBarCenter.clear();
        menuBarRight.clear();
    }

}
