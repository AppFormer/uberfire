package org.uberfire.client.workbench.panels.impl;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.widgets.listbar.ListBarWidget;
import org.uberfire.mvp.Command;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class SimpleWorkbenchPanelViewTest extends AbstractDockingWorkbenchPanelViewTest {

    @InjectMocks
    private SimpleWorkbenchPanelView view;

    @Mock
    private PanelManager panelManager;

    @Mock
    private ListBarWidget listBar;
    private final Element listBarElement = mock( Element.class );
    private final Style listBarElementStyle = mock( Style.class );

    // Not a @Mock or @GwtMock because we want to test the view.init() method
    private SimpleWorkbenchPanelPresenter presenter;

    @Before
    public void setup() {
        Widget listBarWidget = mock( Widget.class );
        when( listBar.asWidget() ).thenReturn( listBarWidget );
        when( listBarWidget.getElement() ).thenReturn( listBarElement );
        when( listBarElement.getStyle() ).thenReturn( listBarElementStyle );

        presenter = mock( SimpleWorkbenchPanelPresenter.class );

        view.setup(); // PostConstruct
        view.init( presenter );
    }

    @Override
    protected AbstractDockingWorkbenchPanelView<?> getViewToTest() {
        return view;
    }

    @Test
    public void shouldAddPresenterOnInit() {
        assertEquals( presenter, view.getPresenter() );
    }

    @Test
    public void shouldSetupDragAndDropOnListBar() {
        verify( listBar ).setDndManager( eq( dndManager ) );
        verify( listBar ).setup( false, false );
        verify( listBar ).addSelectionHandler( any( SelectionHandler.class ) );
        verify( listBar ).addSelectionHandler( any( SelectionHandler.class ) );
        verify( listBar ).addOnFocusHandler( any( Command.class ) );
    }

    @Test
    public void onResize() {

        view.setPixelSize( 10, 10 );
        view.onResize();

        // unfortunately, setPixelSize() doesn't have any side effects during unit tests so we can't verify the arguments
        verify( presenter ).onResize( any( Integer.class ), any( Integer.class ) );

        verify( topLevelWidget ).onResize();
    }


}
