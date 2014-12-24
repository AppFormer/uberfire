package org.uberfire.client.views.bs2.maximize;

import org.uberfire.client.workbench.panels.MaximizeToggleButtonPresenter;
import org.uberfire.client.workbench.panels.MaximizeToggleButtonPresenter.View;
import org.uberfire.commons.validation.PortablePreconditions;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonGroup;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * Bootstrap 2 based view for {@link MaximizeToggleButtonPresenter}.
 * <p>
 * When used, this button should normally be nested inside a {@link ButtonGroup} with a 10 pixel right margin, a 4
 * pixel top margin, and the {@code pull-right} style class applied.
 */
public class MaximizeToggleButton extends Button implements View {

    private MaximizeToggleButtonPresenter presenter;

    public MaximizeToggleButton() {
        setIcon( IconType.CHEVRON_UP );
        setIconSize( IconSize.SMALL );
        setSize( ButtonSize.MINI );
        addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.handleClick();
            }
        } );
    }

    @Override
    public void setMaximized( boolean maximized ) {
        if ( maximized ) {
            setIcon( IconType.CHEVRON_DOWN );
        } else {
            setIcon( IconType.CHEVRON_UP );
        }
    }

    @Override
    public void init( MaximizeToggleButtonPresenter presenter ) {
        this.presenter = PortablePreconditions.checkNotNull( "presenter", presenter );
    }
}
