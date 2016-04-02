package com.i10n.fleet.util;

/**
 * A class for storing Constants necessary for Fleet V2.
 * 
 * @author sabarish
 * 
 */
public final class Constants {
    private Constants() {
    };

    /**
     * Contains Constants for Session Attributes
     * 
     * @author sabarish
     * 
     */
    public static final class SESSION {
        private SESSION() {
        };

        public static final String ATTR_USER = "currentuser";
        public static final String ATTR_GROUP = "currentgroup";
        public static final String ATTR_USER_OBJECT = "currentuserobject";
        public static final String ATTR_TIME = "localTime";
        public static final String ATTR_TIMEZone = "localTimeZone";
    }
}
