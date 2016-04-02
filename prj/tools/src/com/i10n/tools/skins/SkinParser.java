package com.i10n.tools.skins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.i10n.tools.skins.beans.Skin;
import com.i10n.tools.skins.config.SkinProperties;
import com.i10n.tools.skins.exception.SkinParseException;
import com.i10n.tools.skins.filter.ISkinFilter;
import com.i10n.tools.skins.filter.impl.SkinCompressor;
import com.i10n.tools.skins.filter.impl.SkinMerger;
import com.i10n.tools.skins.processor.ISkinProcessor;
import com.i10n.tools.skins.processor.SkinPostProcessor;
import com.i10n.tools.skins.processor.SkinPreProcessor;
import com.i10n.tools.skins.processor.SkinProcessor;
import com.i10n.tools.skins.processor.utils.InheritanceHandler;
import com.i10n.tools.skins.publish.SkinPublisher;
import com.i10n.tools.utils.XMLUtils;

/**
 * A Utility Tool to parse the skin xmls's and resolve the dependencies, and
 * populate with inherent scripts,stylesheets. <br/>
 * TODO : A Possible Candidate for refactoring.The XSLT Transformation coupled
 * with parsing logic for filtering should be reviewed again
 * 
 * @author sabarish
 * 
 */
public class SkinParser {

    private SkinDependencyManager m_dependencyManager;
    private SkinHierarchyBuilder m_skinHierarchyBuilder;
    private ISkinProcessor m_preProcessor;
    private ISkinProcessor m_skinProcessor;
    private ISkinProcessor m_postProcessor;
    private SkinPublisher m_skinPublisher;
    private List<ISkinFilter> m_filters = new ArrayList<ISkinFilter>();

    private static final Logger LOG = Logger.getLogger(SkinParser.class);

    public SkinParser() {
        SkinProperties.load();
        initialize();
        try {
            m_skinHierarchyBuilder.buildHierarchy();
        }
        catch (SkinParseException e) {
            LOG.error("Caught SkinParseException while building hieararchy", e);
        }
    }

    private void initialize() {
        m_dependencyManager = new SkinDependencyManager();
        m_skinHierarchyBuilder = new SkinHierarchyBuilder(m_dependencyManager);
        m_preProcessor = new SkinPreProcessor();
        m_skinProcessor = new SkinProcessor(m_dependencyManager);
        m_postProcessor = new SkinPostProcessor();
        m_skinPublisher = new SkinPublisher();
        m_filters.add(new SkinMerger());
        m_filters.add(new SkinCompressor());
    }

    /**
     * Starts the parsing/building of skins
     * 
     * @throws SkinParseException
     */
    public void start() throws SkinParseException {
        /* Instantiating necessary classes */
        List<Skin> skins = m_skinHierarchyBuilder.getSkins();
        Map<Skin, Document> m_generatedDocs = new HashMap<Skin, Document>();
        InheritanceHandler m_inheritanceHandler = new InheritanceHandler();
        for (Skin skin : skins) {
            /* Implementing deep level inheritance */
            Skin mergedSkin = new Skin("temp");
            mergedSkin.setDocument(skin.getDocument());
            Document xml = null;
            for (Skin parent : m_skinHierarchyBuilder.getHierarchy(skin)) {
                xml = m_preProcessor.process(mergeSkins(mergedSkin, parent));
                mergedSkin.setDocument(xml);
            }
            /* Handle Removals */
            m_inheritanceHandler.implementRemovals(xml, skin.getName());
            /* Transform */
            xml = m_skinProcessor.process(xml);
            /* Wrapping up */
            ((SkinPostProcessor) m_postProcessor)
                    .setDependencyList(getHierarchichalSkinNames(skin));
            xml = m_postProcessor.process(xml);
            m_generatedDocs.put(skin, xml);
        }
        /* Publishing all the skins */
        for (Skin skin : m_generatedDocs.keySet()) {
            skin.setDocument(m_generatedDocs.get(skin));
            for (ISkinFilter filter : m_filters) {
                skin = filter.filter(skin);
            }
            m_skinPublisher.publish(skin);
        }
    }

    /**
     * Returns the Names of hierarchial skins
     */
    private List<String> getHierarchichalSkinNames(Skin skin) {
        List<Skin> skinList = m_skinHierarchyBuilder.getHierarchy(skin);
        List<String> nameList = new ArrayList<String>();
        for (Skin curSkin : skinList) {
            nameList.add(curSkin.getName());
        }
        return nameList;
    }

    /**
     * Merges the parasite skin with the host skin
     * 
     * @param host
     *            The skin into which the parasite skin is merged
     * @param parasite
     * @return
     */
    private Document mergeSkins(Skin host, Skin parasite) {
        /* Merging the XML doc */
        StringBuilder builder = new StringBuilder();
        /* Appending the root element */
        builder.append("<?xml version=\"1.0\"?>\n<skinconfig>");
        /* Appending parasite skin to the host skin */
        String xml = skinToString(host);
        builder.append(xml);
        xml = skinToString(parasite);
        builder.append(xml);
        builder.append("</skinconfig>");
        return XMLUtils.stringToDocument(builder.toString());
    }

    /**
     * Converts the a skin's document to a string Removes some elements that
     * match a predefined set of patterns
     * 
     * @param target
     *            The skin whose document is to be converted to a string
     * @return
     */
    private String skinToString(Skin target) {
        String result = XMLUtils.documentToString(target.getDocument());
        Pattern pattern = Pattern.compile("\\Q<?xml\\E([^\\?]*)\\Q?>\\E");
        Matcher matcher = pattern.matcher(result);
        if (matcher.find()) {
            result = matcher.replaceAll("");
        }
        return result;
    }

    /**
     * Main Function that executes the parer
     * 
     * @param args
     * @throws SkinParseException
     */
    public static void main(String args[]) throws SkinParseException {
        SkinParser parser = new SkinParser();
        parser.start();
    }
}
