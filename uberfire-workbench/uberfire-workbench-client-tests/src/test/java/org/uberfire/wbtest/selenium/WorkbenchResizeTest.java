/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.wbtest.selenium;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.wbtest.client.headfoot.HeaderFooterActivator;
import org.uberfire.wbtest.client.perspective.ListPerspectiveActivity;
import org.uberfire.wbtest.client.perspective.SimplePerspectiveActivity;
import org.uberfire.wbtest.client.perspective.StaticPerspectiveActivity;
import org.uberfire.wbtest.client.perspective.TabbedPerspectiveActivity;
import org.uberfire.wbtest.client.resize.ResizeTestScreenActivity;
import org.uberfire.workbench.model.CompassPosition;


public class WorkbenchResizeTest extends AbstractSeleniumTest {

    @Test
    public void testDefaultPerspectiveSize() throws Exception {
        driver.get( baseUrl + "#" + ResizeTestScreenActivity.class.getName() + "?debugId=addedInDefaultPerspective" );
        ResizeWidgetWrapper widgetWrapper = new ResizeWidgetWrapper( driver, "addedInDefaultPerspective" );
        assertEquals( new Dimension( WINDOW_WIDTH, 20 ), widgetWrapper.getReportedSize() );
        assertEquals( new Dimension( WINDOW_WIDTH, 20 ), widgetWrapper.getActualSize() );
    }

    @Test
    public void testSimplePerspectiveSize() throws Exception {
        driver.get( baseUrl + "#" + SimplePerspectiveActivity.class.getName() );

        ResizeWidgetWrapper widgetWrapper = new ResizeWidgetWrapper( driver, "simplePerspectiveDefault" );
        assertEquals( new Dimension( WINDOW_WIDTH, 20 ), widgetWrapper.getReportedSize() );
        assertEquals( new Dimension( WINDOW_WIDTH, 20 ), widgetWrapper.getActualSize() );
    }

    @Test
    public void testListPerspectiveSize() throws Exception {
        driver.get( baseUrl + "#" + ListPerspectiveActivity.class.getName() );

        ResizeWidgetWrapper widgetWrapper = new ResizeWidgetWrapper( driver, "listPerspectiveDefault" );
        assertEquals( new Dimension( WINDOW_WIDTH, 20 ), widgetWrapper.getReportedSize() );
        assertEquals( new Dimension( WINDOW_WIDTH, 20 ), widgetWrapper.getActualSize() );
    }

    @Test
    public void testListPerspectiveSizeWithNestedPanels() throws Exception {
        driver.get( baseUrl + "#" + ListPerspectiveActivity.class.getName() );
        ResizeWidgetWrapper widgetWrapper = new ResizeWidgetWrapper( driver, "listPerspectiveDefault" );

        TopHeaderWrapper topHeaderWrapper = new TopHeaderWrapper( driver );
        topHeaderWrapper.addPanelToRoot( CompassPosition.WEST,
                                         MultiListWorkbenchPanelPresenter.class,
                                         ResizeTestScreenActivity.class,
                                         "id", "resize1" );

        Dimension sizeAfterWestPanelAdded = widgetWrapper.getActualSize();
        topHeaderWrapper.addPanelToRoot( CompassPosition.EAST,
                                         MultiListWorkbenchPanelPresenter.class,
                                         ResizeTestScreenActivity.class,
                                         "id", "resize2" );

        Dimension sizeAfterBothPanelsAdded = widgetWrapper.getActualSize();

        assertTrue( sizeAfterWestPanelAdded.width < WINDOW_WIDTH );
        assertTrue( sizeAfterBothPanelsAdded.width < sizeAfterWestPanelAdded.width );
    }

    @Test
    public void testTabbedPerspectiveSize() throws Exception {
        driver.get( baseUrl + "#" + TabbedPerspectiveActivity.class.getName() );

        ResizeWidgetWrapper widgetWrapper = new ResizeWidgetWrapper( driver, "tabbedPerspectiveDefault" );
        assertEquals( new Dimension( WINDOW_WIDTH, 20 ), widgetWrapper.getReportedSize() );
        assertEquals( new Dimension( WINDOW_WIDTH, 20 ), widgetWrapper.getActualSize() );
    }

    @Test
    public void testStaticPerspectiveSize() throws Exception {
        driver.get( baseUrl + "#" + StaticPerspectiveActivity.class.getName() );

        ResizeWidgetWrapper widgetWrapper = new ResizeWidgetWrapper( driver, "staticPerspectiveDefault" );
        assertEquals( new Dimension( WINDOW_WIDTH, 20 ), widgetWrapper.getReportedSize() );
        assertEquals( new Dimension( WINDOW_WIDTH, 20 ), widgetWrapper.getActualSize() );
    }

    /**
     * Ensures that empty headers and footers don't take up space on the screen.
     * <p>
     * This is an unfortunately detailed test. I'd rather be checking that the main panel is the full size of the
     * screen, but because of the way GWT's HeaderPanel works, if we stick ANYTHING (even an empty FlowPanel) into the
     * footer slot, an internal component with min-height 20px pops into existence. It sits on top of the main content,
     * which still claims to be the full size of the window. So I can't figure out a testable way to fix this other than
     * requiring that the footer container is absent from the document when it's not needed.
     */
    @Test
    public void ensureEmptyFooterIsNotAttachedToPage() throws Exception {
        driver.get( baseUrl + "?" + HeaderFooterActivator.DISABLE_PARAM + "=true" );
        skipUncaughtExceptionCheck = true;

        // the above is a full refresh of the app, so we have to wait for the bootstrap to finish
        waitForDefaultPerspective();

        // since we aren't expecting to find anything and the above line has already proven we're on the right page,
        // a short timeout is safe here.
        driver.manage().timeouts().implicitlyWait( 1, TimeUnit.SECONDS );
        List<WebElement> footers = driver.findElements( By.id( "gwt-debug-workbenchFooterPanel" ) );
        assertTrue( footers.isEmpty() );

        List<WebElement> headers = driver.findElements( By.id( "gwt-debug-workbenchHeaderPanel" ) );
        assertTrue( headers.isEmpty() );
    }

}
