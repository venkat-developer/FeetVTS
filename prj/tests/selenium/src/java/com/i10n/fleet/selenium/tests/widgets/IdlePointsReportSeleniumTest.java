package com.i10n.fleet.selenium.tests.widgets;

import org.junit.After;

import com.i10n.fleet.selenium.tests.AbstractFleetSeleneseTestCase;
import com.thoughtworks.selenium.Wait;

/**
 * Selenium Test Case for IdlePointsReport widget in Reports page
 * 
 * @see AbstractFleetSeleneseTestCase
 * @author irk
 * 
 */
public class IdlePointsReportSeleniumTest extends AbstractFleetSeleneseTestCase {

    private static final String VIEW_URL = "/fleet/view/reports/";

    /**
     * @see AbstractFleetSeleneseTestCase#setUp()
     */
    public void setUp() throws Exception {
        super.setUp(VIEW_URL);
    }

    public void testBehaviour() {
        /*
         * Test markup load
         */
        selenium.select("//div[@id='reporttimeframe']/div/div[6]/div/select",
                "label=Idle Points Report");
        new Wait("Widget Not Loaded") {
            public boolean until() {
                return selenium.isElementPresent("idlepointsreport");
            }
        };

        /*
         * Test vehicle selection
         */
        selenium.click("//div[@id='data-accordion']/div/div[2]/h3/a[1]");
        new Wait("Vehicle selection failed") {
            public boolean until() {
                return selenium.isElementPresent("//div[@id='idlepointsreport']/div/div")
                        && selenium.isVisible("//div[@id='idlepointsreport']/div/div");
            }
        };
        selenium
                .click("//div[@id='idlepointsreport']/div[1]/div/div[1]/div/div/div/div[2]");

        new Wait("Popup did not open!") {
            public boolean until() {
                return selenium.getAllWindowNames().length > 1;
            }
        };
        selenium.selectWindow(selenium.getAllWindowNames()[1]);
        verifyEquals("Report Print Preview", selenium.getTitle());
        selenium.close();
        selenium.selectWindow(null);
    }

    @After
    public void tearDown() throws Exception {
        doLogout();
        super.tearDown();
    }
}
