package com.i10n.fleet.selenium.tests.views;

import org.junit.After;
import org.junit.Test;

import com.i10n.fleet.selenium.tests.AbstractFleetSeleneseTestCase;

/**
 * Performs the selenium tests for the admin dashboard view
 * 
 * @author aravind
 * 
 */
public class AdminDashboardViewSeleniumTest extends AbstractFleetSeleneseTestCase {
    private static final String VIEW_URL = "/fleet/view/dashboard/";
    private static final String USERPANEL_URL = "/fleet/view/controlpanel/?subpage=user";
    private static final String ADDUSER_URL = "/fleet/view/controlpanel/?subpage=user&action=add";
    private static final String EDITUSER_URL = "/fleet/view/controlpanel/?subpage=user&action=edit";
    private static final String DELETEUSER_URL = "/fleet/view/controlpanel/?subpage=user&action=delete";

    public void setUp() throws Exception {
        super.setUp(VIEW_URL, true);
    }

    /**
     * Test view markupLoad and behaviour
     */
    @Test
    public void testView() throws Exception {
        verifyTrue(selenium.isElementPresent("header"));
        checkWidgetMarkupLoad("userinfo", "USER INFORMATION");
    }

    /**
     * Tests for the UserInfo widget
     */
    @Test
    public void testUserInfoWidget() {
        assertEquals(
                "ADD USER",
                selenium
                        .getText("//div[@id='userinfo']/div/div[1]/div/div/div/a/div/div/div/div/div"));
        assertEquals("USER ID", selenium
                .getText("//div[@id='userinfo']/div/table/thead/tr/th[1]"));
        assertEquals("STATUS", selenium
                .getText("//div[@id='userinfo']/div/table/thead/tr/th[2]"));
        assertEquals("VEHICLE ALLOTTED", selenium
                .getText("//div[@id='userinfo']/div/table/thead/tr/th[3]"));
        assertEquals("LAST LOGIN", selenium
                .getText("//div[@id='userinfo']/div/table/thead/tr/th[4]"));
        assertTrue(selenium
                .isElementPresent("//div[@id='userinfo']/div/table/tbody/tr[1]"));
        assertEquals(ADDUSER_URL, selenium
                .getAttribute("//div[@id='userinfo']/div/table/tbody/tr[1]/td[5]/a@href"));
        assertEquals(EDITUSER_URL, selenium
                .getAttribute("//div[@id='userinfo']/div/table/tbody/tr[1]/td[6]/a@href"));
        assertEquals(DELETEUSER_URL, selenium
                .getAttribute("//div[@id='userinfo']/div/table/tbody/tr[1]/td[7]/a@href"));
        assertEquals(USERPANEL_URL, selenium
                .getAttribute("//div[@id='userinfo']/div/div[1]/div/div/div/a@href"));
        assertTrue(selenium.isElementPresent("//div[@id='userinfo']/div/div[2]"));

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
