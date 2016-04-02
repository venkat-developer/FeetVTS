package com.i10n.fleet.selenium.tests.widgets;

import org.junit.After;
import org.junit.Test;

import com.i10n.fleet.selenium.tests.AbstractFleetSeleneseTestCase;
import com.thoughtworks.selenium.Wait;

public class TripSettingsSeleniumTest extends AbstractFleetSeleneseTestCase {

    private static final String VIEW_URL = "/fleet/view/controlpanel/";

    public void setUp() throws Exception {
        super.setUp(VIEW_URL);
    }

    @Test
    public void testAddTripClicked() {
        /* Opening the PopUp */
        new Wait("Trip Settings Not loaded") {

            @Override
            public boolean until() {
                return selenium
                        .isElementPresent("//div[@class='create-trip']//div[@id='yui-gen0_c']");
            }
        };
        selenium.click("//div[@id='buttons']/div//div/ul/li[2]");
        if (!selenium.isVisible("//div[@class='create-trip']//div[@id='yui-gen0_c']")) {
            fail("The popup is not visible");
        }
    }

    @Test
    public void testSubmitClicked() {
        /* Opening the PopUp and clicking the submit button */
        new Wait("Trip Settings Not loaded") {

            @Override
            public boolean until() {
                return selenium
                        .isElementPresent("//div[@class='create-trip']//div[@id='yui-gen0_c']");
            }
        };
        selenium.click("//div[@id='buttons']/div//div/ul/li[2]");
        selenium.click("//form/div[2]/div[1]/div//div/ul/li[2]");
        /* Checking whether the popup is invisible */
        if (selenium.isVisible("//div[@class='create-trip']//div[@id='yui-gen0_c']")) {
            fail("The popup is still visible");
        }

        /*
         * Checking whether an item is added into the sidepane list - atleast
         * one should be present
         */
        new Wait("Item not added") {

            @Override
            public boolean until() {
                return selenium
                        .isElementPresent("//div[@class='slist-item list-item-type item']");
            }
        };
        if (!selenium.isElementPresent("//div[@class='slist-item list-item-type item']")) {
            fail("The new Trip Element is not added to the side list");
        }
    }

    @Test
    public void testCancelClicked() {
        /* Initiating the action */
        new Wait("Trip Settings Not loaded") {

            @Override
            public boolean until() {
                return selenium
                        .isElementPresent("//div[@class='create-trip']//div[@id='yui-gen0_c']");
            }
        };
        selenium.click("//div[@id='buttons']/div//div/ul/li[2]");
        selenium.type("speed-limit", "t");
        selenium.click("//form/div[2]/div[2]/div//div/ul/li[2]");
        /* Does the element still have text? */
        if (!selenium.getValue("//input[@class='inline-block text-box input-element']")
                .isEmpty()) {
            fail("Some of the input elements are not empty");
        }
        /* The popup should still be visible */
        if (!selenium.isVisible("//div[@class='create-trip']//div[@id='yui-gen0_c']")) {
            fail("The popup is not visible");
        }
    }

    @Test
    public void testClose() {
        /* Initiating the action */
        new Wait("Trip Settings Not loaded") {

            @Override
            public boolean until() {
                return selenium
                        .isElementPresent("//div[@class='create-trip']//div[@id='yui-gen0_c']");
            }
        };
        selenium.click("//div[@id='buttons']/div//div/ul/li[2]");
        selenium
                .click("//div[@id='yui-gen0']/div[1]/div/div/div/div/table/tbody/tr/td[2]/a");
        /* The popup should disappear */
        if (selenium.isVisible("//div[@class='create-trip']//div[@id='yui-gen0_c']")) {
            fail("The popup is not visible");
        }
    }

    @Test
    public void testSelected() {
        /* Setting up the test */
        new Wait("Trip Settings Not loaded") {

            @Override
            public boolean until() {
                return selenium.isElementPresent("//div[3]/div//div/ul/li[2]");
            }
        };
        selenium
                .click("//*[@class='item-header hd']/*[@class='item-name event-source inline-block']");
        /* Checking whether the necessary detail is displayed */
        new Wait("Trip Details!") {
            @Override
            public boolean until() {
                return selenium
                        .isElementPresent("//div[@class='inline-block content data']");
            }
        };
        if (selenium.getText("//div[@class='inline-block content data']").isEmpty()) {
            fail("The  item is not displayed");
        }
    }

    @Test
    public void testPinned() {
        /* Setting up the test */
        new Wait("Trip Settings Not loaded") {

            @Override
            public boolean until() {
                return selenium.isElementPresent("//div[3]/div//div/ul/li[2]");
            }
        };
        selenium.click("//div[@id='data']/div/div[2]/h3/p");
        /* If a pinned element is not present, fail */
        if (!selenium
                .isElementPresent("//*[@class='slist-item list-item-type item pin-enabled']")) {
            fail("The pin is not attached to the element");
        }
    }

    @Test
    public void testEditableDivsEdit() {
        /* Setting up the test */
        new Wait("Trip Settings Not loaded") {

            @Override
            public boolean until() {
                return selenium.isElementPresent("//div[3]/div//div/ul/li[2]");
            }
        };
        selenium
                .click("//*[@class='item-header hd']/*[@class='item-name event-source inline-block']");
        /* Setting up the test */
        new Wait("Trip Settings Not loaded") {

            @Override
            public boolean until() {
                return selenium.isElementPresent("link=Edit");
            }
        };
        selenium.click("link=Edit");
        if (!selenium.isElementPresent("//input[@class='editablediv input']")) {
            fail("The Input box is not added porperly");
        }
    }

    @Test
    public void testEditableDivsSubmit() {
        /* The box will already be in the edit state due to the previous test */
        new Wait("Trip Settings Not loaded") {

            @Override
            public boolean until() {
                return selenium.isElementPresent("//div[3]/div//div/ul/li[2]");
            }
        };
        selenium
                .click("//*[@class='item-header hd']/*[@class='item-name event-source inline-block']");
        /* Setting up the test */
        new Wait("Trip Settings Not loaded") {

            @Override
            public boolean until() {
                return selenium.isElementPresent("link=Edit");
            }
        };
        selenium.click("link=Edit");
        selenium.click("link=Submit");
        if (selenium.isElementPresent("//input[@class='editablediv input']")) {
            fail("The Input box is not closed porperly");
        }
    }

    @After
    public void tearDown() throws Exception {
        doLogout();
        super.tearDown();
    }
}
