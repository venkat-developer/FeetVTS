package com.i10n.fleet.web.interceptors;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.i10n.fleet.test.AbstractFleetWebSpringTests;
import com.i10n.fleet.web.controllers.LoginController;

public class TestLoginInterceptorTest extends AbstractFleetWebSpringTests {

    /**
     * Tests {@link LoginInterceptor} for escaping interception.
     */
    @Test
    public void testEscapeInterception() {
        TestLoginInterceptor interceptor = (TestLoginInterceptor) getApplicationContext()
                .getBean("TestLoginInterceptor");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/fleet/form/login/");
        try {
            assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(),
                    new LoginController()));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("Caught Exception while intercepting :- " + e.getMessage());
        }
    }

    @Test
    public void testGetUserAttributes() {
        /* Atleast one user should be there */
        TestLoginInterceptor interceptor = (TestLoginInterceptor) getApplicationContext()
                .getBean("TestLoginInterceptor");
        assertNotNull(interceptor.getUserAttributes());
    }

}
