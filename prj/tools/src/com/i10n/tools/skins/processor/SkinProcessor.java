package com.i10n.tools.skins.processor;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.i10n.tools.skins.SkinDependencyManager;
import com.i10n.tools.skins.config.SkinProperties;
import com.i10n.tools.skins.exception.SkinParseException;
import com.i10n.tools.utils.XPathUtils;

/**
 * Class for Generating a Dependency resolved,blotted and Optimized Skin XML
 * File
 * 
 * @author N.Balaji
 */

public class SkinProcessor extends AbstractSkinProcessor {

    /**
     * Method for adding the context path to look for files
     * 
     */

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(SkinProcessor.class);
    private boolean m_widgetDependencyCheckEnabled = true;
    private SkinDependencyManager m_dependencyManager = null;

    public SkinProcessor(SkinDependencyManager dependecyManager) {
        m_dependencyManager = dependecyManager;
        m_widgetDependencyCheckEnabled = Boolean.parseBoolean(SkinProperties
                .getProperty(SkinProperties.CYCLIC_CHK_ENABLED_KEY));
    }

    public Document process(Document input) throws SkinParseException {
        Document result = transform(input);
        return result;
    }

    private Document transform(Document input) throws SkinParseException {
        /* Check for widget cyclic dependency */
        if (m_widgetDependencyCheckEnabled) {
            if (m_dependencyManager.checkWidgetDependency(input)) {
                throw new SkinParseException("A Widget Dependency Cycle found in Skin : "
                        + XPathUtils.getAttribute(XPathUtils.getNode(input, "/skin"),
                                "name"));
            }
        }
        /* Safe to continue as there are no cyclic dependencies */
        Document output = transformDocument(input, "transform.xslt");
        output = transformDocument(output, "optimize.xslt");
        return output;
    }
}
