package com.i10n.tools.quality;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.i10n.tools.exception.FleetToolsException;
import com.i10n.tools.utils.XPathUtils;

public class FindbugsChecker extends AbstractQualityChecker {

    protected Map<String, Integer> parseXML(Document xmlDoc) {
        String fileXpath = "/BugCollection/BugInstance/Class";
        List<Node> fileNodes = XPathUtils.getNodes(xmlDoc, fileXpath);
        Map<String, Integer> errorFiles = new HashMap<String, Integer>();
        for (Node fileNode : fileNodes) {
            String fileName = XPathUtils.getAttribute(fileNode, "classname");
            Integer count = errorFiles.get(fileName);
            if (null == count) {
                errorFiles.put(fileName, 1);
            }
            else {
                errorFiles.put(fileName, count.intValue() + 1);
            }
        }
        return errorFiles;

    }

    public static void main(String args[]) throws Exception {
        String errorFile = args[0];
        String thresholdFile = args[1];
        String writePropsOnFail = args[2];
        FindbugsChecker checker = new FindbugsChecker();
        checker.setNamespace("findbugs");
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