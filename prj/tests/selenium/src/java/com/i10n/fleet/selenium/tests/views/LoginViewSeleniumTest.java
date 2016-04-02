package com.i10n.fleet.selenium.tests.views;

import com.i10n.fleet.selenium.tests.AbstractFleetSeleneseTestCase;
import com.i10n.fleet.test.util.PropertyLoader;

/**
 * Selenium test case for Login View
 * 
 * @see AbstractFleetSeleneseTestCase
 * 
 */
public class LoginViewSeleniumTest extends AbstractFleetSeleneseTestCase {

    private static final String VIEW_URL = "/fleet/view/login/";

    private static final String PORT_PROP = "selenium.test.host.port";
    private static final String HOST_PROP = "selenium.test.host";

    public void setUp() throws Exception {
        super.setUp();

        selenium.open(VIEW_URL);
        selenium.waitForPageToLoad("3000");

        verifyEquals(selenium.getLocation(), "http://"
                + PropertyLoader.getProperty(HOST_PROP) + ":"
                + PropertyLoader.getProperty(PORT_PROP) + VIEW_URL);
    }

    /**
     * Test view markup load and behaviour
     */
    public void testMarkupLoad() {
        verifyTrue(selenium.isElementPresent("loginbanner"));
        assertTrue(selenium.isElementPresent("loginform"));
    }
}