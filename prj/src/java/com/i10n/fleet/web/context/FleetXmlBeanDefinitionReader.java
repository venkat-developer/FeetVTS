package com.i10n.fleet.web.context;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;

import com.i10n.fleet.util.EnvironmentInfo;

/**
 * Custom {@link XmlBeanDefinitionReader} which will environmentalize the
 * context xml stream and pass the control to {@link XmlBeanDefinitionReader}
 * 
 * @author sabarish
 * 
 */
public class FleetXmlBeanDefinitionReader extends XmlBeanDefinitionReader {

    public FleetXmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    /**
     * Overrides
     * {@link XmlBeanDefinitionReader#loadBeanDefinitions(EncodedResource)} so
     * that bean definitions can be environmentalized
     */
    @Override
    public int loadBeanDefinitions(EncodedResource encodedResource)
            throws BeanDefinitionStoreException {
        EncodedResource envResource = new EncodedResource(new FleetCascadedResource(
                encodedResource.getResource()));
        return super.loadBeanDefinitions(envResource);
    }

    /**
     * Class to represent a {@link Resource} that will environmentalize the
     * stream the resource represents.This resource is required to be closed and
     * should generate a new stream whenever {@link #getInputStream()} is called
     * Hence the resource that this will represent should also have the same
     * properties.
     * 
     * @author sabarish
     * 
     */
    private static class FleetCascadedResource extends AbstractResource {

        private Resource m_innerResource;

        /**
         * Constructor.Inner Resource which this will represent should be
         * passed.
         * 
         * @param resource
         */
        public FleetCascadedResource(Resource resource) {
            m_innerResource = resource;
        }

        /**
         * See {@link Resource#getDescription()}
         */
        @Override
        public String getDescription() {
            return m_innerResource.getDescription();
        }

        /**
         * Returns the stream which is environmentalized
         * 
         * @see Resource#getInputStream()
         */
        @Override
        public InputStream getInputStream() throws IOException {
            return EnvironmentInfo.getEnvironmentalizedStream(m_innerResource
                    .getInputStream());
        }

    }
}
