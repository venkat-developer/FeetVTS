package com.i10n.tools.skins.processor.filters.impl;

import java.util.ArrayList;
import java.util.List;

import com.i10n.tools.skins.processor.filters.IProcessorStringFilter;

/**
 * 
 * A simple string filter that checks whether the input string matches any of
 * the specified patterns
 * 
 * @author N.Balaji
 * 
 */
public class SimpleStringFilter implements IProcessorStringFilter {
    private final String FILTER_TYPE = "StringFileter";
    private List<String> m_listOfFilteredItems;

    public SimpleStringFilter() {
        m_listOfFilteredItems = new ArrayList<String>();
        m_listOfFilteredItems.add("http");
    }

    @Override
    public String filterType() {
        return FILTER_TYPE;
    }

    @Override
    public boolean isFiltered(String input) {
        boolean result = false;
        for (String regex : m_listOfFilteredItems) {
            if (input.indexOf(regex) >= 0) {
                result = true;
                break;
            }
        }
        return result;
    }
}
