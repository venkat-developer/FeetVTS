package com.i10n.fleet.selenium.tests.widgets;

import org.junit.After;
import org.junit.Before;

import com.i10n.fleet.selenium.tests.AbstractFleetSeleneseTestCase;

/**
 * Selenium test for the login form widget
 * 
 * @see AbstractFleetSeleneseTestCase @
 */
public class LoginFormSeleniumTest extends AbstractFleetSeleneseTestCase {

    private static final String LOGIN_URL = "/fleet/view/login/";

    /**
     * Setup
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        doLogout();
        selenium.open(LOGIN_URL);
        selenium.waitForPageToLoad("3000");
    }

    /**
     * Tests the login form of Login view
     */
    public void testLoginForm() {

        verifyTrue(selenium.isElementPresent("loginform"));
        verifyTrue(selenium.isEditable("login.user"));
        verifyTrue(selenium.isEditable("login.password"));
        /**
         * Trying to check if Application allows a user page to be opened before
         * logging in.
         */
        selenium.open("/fleet/view/dashboard/");
        verifyFalse(selenium.getLocation().contains("/fleet/view/dashboard/"));
        verifyTrue(selenium.getLocation().contains("/fleet/view/login/"));
        /**
         * Logging in.
         */
        doLogin("/fleet/view/dashboard/");
        /**
         * Verifying current page.
         */
        verifyTrue(selenium.getLocation().contains("/fleet/view/dashboard/"));
        verifyTrue(!selenium.getCookieByName("JSESSIONID").isEmpty());
        doLogout();
        /**
         * Verifying prohibition of opening user page after logout.
         */
        selenium.open("/fleet/view/dashboard/");
        verifyFalse(selenium.getLocation().contains("/fleet/view/dashboard/"));
        verifyTrue(selenium.getLocation().contains("/fleet/view/login/"));
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