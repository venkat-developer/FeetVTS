package com.i10n.fleet.selenium.tests.views;

import org.junit.After;
import org.junit.Test;

import com.i10n.fleet.selenium.tests.AbstractFleetSeleneseTestCase;

/**
 * Selenium Test Case for Dashboard View.
 * 
 * @see AbstractFleetSeleneseTestCase
 * @author sabarish
 * 
 */
public class LiveTrackViewSeleniumTest extends AbstractFleetSeleneseTestCase {

    private static final String VIEW_URL = "/fleet/view/livetrack/?skin=openlayers";

    public void setUp() throws Exception {
        /**
         * TODO : LiveTrack Load takes time as selenium think thinks it is not
         * loaded until all the images are loaded. This needs to be fixed. This
         * works well without timeout using google rendering engine. But times
         * out for OpenLayers rendering Engine/GMap2 rendering engine.
         */
        super.setUpWithTimeOut(VIEW_URL, "120000");
    }

    /**
     * Test view markup load and behavior
     */
    @Test
    public void testView() throws Exception {
        verifyTrue(selenium.isElementPresent("header"));
        checkWidgetMarkupLoad("vehiclelist");
        checkWidgetMarkupLoad("headersubnav");

        /*
         * Test if the sidepane widget is loaded based on tab view
         */
        selenium.click("//div[@id='headersubnav']/div/div/ul/li[2]/div/a/em");
        verifyTrue(selenium.getAttribute("xpath=id('vehiclelist')@class").contains(
                "minimized"));
        selenium.click("//div[@id='headersubnav']/div/div/ul/li[1]/div/a/em");
        verifyTrue(!selenium.getAttribute("xpath=id('vehiclelist')@class").contains(
                "minimized"));

        /*
         * a few simple operations
         */
        selenium.select("//div[@id='sidepane-list']/div/div[3]/div[2]/div[3]/div/select",
                "label=North Zone");

        selenium.click("//div[@id='sidepane-list']/div/div[3]/div[2]/div[2]");
        selenium.click("//div[@id='sidepane-list']/div/div[3]/div[2]/div[1]");
        selenium.click("//div[@id='sidepane-list']/div/div[3]/div[1]/div[2]");
        selenium.click("//div[@id='vehiclelist']/div[1]/a");
        selenium.click("//div[@id='vehiclelist']/div[1]/a");
        selenium.click("//div[@id='data-accordion']/div/div[5]/h3/a[2]");
        selenium.click("//div[@id='data-accordion']/div/div[5]/h3/a[2]");
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