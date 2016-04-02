package com.i10n.fleet.selenium.tests.widgets;

import com.i10n.fleet.selenium.tests.AbstractFleetSeleneseTestCase;
import com.thoughtworks.selenium.Wait;

public class ActivityReportTest extends AbstractFleetSeleneseTestCase {

    private static final String VIEW_URL = "/fleet/view/reports/";

    public void setUp() throws Exception {
        super.setUp(VIEW_URL);
    }

    public void loadActivityReport() {
        selenium.click("//div[@id='headersubnav']/div/ul/li[5]/div/a/em");
        new Wait() {
            @Override
            public boolean until() {
                return selenium.isElementPresent("activityreport");
            }
        };
    }

    public void testVehicleActivityWidget() {
        loadActivityReport();
        selenium.setSpeed("2000");
        selenium.click("//div[@id='vehiclelist']//div[@id ='data-accordion']/div/div[4]/h3/a[1]");

        new Wait() {
            @Override
            public boolean until() {
                return (!selenium.isVisible("//html/body/div[2]"));
            }
        };
        new Wait() {
            @Override
            public boolean until() {
                return ((selenium.isElementPresent("//div[@id='vehicleactivityreport']/div[1]/div/div/div/div[2]")));
            }
        };
        new Wait() {
            @Override
            public boolean until() {
                return selenium.getText("//div[@id='vehicleactivityreport']/div[1]/div/div/div/div[2]").equalsIgnoreCase("vehicle-133");
            }
        };
    }

    public void testActivityReportPopUp() {
        selenium.setSpeed("2000");
        loadActivityReport();
        selenium.click("//div[@id='vehiclelist']//div[@id ='data-accordion']/div/div[2]/h3/a[1]");
        new Wait() {
            @Override
            public boolean until() {
                return (!selenium.isVisible("//html/body/div[2]"));
            }
        };
        new Wait() {
            @Override
            public boolean until() {
                return ((selenium.isElementPresent("//div[@id='vehicleactivityreport']/div[2]/table/tbody[2]") && selenium
                        .getText("//div[@id='vehicleactivityreport']/div[2]/table/tbody[2]").length() > 0));
            }
        };

        selenium.click("//div[@id='vehicleactivityreport']/div[1]/div/div/div/div[3]");
        selenium.windowFocus();
        new Wait() {
            @Override
            public boolean until() {
                return (selenium.getAllWindowNames().length > 0);
            }
        };
        selenium.selectWindow(selenium.getAllWindowNames()[1]);
        selenium.close();
        selenium.selectWindow(null);
    }
}