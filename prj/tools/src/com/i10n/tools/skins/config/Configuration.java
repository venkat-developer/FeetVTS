package com.i10n.tools.skins.config;

import java.util.LinkedHashMap;

/*
 * 
 * The Default type with which any Configuration detail is passed around within the 
 * Sikn Management System
 * 
 * Configuration Information (Def1) :- Information that is external to the class
 * 									  that remains constant throught the life time
 * 									  of the class
 * 
 * Configuration Information (Def2) :- Any information that is of the form "key=value" where
 * 									   both key and value are of the type Object Supported By Java
 * 
 * @Autor :- N.Balaji
 */
public class Configuration extends LinkedHashMap<String, Object> {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /* Limiting the scope of the Constructor */
    public Configuration() {
    }
}
