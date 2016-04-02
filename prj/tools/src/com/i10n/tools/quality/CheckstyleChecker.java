package com.i10n.tools.quality;

import com.i10n.tools.exception.FleetToolsException;

public class CheckstyleChecker extends AbstractQualityChecker {

    public static void main(String args[]) throws Exception {
        String errorFile = args[0];
        String thresholdFile = args[1];
        String writePropsOnFail = args[2];
        CheckstyleChecker checker = new CheckstyleChecker();
        checker.setNamespace("checkstyle");
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