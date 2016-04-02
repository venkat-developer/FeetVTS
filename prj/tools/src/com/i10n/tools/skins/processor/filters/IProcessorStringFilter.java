package com.i10n.tools.skins.processor.filters;

/**
 * 
 * A Filter that accepts a String Input and And returns a boolean value stating
 * if the value is filtered
 * 
 * @author N.Balaji
 * 
 */

public interface IProcessorStringFilter extends IProcessorFilter {
    boolean isFiltered(String input);
}
