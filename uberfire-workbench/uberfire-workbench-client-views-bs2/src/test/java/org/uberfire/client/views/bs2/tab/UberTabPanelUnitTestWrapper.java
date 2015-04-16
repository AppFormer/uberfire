package org.uberfire.client.views.bs2.tab;

import static org.mockito.Mockito.*;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;

import com.github.gwtbootstrap.client.ui.DropdownTab;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabLink;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

public class UberTabPanelUnitTestWrapper extends UberTabPanel {

    ResizeTabPanel tabPanelSpy;

    public UberTabPanelUnitTestWrapper( PanelManager panelManager ) {
        super( panelManager, mock( DropdownTab.class ) );
    }

    @Override
    Tab createTab( final WorkbenchPartPresenter.View view,
                   final boolean isActive ) {
        Tab mock = mock( Tab.class );
        when( mock.asTabLink() ).thenReturn( mock( TabLink.class ) );
        return mock;
    }

    public void setupMocks( WorkbenchDragAndDropManager dndManager ) {
        this.dndManager = dndManager;
        this.tabPanelSpy = spy(tabPanel);
        final ComplexPanel tabsWidget = mock( ComplexPanel.class );
        final ComplexPanel tabContentWidget = mock( ComplexPanel.class );
        when ( tabPanelSpy.getWidget( 0 ) ).thenAnswer( new Answer<Widget>() {
            @Override
            public Widget answer( InvocationOnMock invocation ) throws Throwable {
                return tabsWidget;
            }
        } );
        when ( tabPanelSpy.getWidget( 1 ) ).thenAnswer( new Answer<Widget>() {
            @Override
            public Widget answer( InvocationOnMock invocation ) throws Throwable {
                return tabContentWidget;
            }
        } );
        this.tabPanel = tabPanelSpy;
    }

    @Override
    boolean isFirstWidget() {
        return false;
    }
}
