package com.i10n.fleet.test;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * Abstract Class for Fleetcheck Customizations in
 * {@link AbstractDependencyInjectionSpringContextTests}
 * 
 * @author sabarish
 * 
 */
public abstract class AbstractFleetInjectionSpringTests extends
        AbstractDependencyInjectionSpringContextTests {
    /**
     * These files will require change when extra database operations will be
     * added.This will then use mock datasource instead of actual datasource
     */
    private static final String[] configFiles = new String[] {
            "classpath:/WEB-INF/applicationContext.xml",
            "classpath:/WEB-INF/fleetcheck-servlet.xml" };

    @Override
    protected String[] getConfigLocations() {
        return configFiles;
    }
}
