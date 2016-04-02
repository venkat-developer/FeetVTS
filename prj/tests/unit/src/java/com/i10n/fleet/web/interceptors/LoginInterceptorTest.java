package com.i10n.fleet.web.interceptors;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import com.i10n.fleet.test.AbstractFleetWebSpringTests;
import com.i10n.fleet.web.controllers.LoginController;

/**
 * Tests {@link LoginInterceptor}
 * 
 * @author sabarish
 * 
 */
public class LoginInterceptorTest extends AbstractFleetWebSpringTests {

    /**
     * Tests {@link LoginInterceptor} for escaping interception.
     */
    @Test
    public void testEscapeInterception() {
        LoginInterceptor interceptor = (LoginInterceptor) getApplicationContext()
                .getBean("defaultLoginInterceptor");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/fleet/form/login/");
        request.addParameter("user", "testuser");
        request.addParameter("password", "testuser");
        try {
            assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(),
                    new LoginController()));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("Caught Exception while intercepting :- " + e.getMessage());
        }

    }

    /**
     * Tests {@link LoginInterceptor} for interception to redirect to login page
     */
    @Test
    public void testLoggedOutInterception() {
        LoginInterceptor interceptor = (LoginInterceptor) getApplicationContext()
                .getBean("defaultLoginInterceptor");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/fleet/view/dashboard/");
        boolean caughtViewException = false;
        try {
            interceptor.preHandle(request, new MockHttpServletResponse(),
                    new LoginController());
        }
        catch (ModelAndViewDefiningException ex) {
            caughtViewException = true;
            ModelAndView mview = ex.getModelAndView();
            View view = mview.getView();
            assertEquals(((RedirectView) view).getUrl(), interceptor.getFailureView());
        }
        catch (Exception e) {
            fail("Caught Exception while intercepting :- " + e.getMessage());
        }
        assertTrue("ModelAndViewDefiningException not caught", caughtViewException);
    }

    /**
     * Tests {@link LoginInterceptor} for interception to redirect to login page
     */
    @Test
    public void testLoggedInInterception() {
        LoginInterceptor interceptor = (LoginInterceptor) getApplicationContext()
                .getBean("defaultLoginInterceptor");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/fleet/view/dashboard/");
        WebUtils.setSessionAttribute(request, "currentuser", "testuser");
        try {
            assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(),
                    new LoginController()));
        }
        catch (Exception e) {
            fail("Caught Exception while intercepting :- " + e.getMessage());
        }
    }

    /**
     * Tests {@link LoginInterceptor} for interception in allow all mode
     */
    @Test
    public void testAllowModeInterception() {
        LoginInterceptor interceptor = (LoginInterceptor) getApplicationContext()
                .getBean("allowAllLoginInterceptor");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/fleet/view/dashboard/");
        try {
            assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(),
                    new LoginController()));
        }
        catch (Exception e) {
            fail("Caught Exception while intercepting :- " + e.getMessage());
        }
    }
}
