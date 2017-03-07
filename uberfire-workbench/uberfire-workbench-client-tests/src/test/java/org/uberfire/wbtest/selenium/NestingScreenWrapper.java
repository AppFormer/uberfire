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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.uberfire.wbtest.client.panels.docking.NestingScreen;
import org.uberfire.workbench.model.CompassPosition;

/**
 * A Selenium "page object" that abstracts interactions with a {@link NestingScreen} on a live webpage.
 */
public class NestingScreenWrapper {

    private final WebDriver driver;
    private final String positionTag;
    private final WebElement element;
    private final Map<CompassPosition, Integer> childCounts = new HashMap<CompassPosition, Integer>();

    public NestingScreenWrapper(WebDriver driver,
                                String positionTag) {
        this.driver = driver;
        this.positionTag = positionTag;
        element = driver.findElement(By.id("gwt-debug-NestingScreen-" + positionTag));
        if (element == null) {
            throw new IllegalStateException("NestingScreen " + positionTag + " not found!");
        }
    }

    /**
     * Adds a child panel at the given position and returns a wrapper for interacting with it.
     * @param position
     * @return
     */
    public NestingScreenWrapper addChild(CompassPosition position) {
        WebElement button = element.findElement(By.className(position.name().toLowerCase()));
        button.click();

        Integer childCount = childCounts.get(position);
        if (childCount == null) {
            childCount = 0;
        }
        childCounts.put(position,
                        childCount + 1);

        return new NestingScreenWrapper(driver,
                                        positionTag + position.name().charAt(0) + childCount);
    }

    /**
     * Checks if this screen (based on its position tag) is still in the DOM.
     */
    public boolean isStillInDom() {
        try {
            driver.manage().timeouts().implicitlyWait(10,
                                                      TimeUnit.MILLISECONDS);
            return driver.findElements(By.id("gwt-debug-NestingScreen-" + positionTag)).size() > 0;
        } finally {
            driver.manage().timeouts().implicitlyWait(10,
                                                      TimeUnit.SECONDS);
        }
    }

    /**
     * Checks if this screen's parent panel (based on its ID) is still in the DOM.
     */
    public boolean isPanelStillInDom() {
        try {
            driver.manage().timeouts().implicitlyWait(10,
                                                      TimeUnit.MILLISECONDS);
            return driver.findElements(By.id("NestingScreenPanel-" + positionTag)).size() > 0;
        } finally {
            driver.manage().timeouts().implicitlyWait(10,
                                                      TimeUnit.SECONDS);
        }
    }

    /**
     * Returns the location of the top left corner of this nesting screen's view.
     */
    public Point getLocation() {
        return element.getLocation();
    }

    /**
     * Returns the size of this nesting screen's view.
     */
    public Dimension getSize() {
        return element.getSize();
    }

    /**
     * Closes the place associated with this nesting screen.
     */
    public void close() {
        element.findElement(By.className("close")).click();
    }

    /**
     * Returns the Selenium WebElement that this object wraps.
     */
    public WebElement getWebElement() {
        return element;
    }
}
