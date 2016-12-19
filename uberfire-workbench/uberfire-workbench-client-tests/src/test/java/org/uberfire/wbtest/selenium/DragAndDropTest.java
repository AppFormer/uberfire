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
import static org.uberfire.wbtest.selenium.UberAssertions.*;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.uberfire.client.workbench.widgets.listbar.ListBarWidget;
import org.uberfire.wbtest.client.dnd.DragAndDropPerspective;
import org.uberfire.workbench.model.CompassPosition;

/**
 * Tests for drag-and-drop movement of parts.
 */
public class DragAndDropTest extends AbstractSeleniumTest {

    @Before
    public void setUpPerspective() {
        driver.get( baseUrl + "#" + DragAndDropPerspective.class.getName() );
    }

    @Test
    public void testDragFromListPanelToSouthOfSelf() throws Exception {
        WebElement listDragHandle = driver.findElement( By.id( "gwt-debug-" + ListBarWidget.DEBUG_TITLE_PREFIX + "DnD-2" ) );

        // make sure we're grabbing the right thing
        assertEquals( "DnD-2", listDragHandle.getText() );

        // get the compass to appear
        Actions dragAndDrop = new Actions( driver );
        dragAndDrop.clickAndHold( listDragHandle );
        dragAndDrop.moveByOffset( 0, 50 );
        dragAndDrop.perform();

        // now find the south point of the compass and drop on it
        WebElement compassSouth = driver.findElement( By.id( "gwt-debug-CompassWidget-south" ) );
        dragAndDrop.click( compassSouth );
        dragAndDrop.perform();

        // The DnD-1 screen should have appeared now to take the place of DnD-2, which we have moved away
        WebElement dnd1Screen = driver.findElement( By.id( "DragAndDropScreen-1" ) );
        WebElement dnd2Screen = driver.findElement( By.id( "DragAndDropScreen-2" ) );

        // to prove it worked, we should ensure DnD-2 is south of DnD-1
        assertTrue( dnd1Screen.isDisplayed() );
        assertRelativePosition( CompassPosition.SOUTH, dnd1Screen, dnd2Screen );
    }

    @Test
    public void dragCompassShouldBeCenteredOverRootListTargetPanel() throws Exception {
        WebElement listDragHandle = driver.findElement( By.id( "gwt-debug-" + ListBarWidget.DEBUG_TITLE_PREFIX + "DnD-2" ) );

        // make sure we're grabbing the right thing
        assertEquals( "DnD-2", listDragHandle.getText() );

        // get the compass to appear over the root panel (which is a MultiList panel)
        WebElement listPanel = driver.findElement( By.id( "DragAndDropPerspective-list" ) );
        Actions dragAndDrop = new Actions( driver );
        dragAndDrop.clickAndHold( listDragHandle );
        dragAndDrop.moveToElement( listPanel );
        dragAndDrop.perform();

        // now find the south point of the compass and drop on it
        WebElement compassCenter = driver.findElement( By.id( "gwt-debug-CompassWidget-centre" ) );
        assertCentered( listPanel, compassCenter );
    }

    @Test
    public void dragCompassShouldBeCenteredOverWestTabbedTargetPanel() throws Exception {
        WebElement listDragHandle = driver.findElement( By.id( "gwt-debug-" + ListBarWidget.DEBUG_TITLE_PREFIX + "DnD-2" ) );

        // make sure we're grabbing the right thing
        assertEquals( "DnD-2", listDragHandle.getText() );

        // get the compass to appear over the west panel (a MultiTab panel)
        WebElement tabPanel = driver.findElement( By.id( "DragAndDropPerspective-tab" ) );
        Actions dragAndDrop = new Actions( driver );
        dragAndDrop.clickAndHold( listDragHandle );
        dragAndDrop.moveToElement( tabPanel );
        dragAndDrop.perform();

        // now find the south point of the compass and drop on it
        WebElement compassCenter = driver.findElement( By.id( "gwt-debug-CompassWidget-centre" ) );
        assertCentered( tabPanel, compassCenter );
    }

}
