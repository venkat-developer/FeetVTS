package com.i10n.fleet.web.handler;

import java.io.File;
import java.util.Locale;

import javax.servlet.ServletContext;

import org.junit.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import com.i10n.fleet.test.AbstractFleetWebSpringTests;
import com.i10n.fleet.web.servlet.view.freemarker.FleetFreeMarkerViewResolver;

/**
 * Test Cases for testing {@link FreeMarkerViewResolver}
 * 
 * @author sabarish
 * 
 */
public class FreeMarkerViewResolverTest extends AbstractFleetWebSpringTests {
    private ServletContext m_servletContext = null;

    private static final String TEST_FILE = "/freemarkerview/input.ftm";

    private static final String[] CONFIG_FILES = new String[] {
            "classpath:/WEB-INF/applicationContext.xml",
            "classpath:/freemarkerview/test-fleetcheck-servlet.xml" };

    /**
     * Tests if {@link FreeMarkerViewResolver} handles if template exists
     */
    @Test
    public void testViewResolver() {
        FleetFreeMarkerViewResolver viewResolver = (FleetFreeMarkerViewResolver) getApplicationContext()
                .getBean("viewMacroResolver");
        viewResolver.setPrefix("/freemarkerview");
        try {
            assertNotNull(viewResolver.resolveViewName("/input", Locale.ENGLISH));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("Caught Exception while resolving view name" + e.getMessage());
        }
    }

    /**
     * Tests if {@link FreeMarkerViewResolver} escapes handling if template does
     * not exist
     */
    @Test
    public void testEscapingViewResolver() {
        FleetFreeMarkerViewResolver viewResolver = (FleetFreeMarkerViewResolver) getApplicationContext()
                .getBean("viewDataResolver");
        viewResolver.setPrefix("/freemarkerview");
        try {
            assertNull(viewResolver.resolveViewName("/input", Locale.ENGLISH));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("Caught Exception while resolving view name" + e.getMessage());
        }
    }

    /**
     * Tests if {@link FreeMarkerViewResolver} handles if viewName is already
     * set
     */
    @Test
    public void testAlreadySetViewName() {
        FleetFreeMarkerViewResolver viewResolver = (FleetFreeMarkerViewResolver) getApplicationContext()
                .getBean("viewMacroResolver");
        viewResolver.setPrefix("/freemarkerview");
        String[] viewNames = { "/input" };
        viewResolver.setViewNames(viewNames);
        try {
            assertNotNull(viewResolver.resolveViewName("/input", Locale.ENGLISH));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("Caught Exception while resolving view name" + e.getMessage());
        }
    }

    @Override
    protected ServletContext getServletContext() {
        if (null == m_servletContext) {
            String path = "";
            File inputFile = new File(getClass().getResource(TEST_FILE).getFile());
            if (inputFile.exists()) {
                path = "file:" + inputFile.getParentFile().getParent();
            }
            m_servletContext = new MockServletContext(path);
        }
        return m_servletContext;
    }

    @Override
    protected String[] getConfigLocations() {
        return CONFIG_FILES;
    }

}
