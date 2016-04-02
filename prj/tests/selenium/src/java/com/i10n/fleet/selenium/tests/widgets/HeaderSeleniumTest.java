package com.i10n.fleet.selenium.tests.widgets;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.After;
import org.junit.Test;

import com.i10n.fleet.selenium.tests.AbstractFleetSeleneseTestCase;
import com.thoughtworks.selenium.Wait;

/**
 * Selenium Test Case for testing Header behavior.
 * 
 * @see AbstractFleetSeleneseTestCase
 * @author sabarish
 * 
 */
public class HeaderSeleniumTest extends AbstractFleetSeleneseTestCase {

    private static final String VIEW_URL = "/fleet/view/dashboard/";
    private static final String LOGIN_URL = "/fleet/view/login/";

    /**
     * @see AbstractFleetSeleneseTestCase#setUp()
     */
    public void setUp() throws Exception {
        super.setUp(VIEW_URL);
    }

    /**
     * Tests Header behavior for various clicks
     */
    @Test
    public void testHeaderBehavior() {
        selenium.click("//div[@id='header']/div/div[1]/div[2]/ul/li[2]/div/a");
        new Wait("Livetrack page not loaded") {
            public boolean until() {
                return isCurrentURL("/fleet/view/livetrack/");
            }
        };

        selenium.click("//div[@id='header']/div/div[1]/div[2]/ul/li[3]/div/a");
        new Wait("Reports page not loaded") {
            public boolean until() {
                return isCurrentURL("/fleet/view/reports/");
            }
        };

        selenium.click("//div[@id='header']/div/div[1]/div[2]/ul/li[4]/div/a");
        new Wait("Control Panel page not loaded") {
            public boolean until() {
                return isCurrentURL("/fleet/view/controlpanel/");
            }
        };

        selenium.click("//div[@id='header']/div/div[1]/div[2]/ul/li[1]/div/a");
        new Wait("Dashboard page not loaded") {
            public boolean until() {
                return isCurrentURL("/fleet/view/dashboard/");
            }
        };
    }

    /**
     * Test view on clicking SubNav tabs after session close
     */
    @Test
    public void testViewAfterSessionClose() {
        selenium.open("/fleet/view/dashboard/");
        selenium.click("//div[@id='header']/div/div[1]/div[2]/ul/li[4]/div/a");
        selenium.waitForPageToLoad("120000");
        selenium.click("//div[@id='header']/div/div[1]/div[2]/ul/li[3]/div/a");
        selenium.waitForPageToLoad("120000");
        selenium.deleteCookie("JSESSIONID", "/fleet");
        selenium.click("//div[@id='headersubnav']/div/ul/li[3]/div/a/em");
        new Wait(" Not the expected view..!!") {
            @Override
            public boolean until() {
                try {
                    return (new URL(selenium.getLocation())).getPath().equals(
                            LOGIN_URL);
                } catch (MalformedURLException e) {
                    fail("Caught MalformedURLException while parsing location : "
                            + selenium.getLocation());
                }
                return false;
            }
        };
    }

    /**
     * Test if the required view is opened
     */
    private boolean isCurrentURL(String location) {
        URL url = null;
        try {
            url = new URL(selenium.getLocation());
        } catch (MalformedURLException e) {
            fail("Caught MalformedURLException while parsing location : "
                    + selenium.getLocation());
        }
        return url.getPath().equals(location);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        doLogout();
        super.tearDown();
    }
}