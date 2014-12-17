package org.uberfire.client.views.pfly.tab;

import org.gwtbootstrap3.client.GwtBootstrap3EntryPoint;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class TabPanelWithDropdownsTest extends GWTTestCase {

    private TabPanelWithDropdowns tabPanel;

    @Override
    public String getModuleName() {
        return "org.uberfire.client.views.pfly.PatternFlyTabTests";
    }

    @Override
    protected void gwtSetUp() throws Exception {
        tabPanel = new TabPanelWithDropdowns();
        new GwtBootstrap3EntryPoint().onModuleLoad();
    }

    public void testAddTabByTitleAndContent() throws Exception {
        Label content = new Label( "First tab's content" );
        TabPanelEntry item = tabPanel.addItem( "First Tab", content );

        assertNotNull( item.getTabWidget() );
        assertNotNull( item.getContents() );
        assertEquals( item.getTitle(), "First Tab" );

        // the content should be attached
        assertNotNull( content.getParent() );
    }

    public void testShowTab() throws Exception {
        RootPanel.get().add( tabPanel );

        TabPanelEntry item1 = tabPanel.addItem( "First Tab", new Label( "First tab's content" ) );
        TabPanelEntry item2 = tabPanel.addItem( "Second Tab", new Label( "Second tab's content" ) );

        item2.showTab();
        item1.showTab();

        assertTrue( item1.getContentPane().isActive() );
        assertTrue( item1.getTabWidget().isActive() );

        assertFalse( item2.getContentPane().isActive() );
        assertFalse( item2.getTabWidget().isActive() );
    }

}
