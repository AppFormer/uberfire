package org.uberfire.client.views.pfly.menu;

import static java.util.Collections.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

public class MenusFixture {


    public static Menus buildTopLevelMenu() {

        return MenuFactory.newTopLevelMenu( "RIGHT" ).position( MenuPosition.RIGHT )
                .menus()
                .endMenus()
                .endMenu()
                .newTopLevelMenu( "CENTER" ).position( MenuPosition.CENTER )
                .menus()
                .endMenus()
                .endMenu()
                .newTopLevelMenu( "LEFT" ).position( MenuPosition.LEFT )
                .menus()
                .endMenus()
                .endMenu()
                .newTopLevelMenu( "RIGHT" ).position( MenuPosition.RIGHT )
                .menus()
                .endMenus()
                .endMenu()
                .build();

    }

    public static MenuItem buildMenuGroupItem() {

        Menus menu = buildMenuGroup();
        return  menu.getItems().get( 0 );
    }

    public static Menus buildMenuGroup() {
        return MenuFactory.newTopLevelMenu( "Screens" )
                    .menus()
                    .menu( "Hello Screen" ).endMenu()
                    .menu( "Mood Screen" ).endMenu()
                    .endMenus()
                    .endMenu()
                    .newTopLevelMenu( "Perspectives" )
                    .menus()
                    .menu( "Home Perspective" ).endMenu()
                    .menu( "Horizontal Perspective" ).endMenu()
                    .endMenus()
                    .endMenu()
                    .newTopLevelMenu( "Other" )
                    .menus()
                    .menu( "Alert Box" ).endMenu()
                    .endMenus()
                    .endMenu()
                    .build();
    }

    public static MenuItemCommand buildMenuItemCommand(){
        return new MenuItemCommand() {

            private final List<EnabledStateChangeListener> enabledStateChangeListeners = new ArrayList<EnabledStateChangeListener>();
            private boolean isEnabled = true;

            @Override
            public Command getCommand() {
                return mock(Command.class);  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public String getContributionPoint() {
                return "";
            }

            @Override
            public String getCaption() {
                return "";
            }

            @Override
            public MenuPosition getPosition() {
                return mock(MenuPosition.class);
            }

            @Override
            public int getOrder() {
                return 1;
            }

            @Override
            public boolean isEnabled() {
                return isEnabled;
            }

            @Override
            public void setEnabled( final boolean enabled ) {
                this.isEnabled = enabled;
                notifyListeners( enabled );
            }

            @Override
            public void addEnabledStateChangeListener( final EnabledStateChangeListener listener ) {
                enabledStateChangeListeners.add( listener );
            }

            @Override
            public String getSignatureId() {
                return "";
            }

            @Override
            public Collection<String> getRoles() {
                return mock(Collection.class);
            }

            @Override
            public Collection<String> getTraits() {
                return emptyList();
            }

            private void notifyListeners( final boolean enabled ) {
                for ( final EnabledStateChangeListener listener : enabledStateChangeListeners ) {
                    listener.enabledStateChanged( enabled );
                }
            }
        };
    }

}
