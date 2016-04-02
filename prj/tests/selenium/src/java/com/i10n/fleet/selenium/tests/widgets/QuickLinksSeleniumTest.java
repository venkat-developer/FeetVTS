package com.i10n.fleet.selenium.tests.widgets;

import org.junit.After;
import org.junit.Test;

import com.i10n.fleet.selenium.tests.AbstractFleetSeleneseTestCase;
import com.thoughtworks.selenium.Wait;

public class QuickLinksSeleniumTest extends AbstractFleetSeleneseTestCase {

    private static final String VIEW_URL = "/fleet/view/dashboard/";

    public void setUp() throws Exception {
        super.setUp(VIEW_URL);
    }

    @Test
    public void testCloseListElement() {
        /* Test for the close button in a link item */
        selenium.click("//div[@id='quicklinks']/div[1]/div[2]/div/div[1]/div/div[2]");
        new Wait("The Link was not removed!") {
            public boolean until() {
                return (!(selenium.getText("quicklinks").contains("Live Tracking")));
            }
        };
    }

    @Test
    public void testAddLinks() {
        /* Test for user behavior while adding links */
        testCloseListElement();
        selenium.click("link=Add Links");
        selenium.select("link", "label=Geo Fencing");
        selenium.click("//div[@id='buttons']/div/*/div/ul/li[2]");
        new Wait("The Link was not added!") {
            public boolean until() {
                return selenium.getText("quicklinks").contains("Geo Fencing");
            }
        };
    }

    @Test
    public void testLinkFollowed() {
        selenium.click("link=Live Tracking");
        new Wait("The livetrack did no open") {
            public boolean until() {
                return selenium.getLocation().contains("livetrack");
            }
        };
    }

    @After
    public void tearDown() throws Exception {
        doLogout();
        super.tearDown();
    }
}
