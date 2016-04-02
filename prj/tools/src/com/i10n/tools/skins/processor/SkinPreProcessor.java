package com.i10n.tools.skins.processor;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

/**
 * Skin Pre Processor, preprocesses for scripts/css/ftl dependency with the base
 * skins. and builds a xml that can be processed further by
 * {@link SkinProcessor}
 * 
 * @author N.Balaji
 * 
 */
public class SkinPreProcessor extends AbstractSkinProcessor {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(SkinPreProcessor.class);

    public SkinPreProcessor() {
    }

    public Document process(Document input) {
        Document result = transformDocument(input, "buildskin.xslt");
        result = transformDocument(result, "buildskinoptimizer.xslt");
        return result;
    }

}
