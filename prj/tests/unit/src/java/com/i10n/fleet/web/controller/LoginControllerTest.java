package com.i10n.fleet.web.controller;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import com.i10n.fleet.test.AbstractFleetWebSpringTests;
import com.i10n.fleet.util.Constants;
import com.i10n.fleet.web.controllers.LoginController;

/**
 * Tests {@link LoginController} for handling logins and logouts
 * 
 * @author sabarish
 * 
 */
public class LoginControllerTest extends AbstractFleetWebSpringTests {

    /**
     * Tests {@link LoginController} for login requests;
     */
    @Test
    public void testLogin() {
        LoginController controller = (LoginController) getApplicationContext().getBean(
                "loginFormController");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/fleet/form/login/");
        request.addParameter("user", "admin");
        request.addParameter("password", "testadmin");
        request.setMethod("POST");
        try {
            ModelAndView mview = controller.handleRequestInternal(request,
                    new MockHttpServletResponse());
            RedirectView view = (RedirectView) mview.getView();
            assertTrue(view.getUrl().equals("/fleet/view/dashboard/"));
            assertEquals(WebUtils.getSessionAttribute(request,
                    Constants.SESSION.ATTR_USER), "admin");
            assertEquals(WebUtils.getSessionAttribute(request,
                    Constants.SESSION.ATTR_GROUP), "admin");
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("Caught Exception while handling login request");
        }
    }

    /**
     * Tests {@link LoginController} for admin login request
     */
    @Test
    public void testAdminLogin() {
        LoginController controller = (LoginController) getApplicationContext().getBean(
                "loginFormController");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/fleet/form/login/");
        request.addParameter("user", "testuser");
        request.addParameter("password", "testuser");
        request.setMethod("POST");
        try {
            ModelAndView mview = controller.handleRequestInternal(request,
                    new MockHttpServletResponse());
            RedirectView view = (RedirectView) mview.getView();
            assertTrue(view.getUrl().equals("/fleet/view/dashboard/"));
            assertEquals(WebUtils.getSessionAttribute(request,
                    Constants.SESSION.ATTR_USER), "testuser");
            assertEquals(WebUtils.getSessionAttribute(request,
                    Constants.SESSION.ATTR_GROUP), "default");
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("Caught Exception while handling login request");
        }
    }

    /**
     * Tests {@link LoginController} for logout requests;
     */
    @Test
    public void testLogout() {
        testLogin();
        LoginController controller = (LoginController) getApplicationContext().getBean(
                "loginFormController");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/fleet/form/login/");
        request.addParameter("logout", "true");

        try {
            ModelAndView mview = controller.handleRequestInternal(request,
                    new MockHttpServletResponse());
            RedirectView view = (RedirectView) mview.getView();
            assertTrue(view.getUrl().equals("/fleet/view/login/"));
            assertNull(WebUtils.getSessionAttribute(request, "currentuser"));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("Caught Exception while handling login request");
        }
    }
}
