package com.i10n.tools.skins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.i10n.tools.skins.beans.Skin;
import com.i10n.tools.skins.exception.SkinParseException;
import com.i10n.tools.utils.XPathUtils;

/**
 * Manages dependency between skins, detects cycles in skin before adding
 * dependency between skins. and also helps in sorting the skins based on
 * dependency, which is useful to find the order of parsing skins.
 * 
 * @author sabarish
 * 
 */
public class SkinDependencyManager {

    private Map<String, Skin> m_skins = null;

    /**
     * starts to find dependency among skins
     * 
     * @param skins
     * @return
     * @throws SkinParseException
     */
    public Map<String, Skin> manageSkinDependency(Map<String, Skin> skins)
            throws SkinParseException {
        m_skins = skins;
        List<Skin> skinList = new ArrayList<Skin>(skins.values());
        populateDependency();
        Collections.sort(skinList, new SkinDependencyComparator());
        Map<String, Skin> result = new LinkedHashMap<String, Skin>();
        for (Skin skin : skinList) {
            result.put(skin.getName(), skin);
        }
        return result;
    }

    /**
     * Detects cycle in skins and appoints appropriate baseSkins based on the
     * dependency given in the skin xml document
     * 
     * @throws SkinParseException
     */
    private void populateDependency() throws SkinParseException {
        for (Skin skin : m_skins.values()) {
            Document doc = skin.getDocument();
            String depends = XPathUtils.getAttribute(XPathUtils.getNode(doc, "/skin"),
                    "depends");
            if (null != depends && !depends.isEmpty() && m_skins.containsKey(depends)) {
                Skin baseSkin = m_skins.get(depends);
                if (!checkForCycle(baseSkin, skin)) {
                    skin.setBaseSkin(baseSkin);
                }
                else {
                    throw new SkinParseException("Found a cycle in skin base : "
                            + baseSkin.getName() + " ; dependent : " + skin.getName());
                }
            }
        }
    }

    /**
     * Checks for dependency if baseSkin is added to dependent returns true if
     * cycle will be occurring if dependency is added else will return false
     * 
     * @param baseSkin
     * @param dependent
     * @return
     */
    private static boolean checkForCycle(Skin baseSkin, Skin dependent) {
        boolean result = false;
        Skin skin = baseSkin;
        while (null != skin) {
            if (skin.equals(dependent)) {
                result = true;
                break;
            }
            skin = skin.getBaseSkin();
        }
        return result;
    }

    /**
     * Checks for cycles in the widget references between any of the nodes
     * 
     * @param xml
     * @return
     */
    public boolean checkWidgetDependency(Document xml) throws SkinParseException {
        boolean cyclic = false;

        /* Checking for cycles in widgetrefs */
        List<Node> listOfWidgets = XPathUtils.getNodes(xml.getDocumentElement(),
                "//widgetconfig/widget");
        CyclicDependencyChecker dependencyChecker = new CyclicDependencyChecker();
        cyclic = dependencyChecker.checkForCycle(listOfWidgets, "./widgetrefs/widgetref");
        return cyclic;
    }

    /**
     * A local comparator for sorting based on the dependency
     * 
     * @author sabarish
     * 
     */
    private static class SkinDependencyComparator implements Comparator<Skin> {

        public int compare(Skin obj1, Skin obj2) {
            int result = 0;
            boolean isAncestor = checkForCycle(obj1, obj2);
            if (isAncestor) {
                result = 1;
            }
            else {
                isAncestor = checkForCycle(obj2, obj1);
                if (isAncestor) {
                    result = -1;
                }
            }
            return result;
        }

    }
}
