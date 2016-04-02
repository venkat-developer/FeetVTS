package com.i10n.fleet.web.servlet.view.freemarker;

import java.io.IOException;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

/**
 * Custom {@link FreeMarkerViewResolver} for fleetcheck customizations
 * 
 * @author sabarish
 * 
 */
public class FleetFreeMarkerViewResolver extends FreeMarkerViewResolver {

    private static final Logger LOG = Logger.getLogger(FleetFreeMarkerViewResolver.class);

    /**
     * Overrides the handling of chaining ViewResolvers in
     * {@link FreeMarkerViewResolver} to check if the resource exists as Spring
     * does not handle this chaining well
     */
    @Override
    protected boolean canHandle(String viewName, Locale local) {
        FreeMarkerConfigurer configurer = (FreeMarkerConfigurer) getApplicationContext()
                .getBean("freemarkerConfig");
        boolean result = false;
        try {
            result = (null != configurer.getConfiguration().getTemplateLoader()
                    .findTemplateSource(getPrefix() + viewName + getSuffix()));
        }
        catch (IOException e) {
            LOG.error("Caught IOException while loading " + viewName, e);
        }
        return result;
    }

}
