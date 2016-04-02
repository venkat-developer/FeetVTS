package com.i10n.fleet.selenium.tests.widgets;

import org.junit.After;
import org.junit.Test;

import com.i10n.fleet.selenium.tests.AbstractFleetSeleneseTestCase;
import com.thoughtworks.selenium.Wait;

public class GraphReportSeleniumTest extends AbstractFleetSeleneseTestCase {

    private static final String VIEW_URL = "/fleet/view/reports/";

    public void setUp() throws Exception {
        super.setUp(VIEW_URL);
    }

    @Test
    public void testPrintPreview() {
        /* Test for the close button in a link item */
        new Wait("Element not present") {
            public boolean until() {
                return (selenium
                        .isElementPresent("//div[@id='reporttimeframe']/div/div[6]/div/select"));
            }
        };
        selenium.select("//div[@id='reporttimeframe']/div/div[6]/div/select",
                "label=Fuel Graph Report");
        selenium.click("//div[@id='graphreport']/div[1]/div/div/div/div[2]/div");
        /* If the popup is not opened, the wait will time out and throw an error */
        new Wait("Popup Not opened") {
            public boolean until() {
                return (selenium.getAllWindowNames().length > 1);
            }
        };
    }

    @After
    public void tearDown() throws Exception {
        doLogout();
        super.tearDown();
    }
}
