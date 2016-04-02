package com.i10n.fleet.web.context;

import java.io.IOException;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * Custom {@link XmlWebApplicationContext} that uses
 * {@link FleetXmlBeanDefinitionReader} as a custom bean definition reader.
 * 
 * @author sabarish
 * 
 */
public class FleetXmlWebApplicationContext extends XmlWebApplicationContext implements
        ConfigurableWebApplicationContext {

    /**
     * Overrides {@link XmlWebApplicationContext#loadBeanDefinitions} so that
     * Fleet's custom BeanDefinitionreader {@link FleetXmlBeanDefinitionReader}
     * can be used instead of the default one.
     */
    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory)
            throws IOException {
        FleetXmlBeanDefinitionReader beanDefinitionReader = new FleetXmlBeanDefinitionReader(
                beanFactory);
        // Configure the bean definition reader with this context's
        // resource loading environment.
        beanDefinitionReader.setResourceLoader(this);
        beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));

        // Allow a subclass to provide custom initialisation of the reader,
        // then proceed with actually loading the bean definitions.
        initBeanDefinitionReader(beanDefinitionReader);
        loadBeanDefinitions(beanDefinitionReader);

    }
}
