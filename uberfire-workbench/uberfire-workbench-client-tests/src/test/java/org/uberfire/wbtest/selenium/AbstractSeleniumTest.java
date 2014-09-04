package org.uberfire.wbtest.selenium;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.uberfire.wbtest.client.main.DefaultScreenActivity;


public class AbstractSeleniumTest {

    static final int WINDOW_HEIGHT = 700;
    static final int WINDOW_WIDTH = 1000;

    protected WebDriver driver;
    protected String baseUrl;

    /**
     * Sets up the selenium driver, loads the default perspective, and waits for its screen to appear. This lets
     * subclass {@code @Before} methods or the tests themselves navigate directly to their screen or perspective of
     * interest.
     */
    @Before
    public final void setUp() throws Exception {
      driver = new FirefoxDriver();
      baseUrl = "http://localhost:8080/index.html";
      driver.manage().timeouts().implicitlyWait( 30, TimeUnit.SECONDS );
      driver.manage().window().setSize( new Dimension( AbstractSeleniumTest.WINDOW_WIDTH, AbstractSeleniumTest.WINDOW_HEIGHT ) );

      driver.get( baseUrl );
      waitForDefaultPerspective();
    }

    protected void waitForDefaultPerspective() {
        driver.findElement( By.id( "gwt-debug-" + DefaultScreenActivity.DEBUG_ID ) );
    }

    @After
    public void tearDown() throws Exception {
      driver.quit();
    }

}
