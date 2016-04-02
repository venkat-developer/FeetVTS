package com.i10n.fleet.selenium.tests.views;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.i10n.fleet.selenium.tests.AbstractFleetSeleneseTestCase;
import com.thoughtworks.selenium.Wait;

/**
 * Selenium test case for Reports View
 * 
 * @see AbstractFleetSeleneseTestCase
 */
public class ReportsViewSeleniumTest extends AbstractFleetSeleneseTestCase {

    private static final String VIEW_URL = "/fleet/view/reports/?skin=openlayers";

    @Before
    public void setUp() throws Exception {
        /**
         * TODO : Maps Load takes time as selenium think thinks it is not loaded
         * until all the images are loaded. This needs to be fixed. This works
         * well without timeout using google rendering engine. But times out for
         * OpenLayers rendering Engine/GMap2 rendering engine.
         */
        super.setUpWithTimeOut(VIEW_URL, "120000");
    }

    /**
     * Test markup load and behaviour of the view
     */

    @Test
    public void testView() {
        verifyTrue(selenium.isElementPresent("header"));
        verifyTrue(selenium.isElementPresent("headersubnav"));
        checkHeaderLinks();
        selenium.click("//div[@id='headersubnav']/div/ul/li[4]/div/a/em");
        selenium.click("//div[@id='headersubnav']/div/ul/li[5]/div/a/em");
        selenium.click("//div[@id='headersubnav']/div/ul/li[1]/div/a/em");

        /**
         * To test VehicleReport Widget
         */
        selenium
                .click("//div[@id='vehiclelist']//div[@id ='data-accordion']/div/div[2]/h3/a[1]");
        new Wait("Markers Not Loaded") {

            @Override
            public boolean until() {
                return selenium
                        .isElementPresent("//div[@id='vehiclemapreport']//div[@class='map-marker-icon']")
                        && selenium
                                .isVisible("//div[@id='vehiclemapreport']//div[@class='map-marker-icon']");
            }
        }.wait("Markers Not Loaded", 120000);
        selenium
                .click("//input[@id='time-frame' and @name='radiogroup' and @value='Custom']");
        selenium.isVisible("//div[@id='overlay']");
        assertEquals(
                "CUSTOMIZED REPORT",
                selenium
                        .getText("//div[@id='customreport']//div[@id='yui-gen1']/div[1]/div/div/div/div/table/tbody/tr/td[1]/div"));
    }

    /**
     * Tests VehicleReport Widget & SpeedGraphReport Widget in Reports View
     */
    @Test
    public void testVehicleReport() {

        selenium.click("//div[@id='headersubnav']/div/ul/li[1]/div/a/em");
        selenium.select("//div[@id='reporttimeframe']/div/div[6]/div/select",
                "label=Speed Graph Report");
        selenium.click("//*[@class='item-name inline-block'][1]");
        selenium.click("//div[@id='buttons']/div/div/div/ul/li[2]/div");
        selenium
                .click("//div[@id='vehiclegraphreport']//div[@id='graphreport']//div[@class='inline-block print-preview']");
        selenium.waitForPopUp("popup", "30000");
        selenium.selectWindow("name=popup");
        verifyTrue(selenium.isElementPresent("//div[@id='graphreport']"));

    }

    /**
     * Tests GroupedReport Widget in Reports View
     */
    @Test
    public void testGroupedReport() {
        /**
         * TODO: After each clicks assertion for post-condition should be added
         */
        selenium.click("//div[@id='headersubnav']/div/ul/li[3]/div/a/em");

        /**
         * Test for VehicleStatsReport widget
         */
        new Wait("Error: Widget not loaded.. !!") {
            public boolean until() {
                return selenium
                        .isElementPresent("//div[@id='groupedreport']/div[1]/div/div/div/div[2]/select");
            }
        };

        checkWidgetMarkupLoad("groupedreport");
        selenium.select("//div[@id='groupedreport']/div[1]/div/div/div/div[2]/select",
                "label=East Zone");
        selenium.click("link=None");
        selenium.click("link=Page");
        selenium.click("link=All");
        selenium.select("//div[@id='groupedreport']/div[1]/div/div/div/div[2]/select",
                "label=East Zone");
        selenium.select("//div[@id='groupedreport']/div[1]/div/div/div/div[2]/select",
                "label=All");
        selenium.click("yui-pg0-0-next-link");
        new Wait("Next Page Not Loaded!") {
            public boolean until() {
                return selenium.isElementPresent("yui-pg0-0-prev-link");
            }
        };
        selenium.click("yui-pg0-0-prev-link");
        selenium.click("//div[@id='groupedreport']/div[1]/div/div/div/div[3]");
    }

    /**
     * Tests Tab View of Violation Reports
     */
    @Test
    public void testViolationReportsTabs() {

        /**
         * Clicking on Violation Report Tab
         */
        selenium.click("//div[@id='headersubnav']/div/ul/li[4]/div/a/em");
        /**
         * Test for Violation Report widget
         */
        new Wait("Error: Widget not loaded.. !!") {
            @Override
            public boolean until() {
                return selenium.isElementPresent("violationreport");
            }
        };
        assertTrue(selenium.isElementPresent("chargerstatus"));
        assertTrue(selenium.isElementPresent("geofencing"));
        assertTrue(selenium.isElementPresent("overspeeding"));
        assertFalse(selenium.getAttribute("//div[@id='overspeeding']@class").contains(
                "yui-hidden"));
        assertTrue(selenium.getAttribute("//div[@id='geofencing']@class").contains(
                "yui-hidden"));
        assertTrue(selenium.getAttribute("//div[@id='chargerstatus']@class").contains(
                "yui-hidden"));
        /**
         * Clicking on Geo Fencing Violations
         */
        selenium.click("//div[@id='tabview']/ul/li[2]/div/a/div");
        assertFalse(selenium.getAttribute("//div[@id='geofencing']@class").contains(
                "yui-hidden"));
        assertTrue(selenium.getAttribute("//div[@id='overspeeding']@class").contains(
                "yui-hidden"));
        assertTrue(selenium.getAttribute("//div[@id='chargerstatus']@class").contains(
                "yui-hidden"));
        /**
         * Clicking on Charger Violations
         */
        selenium.click("//div[@id='tabview']/ul/li[3]/div/a/div");
        assertFalse(selenium.getAttribute("//div[@id='chargerstatus']@class").contains(
                "yui-hidden"));
        assertTrue(selenium.getAttribute("//div[@id='overspeeding']@class").contains(
                "yui-hidden"));
        assertTrue(selenium.getAttribute("//div[@id='geofencing']@class").contains(
                "yui-hidden"));
        /**
         * Clicking on Over Speed Violations
         */
        selenium.click("//div[@id='tabview']/ul/li[1]/div/a/div");
        assertFalse(selenium.getAttribute("//div[@id='overspeeding']@class").contains(
                "yui-hidden"));
        assertTrue(selenium.getAttribute("//div[@id='geofencing']@class").contains(
                "yui-hidden"));
        assertTrue(selenium.getAttribute("//div[@id='chargerstatus']@class").contains(
                "yui-hidden"));
    }

    /**
     * Tests behavior of violations report
     */
    @Test
    public void testViolationReportsBehavior() {
        /**
         * Clicking on Violation Report Tab
         */
        selenium.click("//div[@id='headersubnav']/div/ul/li[4]/div/a/em");
        /**
         * Test for Violation Report widget
         */
        new Wait("Error: Widget not loaded.. !!") {
            @Override
            public boolean until() {
                return selenium
                        .isElementPresent("//div[2]/div/div/div[1]/div/div[1]/div/div/div/div[2]/select");
            }
        };
        selenium.select("//div[2]/div/div/div[1]/div/div[1]/div/div/div/div[2]/select",
                "label=South Zone");
        selenium.select("//div[2]/div/div/div[1]/div/div[1]/div/div/div/div[2]/select",
                "label=East Zone");
        selenium.click("//div[2]/div/div/div[1]/div/div[1]/div/div/div/div[3]");
    }

    /**
     * Test Vehicle Statistics Widget
     */
    @Test
    public void testVehicleStatisticsWidget() {
        /**
         * Select Vehicle Statistics Widget
         */
        selenium.select("//div[@id='reporttimeframe']/div/div[6]/div/select",
                "label=Vehicle Statistics");
        new Wait("Widget Not Loaded") {
            @Override
            public boolean until() {
                return selenium.isElementPresent("vehiclestatistics");
            }
        };

        /**
         * Select a Vehicle
         */
        selenium.click("//div[@id='data-accordion']/div/div[2]/h3/a[1]");

        /**
         * Test for print-preview pop-up
         */
        selenium.click("//div[@id='vehiclestatistics']/div[1]/div[1]/div/div/div/div[3]");
        selenium.waitForPopUp("popup", "30000");
        selenium.selectWindow("name=popup");
        verifyEquals("Report Print Preview", selenium.getTitle());
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