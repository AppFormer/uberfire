package org.uberfire.workbench.model.menu.impl;

import java.util.*;

import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.commons.validation.PortablePreconditions.*;
import static java.util.Collections.*;

/**
 *
 */
public final class MenuBuilderImpl
        implements MenuFactory.MenuBuilder,
                   MenuFactory.ContributedMenuBuilder,
                   MenuFactory.TopLevelMenusBuilder,
                   MenuFactory.SubMenuBuilder,
                   MenuFactory.SubMenusBuilder,
                   MenuFactory.TerminalMenu {

    public enum MenuType {
        TOP_LEVEL, CONTRIBUTED, REGULAR, GROUP, CUSTOM
    }

    final List<MenuItem> menuItems = new ArrayList<MenuItem>();
    final Stack<MenuFactory.CustomMenuBuilder> context = new Stack<MenuFactory.CustomMenuBuilder>();

    private static class CurrentContext implements MenuFactory.CustomMenuBuilder {

        MenuItem menu = null;

        int order = 0;
        MenuType menuType = MenuType.REGULAR;
        String caption = null;
        Set<String> roles = new HashSet<String>();
        MenuPosition position = MenuPosition.LEFT;
        String contributionPoint = null;
        Command command = null;
        List<MenuItem> menuItems = new ArrayList<MenuItem>();
        Stack<MenuFactory.CustomMenuBuilder> menuRawItems = new Stack<MenuFactory.CustomMenuBuilder>();

        @Override
        public void push( MenuFactory.CustomMenuBuilder element ) {
            menuRawItems.push( element );
        }

        public MenuItem build() {
            if ( menu != null ) {
                return menu;
            }
            if ( menuItems.size() > 0 || menuRawItems.size() > 0 ) {
                if ( menuRawItems.size() > 0 ) {
                    for ( final MenuFactory.CustomMenuBuilder current : menuRawItems ) {
                        menuItems.add( current.build() );
                    }
                }
                return new MenuGroup() {
                    private final List<EnabledStateChangeListener> enabledStateChangeListeners = new ArrayList<EnabledStateChangeListener>();
                    private boolean isEnabled = true;

                    @Override
                    public List<MenuItem> getItems() {
                        return menuItems;
                    }

                    @Override
                    public String getContributionPoint() {
                        return contributionPoint;
                    }

                    @Override
                    public String getCaption() {
                        return caption;
                    }

                    @Override
                    public MenuPosition getPosition() {
                        return position;
                    }

                    @Override
                    public int getOrder() {
                        return order;
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
                        if ( contributionPoint != null ) {
                            return getClass().getName() + "#" + contributionPoint + "#" + caption;

                        }
                        return getClass().getName() + "#" + caption;
                    }

                    @Override
                    public Collection<String> getRoles() {
                        return roles;
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
            } else if ( command != null ) {
                return new MenuItemCommand() {

                    private final List<EnabledStateChangeListener> enabledStateChangeListeners = new ArrayList<EnabledStateChangeListener>();
                    private boolean isEnabled = true;

                    @Override
                    public Command getCommand() {
                        return command;  //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public String getContributionPoint() {
                        return contributionPoint;
                    }

                    @Override
                    public String getCaption() {
                        return caption;
                    }

                    @Override
                    public MenuPosition getPosition() {
                        return position;
                    }

                    @Override
                    public int getOrder() {
                        return order;
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
                        if ( contributionPoint != null ) {
                            return getClass().getName() + "#" + contributionPoint + "#" + caption;

                        }
                        return getClass().getName() + "#" + caption;
                    }

                    @Override
                    public Collection<String> getRoles() {
                        return roles;
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
            return new MenuItem() {

                private final List<EnabledStateChangeListener> enabledStateChangeListeners = new ArrayList<EnabledStateChangeListener>();
                private boolean isEnabled = true;

                @Override
                public String getContributionPoint() {
                    return contributionPoint;
                }

                @Override
                public String getCaption() {
                    return caption;
                }

                @Override
                public MenuPosition getPosition() {
                    return position;
                }

                @Override
                public int getOrder() {
                    return order;
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
                    if ( contributionPoint != null ) {
                        return getClass().getName() + "#" + contributionPoint + "#" + caption;

                    }
                    return getClass().getName() + "#" + caption;
                }

                @Override
                public Collection<String> getRoles() {
                    return roles;
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

    public MenuBuilderImpl( final MenuType menuType,
                            final String caption ) {
        final CurrentContext currentContext = new CurrentContext();
        currentContext.caption = checkNotEmpty( "caption", caption );
        currentContext.menuType = checkNotNull( "menuType", menuType );
        context.push( currentContext );
    }

    public MenuBuilderImpl( final MenuType menuType,
                            final MenuFactory.CustomMenuBuilder builder ) {
        context.push( builder );
    }

    @Override
    public MenuBuilderImpl newContributedMenu( final String caption ) {
        final CurrentContext currentContext = new CurrentContext();
        currentContext.caption = checkNotEmpty( "caption", caption );
        currentContext.menuType = MenuType.CONTRIBUTED;
        context.push( currentContext );

        return this;
    }

    @Override
    public MenuBuilderImpl newTopLevelMenu( final MenuItem menu ) {
        final CurrentContext currentContext = new CurrentContext();
        currentContext.menu = checkNotNull( "menu", menu );
        context.push( currentContext );

        return this;
    }

    @Override
    public MenuBuilderImpl newTopLevelMenu( final String caption ) {
        final CurrentContext currentContext = new CurrentContext();
        currentContext.caption = checkNotEmpty( "caption", caption );
        currentContext.menuType = MenuType.TOP_LEVEL;
        context.push( currentContext );

        return this;
    }

    @Override
    public MenuFactory.TerminalMenu newTopLevelCustomMenu( final MenuFactory.CustomMenuBuilder builder ) {
        context.push( builder );

        return this;
    }

    @Override
    public MenuBuilderImpl menu( final String caption ) {
        final CurrentContext currentContext = new CurrentContext();
        currentContext.caption = checkNotEmpty( "caption", caption );
        currentContext.menuType = MenuType.REGULAR;
        context.push( currentContext );

        return this;
    }

    @Override
    public MenuBuilderImpl menus() {
        ( (CurrentContext) context.peek() ).menuType = MenuType.GROUP;
        return this;
    }

    @Override
    public MenuFactory.TerminalMenu custom( MenuFactory.CustomMenuBuilder builder ) {
        context.push( builder );

        return this;
    }

    @Override
    public MenuBuilderImpl submenu( final String caption ) {
        final CurrentContext currentContext = new CurrentContext();
        currentContext.caption = checkNotEmpty( "caption", caption );
        currentContext.menuType = MenuType.GROUP;
        context.push( currentContext );

        return this;
    }

    @Override
    public MenuBuilderImpl contributeTo( final String contributionPoint ) {
        ( (CurrentContext) context.peek() ).contributionPoint = checkNotEmpty( "contributionPoint", contributionPoint );
        return this;
    }

    @Override
    public MenuBuilderImpl withItems( final List items ) {
        ( (CurrentContext) context.peek() ).menuItems = new ArrayList<MenuItem>( checkNotEmpty( "items", items ) );

        return this;
    }

    @Override
    public MenuBuilderImpl respondsWith( final Command command ) {
        ( (CurrentContext) context.peek() ).command = checkNotNull( "command", command );

        return this;
    }

    @Override
    public MenuBuilderImpl withRole( final String role ) {
        ( (CurrentContext) context.peek() ).roles.add( role );

        return this;
    }

    @Override
    public MenuBuilderImpl withRoles( Collection roles ) {
        for ( Iterator it = roles.iterator(); it.hasNext(); ) {
            String role = (String) it.next();
            ( (CurrentContext) context.peek() ).roles.add( role );
        }

        return this;
    }

    @Override
    public MenuBuilderImpl withRoles( final String... roles ) {
        for ( final String role : checkNotEmpty( "roles", roles ) ) {
            ( (CurrentContext) context.peek() ).roles.add( role );
        }

        return this;
    }

    @Override
    public MenuBuilderImpl order( final int order ) {
        ( (CurrentContext) context.peek() ).order = order;

        return this;
    }

    @Override
    public MenuBuilderImpl position( final MenuPosition position ) {
        ( (CurrentContext) context.peek() ).position = checkNotNull( "position", position );

        return this;
    }

    @Override
    public MenuBuilderImpl endMenus() {
        return this;
    }

    @Override
    public MenuBuilderImpl endMenu() {
        if ( context.size() == 1 ) {
            menuItems.add( context.pop().build() );
        } else {
            context.peek().push( context.pop() );
        }

        return this;
    }

    @Override
    public Menus build() {

        context.clear();

        return new Menus() {
            @Override
            public List<MenuItem> getItems() {
                return unmodifiableList( menuItems );
            }

            @Override
            public Map<Object, MenuItem> getItemsMap() {
                return new HashMap<Object, MenuItem>() {{
                    for ( final MenuItem menuItem : menuItems ) {
                        put( menuItem, menuItem );
                    }
                }};
            }
        };
    }
}
