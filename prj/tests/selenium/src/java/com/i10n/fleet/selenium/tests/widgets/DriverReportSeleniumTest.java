package com.i10n.fleet.selenium.tests.widgets;

import org.junit.After;

import com.i10n.fleet.selenium.tests.AbstractFleetSeleneseTestCase;
import com.thoughtworks.selenium.Wait;

/**
 * 
 * @author aravind Driver Report widget test
 * 
 */
public class DriverReportSeleniumTest extends AbstractFleetSeleneseTestCase {
    private static final String VIEW_URL = "/fleet/view/reports/?skin=openlayers";

    public void setUp() throws Exception {
        super.setUp(VIEW_URL);
    }

    /**
     * Tests for the widget initialization and the printpreview for the driver
     * report
     */
    public void loadDriverReport() {

        selenium.click("//div[@id='headersubnav']/div/ul/li[2]/div/a/em");
        selenium
                .click("//div[@id='driverlist']//div[@id='data-accordion']/div/div[2]/h3/a[1]");
        new Wait() {
            @Override
            public boolean until() {
                // TODO Auto-generated method stub
                return selenium.isVisible("driverspeedgraph");
            }
        };
    }

    /**
     * Selenium tests for the DriverSpeedGraph widget
     */
    public void testDriverSpeedGraphWidget() {

        loadDriverReport();
        selenium
                .click("//div[@id='driverlist']//div[@id ='data-accordion']/div/div[2]/h3/a[1]");
        selenium
                .click("//div[@id='driverlist']//div[@id ='data-accordion']/div/div[3]/h3/a[1]");
        selenium
                .click("//div[@id='driverlist']//div[@id ='data-accordion']/div/div[5]/h3/a[1]");
        if (selenium
                .getText(
                        "//div[@id='driverreport']//div[@id='graphreport']//div[@class='nested']/div[1]")
                .indexOf("Driver SpeedGraph") < 0) {
            fail("Driver graph widget not loaded");
        }

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
