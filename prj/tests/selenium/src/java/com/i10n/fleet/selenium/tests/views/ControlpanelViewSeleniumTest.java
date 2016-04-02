package com.i10n.fleet.selenium.tests.views;

import org.junit.After;

import com.i10n.fleet.selenium.tests.AbstractFleetSeleneseTestCase;
import com.thoughtworks.selenium.Wait;

/**
 * Selenium test case for ControlPanel view
 * 
 * @see AbstractFleetSeleneseTestCase
 */
public class ControlpanelViewSeleniumTest extends AbstractFleetSeleneseTestCase {

    private static final String VIEW_URL = "/fleet/view/controlpanel/?merge=false";

    public void setUp() throws Exception {
        super.setUp(VIEW_URL);
    }

    /**
     * Test the markup load and behaviour of the view
     */
    public void testView() {
        verifyTrue(selenium.isElementPresent("header"));
        verifyTrue(selenium.isElementPresent("headersubnav"));
        checkHeaderLinks();
        /*
         * Test for Trip-settings widget
         */
        selenium.click("//div[@id='headersubnav']/div/div/ul/li[1]/div/a/em");
        new Wait("Trip Settings did not load.") {
            public boolean until() {
                return selenium.isElementPresent("tripsettings");
            }
        }.wait("Trip Settings did not load.", 60000);
        /*
         * TODO: After each clicks assertion for the post-conditions should be
         * added
         */
        selenium.click("//div[@id='headersubnav']/div/div/ul/li[2]/div/a/em");
        verifyTrue(selenium.isElementPresent("//div[@id='buttons']"));

        /**
         * Test for Report-settings widget
         */
        selenium.click("//div[@id='headersubnav']/div/div/ul/li[3]/div/a/em");
        new Wait("Error: Widget not loaded.. !!") {
            @Override
            public boolean until() {
                return selenium.isElementPresent("reportsettings");
            }
        }.wait("Report Settings did not load.", 60000);
        selenium.type("//input[@name='name']", "username");
        selenium.type("//input[@name='email']", "user@i10n.com");
        selenium.click("//input[@name='all']");
        selenium.click("//input[@name='schedule' and @value='Weekly']");
        selenium.click("//div[4]/div[@id='buttons']/div/*/div/ul/li[2]");
        /**
         * Test for Alert-settings widget
         */
        selenium.click("//div[@id='headersubnav']/div/div/ul/li[4]/div/a/em");
        new Wait("Alert Settings Widget Not Loaded!") {
            @Override
            public boolean until() {
                return selenium.isElementPresent("alertsettings");
            }
        }.wait("Alert Settings did not load.", 60000);
        selenium.type("//div[@id='alertsettingslist']//input[@name='name']", "username");
        selenium.type("//div[@id='alertsettingslist']//input[@name='email']",
                "user@i10n.com");
        selenium.click("//div[@id='alertsettingslist']//input[@name='all']");
        selenium.click("//div[4]/div[@id='buttons']/div/*/div/ul/li[2]");
        /*
         * Test for System-Settings Widget
         */
        selenium.click("//div[@id='headersubnav']/div/div/ul/li[5]/div/a/em");
        new Wait("Widget not loaded.. !!") {
            @Override
            public boolean until() {
                return selenium.isElementPresent("systemsettings");
            }
        };
        checkWidgetMarkupLoad("systemsettings");
        selenium.click("fetch-method");
        selenium.click("//div[2]/div[2]/div[@id='buttons']/div/*/div/ul/li[2]");
        selenium.type("//input[@id='search-string' and @value='Search']", "testuser");
        selenium.click("//div/div[1]/div/div/div/div[1]/div[3]/div[@id='buttons']/div/*/div/ul/li[2]");
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