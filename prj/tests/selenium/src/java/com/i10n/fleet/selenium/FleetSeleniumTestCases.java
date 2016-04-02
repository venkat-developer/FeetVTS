package com.i10n.fleet.selenium;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.After;
import org.junit.Before;

import com.i10n.fleet.selenium.tests.views.AdminDashboardViewSeleniumTest;
import com.i10n.fleet.selenium.tests.views.ControlpanelViewSeleniumTest;
import com.i10n.fleet.selenium.tests.views.DashboardViewSeleniumTest;
import com.i10n.fleet.selenium.tests.views.LiveTrackViewSeleniumTest;
import com.i10n.fleet.selenium.tests.views.LoginViewSeleniumTest;
import com.i10n.fleet.selenium.tests.views.ReportsViewSeleniumTest;
import com.i10n.fleet.selenium.tests.widgets.ActivityReportTest;
import com.i10n.fleet.selenium.tests.widgets.DriverReportSeleniumTest;
import com.i10n.fleet.selenium.tests.widgets.GraphReportSeleniumTest;
import com.i10n.fleet.selenium.tests.widgets.HeaderSeleniumTest;
import com.i10n.fleet.selenium.tests.widgets.IdlePointsReportSeleniumTest;
import com.i10n.fleet.selenium.tests.widgets.LoginFormSeleniumTest;
import com.i10n.fleet.selenium.tests.widgets.QuickLinksSeleniumTest;
import com.i10n.fleet.selenium.tests.widgets.TripSettingsSeleniumTest;
import com.i10n.fleet.test.util.FleetJettyServer;
import com.i10n.fleet.test.util.PropertyLoader;

/**
 * Test Suite to run All Selenium Test Cases All the Selenium tests added should
 * also be registered here
 * 
 * @author sabarish
 * 
 */
public class FleetSeleniumTestCases extends TestSuite {

    private static final String START_JETTY = "jetty.start";

    /**
     * A One Time Setup that starts the {@link FleetJettyServer}
     * 
     * @throws Exception
     */
    public static void oneTimeSetup() throws Exception {
        PropertyLoader.load();
        if ("true".equals(PropertyLoader.getProperty(START_JETTY))) {
            FleetJettyServer.getInstance().start();
        }
    }

/**
     * Returns the suite of TestCases to be run by JUnit
     * and the tests are setup in such a way that {@link FleetSeleniumTestCases#oneTimeSetup()} and
     * {@link FleetSeleniumTestCases#oneTimeTeardown() are run only once for this suite
     * @return
     */
    public static Test suite() {
        FleetSeleniumTestCases suite = new FleetSeleniumTestCases();
        /*
         * Test for the views
         */
        suite.addTest(LoginViewSeleniumTest.class);
        suite.addTest(DashboardViewSeleniumTest.class);
        suite.addTest(LiveTrackViewSeleniumTest.class);
        suite.addTest(ReportsViewSeleniumTest.class);
        suite.addTest(ControlpanelViewSeleniumTest.class);
        suite.addTest(AdminDashboardViewSeleniumTest.class);
        /*
         * Test for some important widgets
         */
        suite.addTest(HeaderSeleniumTest.class);
        suite.addTest(LoginFormSeleniumTest.class);
        suite.addTest(GraphReportSeleniumTest.class);
        suite.addTest(QuickLinksSeleniumTest.class);
        suite.addTest(DriverReportSeleniumTest.class);
        suite.addTest(TripSettingsSeleniumTest.class);
        suite.addTest(IdlePointsReportSeleniumTest.class);
        suite.addTest(ActivityReportTest.class);

        TestSetup setup = new TestSetup(suite) {
            @Before
            @Override
            protected void setUp() throws Exception {
                oneTimeSetup();
            }

            @After
            @Override
            protected void tearDown() throws Exception {
                oneTimeTeardown();
            }
        };
        return setup;
    }

    private void addTest(Class<? extends Test> test) {
        addTest(new TestSuite(test));
    }

    /**
     * A One Time TearDown function that stops the {@link FleetJettyServer}
     * 
     * @throws Exception
     */
    public static void oneTimeTeardown() throws Exception {
        if ("true".equals(PropertyLoader.getProperty(START_JETTY))) {
            FleetJettyServer.getInstance().stop();
        }
    }
}
