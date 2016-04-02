package com.i10n.fleet.jsunit;

import net.jsunit.StandaloneTest;

import com.i10n.fleet.test.util.FleetJettyServer;
import com.i10n.fleet.test.util.PropertyLoader;

/**
 * Fleet Customized JSUNIT {@link StandaloneTest} for running the Jetty Server
 * on test deployment before running tests.
 * 
 * @author sabarish
 * 
 */
public class FleetJSUnitStandAloneTest extends StandaloneTest {

    private static final String START_JETTY = "jetty.start";

    /**
     * See {@link StandaloneTest}
     * 
     * @param name
     */
    public FleetJSUnitStandAloneTest(String name) {
        super(name);
    }

    /**
     * Starts the {@link FleetJettyServer}
     * 
     * @see {@link StandaloneTest#setUp()}
     */
    @Override
    public void setUp() throws Exception {
        PropertyLoader.load();
        if ("true".equals(PropertyLoader.getProperty(START_JETTY))) {
            FleetJettyServer.getInstance().start();
        }
        super.setUp();
    }

    /**
     * Stops the {@link FleetJettyServer}
     * 
     * @see {@link StandaloneTest#tearDown()}
     */
    @Override
    public void tearDown() throws Exception {
        if ("true".equals(PropertyLoader.getProperty(START_JETTY))) {
            FleetJettyServer.getInstance().stop();
        }
        super.tearDown();
    }
}
