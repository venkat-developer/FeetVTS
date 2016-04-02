package com.i10n.tools.skins.processor.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * A utility that classifies the various types of elements in a skin
 * 
 * @author N.Balaji
 * 
 */

public class SkinElementClassifier {

    public static final int OVERRIDEN_TYPE = 0;
    public static final int BASEELEMENTSFIRST_TYPE = 1;
    public static final int INTERNAL_TYPE = 2;
    public static final int EXTERNAL_TYPE = 3;
    public static final int HYBRID_TYPE = 4;
    public static final int STATIC_TYPE = 5;
    private final List<String> m_overridenType;
    private final List<String> m_baseElementsFirstType;
    private final List<String> m_internalType;
    private final List<String> m_externalType;
    private final List<String> m_hybridType;
    private final List<String> m_staticType;

    public SkinElementClassifier() {
        /* Initializing all the types */
        m_overridenType = initializeOverriden();
        m_baseElementsFirstType = initializeBaseElementFirst();
        m_internalType = initializeInternal();
        m_externalType = initializeExternal();
        m_hybridType = initializeHybrid();
        m_staticType = initializeStatic();
    }

    public List<String> initializeStatic() {
        List<String> result = new ArrayList<String>();
        result.add("script");
        result.add("stylesheet");
        return result;
    }

    public List<String> initializeOverriden() {
        List<String> result = new ArrayList<String>();
        result.add("script");
        result.add("template");
        result.add("data");
        return result;
    }

    public List<String> initializeBaseElementFirst() {
        List<String> result = new ArrayList<String>();
        result.add("stylesheet");
        return result;
    }

    public List<String> initializeInternal() {
        List<String> result = new ArrayList<String>();
        result.add("template");
        result.add("data");
        return result;
    }

    public List<String> initializeExternal() {
        List<String> result = new ArrayList<String>();
        return result;
    }

    public List<String> initializeHybrid() {
        List<String> result = new ArrayList<String>();
        result.add("script");
        result.add("stylesheet");
        return result;
    }

    public List<Integer> classify(String elementName) {
        List<Integer> result = new ArrayList<Integer>();
        /* Classifying the elements */
        if (m_internalType.contains(elementName)) result.add(INTERNAL_TYPE);
        if (m_baseElementsFirstType.contains(elementName))
            result.add(BASEELEMENTSFIRST_TYPE);
        if (m_externalType.contains(elementName)) result.add(EXTERNAL_TYPE);
        if (m_hybridType.contains(elementName)) result.add(HYBRID_TYPE);
        if (m_overridenType.contains(elementName)) result.add(OVERRIDEN_TYPE);
        if (m_staticType.contains(elementName)) result.add(STATIC_TYPE);
        return result;
    }
}
