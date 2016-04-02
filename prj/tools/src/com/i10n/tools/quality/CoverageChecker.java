package com.i10n.tools.quality;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.i10n.tools.exception.FleetToolsException;
import com.i10n.tools.utils.XPathUtils;

public class CoverageChecker extends AbstractQualityChecker {

    /**
     * See {@link AbstractQualityChecker#parseXML(Document)}
     */
    protected Map<String, Integer> parseXML(Document xmlDoc) {
        String coverageXpath = "/report/data/all/coverage";
        List<Node> coverageNodes = XPathUtils.getNodes(xmlDoc, coverageXpath);
        Map<String, Integer> coverages = new HashMap<String, Integer>();
        for (Node coverageNode : coverageNodes) {
            String coverageType = XPathUtils.getAttribute(coverageNode, "type");
            coverageType = coverageType.substring(0, coverageType.indexOf(","));
            String coverageValue = XPathUtils.getAttribute(coverageNode, "value");
            coverageValue = coverageValue.substring(0, coverageValue.indexOf("%"));
            coverages.put("all." + coverageType, Integer.parseInt(coverageValue));
        }
        return coverages;

    }

    /**
     * See {@link AbstractQualityChecker#getCompareType()}
     */
    @Override
    protected int getCompareType() {
        return LESSER_COMPARE_TYPE;
    }

    public static void main(String args[]) throws Exception {
        String errorFile = args[0];
        String thresholdFile = args[1];
        String writePropsOnFail = args[2];

        CoverageChecker checker = new CoverageChecker();
        checker.setNamespace("coverage");
        try {
            checker.process(errorFile, thresholdFile);
        }
        catch (FleetToolsException e) {
            if ("true".equals(writePropsOnFail)) {
                checker.writeProperties(checker.getNamespace() + "-threshold.properties");
            }
            throw e;
        }
        catch (Exception e) {
            throw e;
        }
        checker.writeProperties(thresholdFile);
    }
}
