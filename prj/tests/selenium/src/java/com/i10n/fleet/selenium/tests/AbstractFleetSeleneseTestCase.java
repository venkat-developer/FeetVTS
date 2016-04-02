package com.i10n.fleet.selenium.tests;

import org.junit.Test;

import com.i10n.fleet.test.util.PropertyLoader;
import com.thoughtworks.selenium.SeleneseTestCase;

/**
 * Performs the basic setup, login, logout and widgetLoad test
 * 
 * @author sabarish,irk,aravind
 */
public abstract class AbstractFleetSeleneseTestCase extends SeleneseTestCase {

    private static final String PORT_PROP = "selenium.test.host.port";
    private static final String HOST_PROP = "selenium.test.host";
    private static final String TEST_USER = "testuser";
    private static final String TEST_PASSWORD = "testpassword";
    private static final String ADMIN_USER = "admin";
    private static final String LOGIN_URL = "/fleet/view/login/";
    private static final String LOGOUT_URL = "/fleet/form/login/?logout=true";

    /**
     * Sets up the selenium test case jetty deployed fleet application
     * 
     * @see AbstractFleetSeleneseTestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        setUp("http://" + PropertyLoader.getProperty(HOST_PROP) + ":"
                + PropertyLoader.getProperty(PORT_PROP), "*firefox");
    }

    public void setUp(String viewURL) throws Exception {
        setUp("http://" + PropertyLoader.getProperty(HOST_PROP) + ":"
                + PropertyLoader.getProperty(PORT_PROP), "*firefox");
        doLogin(viewURL);
    }

    public void setUp(String viewURL, boolean isAdmin) throws Exception {
        setUp("http://" + PropertyLoader.getProperty(HOST_PROP) + ":"
                + PropertyLoader.getProperty(PORT_PROP), "*firefox");
        if (isAdmin) {
            doAdminLogin(viewURL);
        }
        else {
            doLogin(viewURL);
        }
    }

    public void setUpWithTimeOut(String viewURL, String timeout) throws Exception {
        setUp("http://" + PropertyLoader.getProperty(HOST_PROP) + ":"
                + PropertyLoader.getProperty(PORT_PROP), "*firefox");
        selenium.setTimeout(timeout);
        doLogin(viewURL);
    }

    /**
     * Performs login and checks if the required view is loaded
     */
    protected void doLogin(String viewURL) {
        selenium.open(LOGIN_URL);
        selenium.type("//*[@id='login.user']", TEST_USER);
        selenium.type("//*[@id='login.password']", TEST_PASSWORD);
        selenium.click("//div[@id='buttons']/div/*/div/ul/li[2]");
        selenium.waitForPageToLoad("15000");
        selenium.open(viewURL);
    }

    /**
     * Performs login as admin and checks if the required view is loaded
     */
    protected void doAdminLogin(String viewURL) {
        selenium.open(LOGIN_URL);
        selenium.type("//*[@id='login.user']", ADMIN_USER);
        selenium.type("//*[@id='login.password']", TEST_PASSWORD);
        selenium.click("//div[@id='buttons']/div/*/div/ul/li[2]");
        selenium.waitForPageToLoad("15000");
        selenium.open(viewURL);
    }

    /**
     * Performs Logout operation and checks for logout post conditions
     */
    protected void doLogout() {

        selenium.open(LOGOUT_URL);
        assertEquals(selenium.getLocation(), "http://"
                + PropertyLoader.getProperty(HOST_PROP) + ":"
                + PropertyLoader.getProperty(PORT_PROP) + LOGIN_URL);
    }

    public void checkHeaderLinks() {
        verifyTrue(selenium
                .isElementPresent("//div[@id='header']//div[@class='header-links']/div[1]/a"));
        verifyTrue(selenium.getText(
                "//div[@id='header']//div[@class='header-links']/div[1]/a").contains(
                TEST_USER));
        verifyTrue(selenium
                .isElementPresent("//div[@id='header']//div[@class='header-links']/div[2]/a"));
        verifyEquals(
                selenium
                        .getAttribute("//div[@id='header']//div[@class='header-links']/div[2]/a@href"),
                LOGOUT_URL);
        verifyTrue(selenium
                .isElementPresent("//div[@id='header']//div[@class='header-links']/div[3]/a"));
    }

    /**
     * Checks if a widget is loaded when the widget's div id is sent as
     * parameter
     */
    public void checkWidgetMarkupLoad(String widgetId, String keyWords) {
        checkWidgetMarkupLoad(widgetId);
        verifyTrue(selenium.getText(widgetId).contains(keyWords));
    }

    public void checkWidgetMarkupLoad(String widgetId) {
        verifyTrue(selenium.isElementPresent(widgetId));
        verifyTrue(!selenium.getText(widgetId).isEmpty());
    }

    /**
     * Checks for exception in page
     */
    @Test
    public void testForExceptionInPage() {
        String htmlSource = selenium.getHtmlSource();
        verifyFalse(htmlSource.contains("Java backtrace for programmers:"));
    }

}