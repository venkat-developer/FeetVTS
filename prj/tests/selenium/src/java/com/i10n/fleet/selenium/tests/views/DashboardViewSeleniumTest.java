package com.i10n.fleet.selenium.tests.views;

import org.junit.After;
import org.junit.Test;

import com.i10n.fleet.selenium.tests.AbstractFleetSeleneseTestCase;

/**
 * Selenium test case for DashBoard view
 * 
 * @see AbstractFleetSeleneseTestCase
 */
public class DashboardViewSeleniumTest extends AbstractFleetSeleneseTestCase {

    private static final String VIEW_URL = "/fleet/view/dashboard/";

    public void setUp() throws Exception {
        super.setUp(VIEW_URL);
    }

    /**
     * Test view markupLoad and behaviour
     */
    @Test
    public void testView() throws Exception {
        verifyTrue(selenium.isElementPresent("header"));
        checkHeaderLinks();
        checkWidgetMarkupLoad("dashboardvehiclestatus", "VEHICLE STATUS");
        checkWidgetMarkupLoad("vacantvehicles", "VACANT VEHICLES");

        /*
         * TO DO: Test dashboard vehicle status widget. After each click :
         * selenium.waitForPageToLoad(time); assert/verify(post-condition)
         */
        selenium.click("link=online");
        selenium.click("link=idle");
        selenium.click("link=offline");
        selenium.click("link=View All");
    }

    /**
     * Tests Support Desk Widget in Dashboard View
     */
    @Test
    public void testSupportDeskWidget() {

        checkWidgetMarkupLoad("supportdesk");

        selenium.select("issue-type", "label=Application Bug");
        selenium.isEditable("issue-description");
        selenium.type("issue-description", "This is a sample test");
        /*
         * TO DO: After typing the issue-description :
         * selenium.click("//*[@id='send']");
         * selenium.click("//*[@id='cancel']"); assert/verify(post-condition)
         */
    }

    /**
     * Tests Dashboard Violations Widget in Dashboard View
     */
    @Test
    public void testDashboardViolationsWidget() {

        checkWidgetMarkupLoad("dashboardviolations", "VIOLATION ALERTS");

        assertEquals("VIOLATION ALERTS", selenium
                .getText("//div[@id='dashboardviolations']/div/div[1]/div/div/div/div"));
        assertEquals("OVER SPEEDING", selenium
                .getText("//div[@id='tabview']/ul/li[1]/div/a/div"));
        assertEquals("GEO FENCING", selenium
                .getText("//div[@id='tabview']/ul/li[2]/div/a/div"));
        assertEquals("CHARGER DISCONNECTED", selenium
                .getText("//div[@id='tabview']/ul/li[3]/div/a/div"));
        selenium.click("//div[@id='tabview']/ul/li[1]/div/a/div");
        assertEquals("VEHICLE NAME", selenium
                .getTable("//div[@id='overspeeding']/table.0.0"));
        assertEquals("SPEED LIMIT", selenium
                .getTable("//div[@id='overspeeding']/table.0.3"));
        assertEquals("ORIGINAL SPEED", selenium
                .getTable("//div[@id='overspeeding']/table.0.4"));
        assertTrue(selenium
                .isElementPresent("//div[@id='overspeeding']/table/tbody/tr[1]/td[1]"));
        assertTrue(selenium
                .isElementPresent("//div[@id='overspeeding']/table/tbody/tr[2]/td[1]"));
        selenium.click("//div[@id='tabview']/ul/li[2]/div/a/div");
        assertEquals("ENTRY TIME", selenium
                .getTable("//div[@id='geo-fencing']/table.0.2"));
        assertEquals("EXIT TIME", selenium.getTable("//div[@id='geo-fencing']/table.0.3"));
        assertTrue(selenium
                .isElementPresent("//div[@id='geo-fencing']/table/tbody/tr[1]/td[1]"));
        assertTrue(selenium
                .isElementPresent("//div[@id='geo-fencing']/table/tbody/tr[2]/td[1]"));
        selenium.click("//div[@id='tabview']/ul/li[3]/div/a/div");
        assertEquals("TIME", selenium.getTable("//div[@id='chargerstatus']/table.0.2"));
        assertEquals("LOCATION", selenium
                .getTable("//div[@id='chargerstatus']/table.0.1"));
        assertTrue(selenium
                .isElementPresent("//div[@id='chargerstatus']/table/tbody/tr[1]/td[1]"));
        assertTrue(selenium
                .isElementPresent("//div[@id='chargerstatus']/table/tbody/tr[2]/td[1]"));
        assertEquals("View All", selenium
                .getText("//div[@id='dashboardviolations']/div/div[3]/div/div/div/div/a"));
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