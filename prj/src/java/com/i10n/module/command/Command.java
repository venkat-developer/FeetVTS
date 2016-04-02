package com.i10n.module.command;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Praveen DS (c) Versata 2009
 * @update Dharmaraju V
 */
public enum Command {
	DEFAULT((short)0,0), 
	/*AUTHENTICATION((short) 1, 20),SMARTCARDALERT((short)2,32), POLL((short) 3, 18), NEWDRIVERADDED((short) 4, 20),
    DELETEDRIVER((short) 6, 21), ENGINESTARTSTOP((short) 7, 25),*/ 
	SEQUENCEREQUEST((short) 11, 16), 
	ETA_GUJRAT((short)12, 50),
    ALERT_OR_VIOLATION ((short)13, 50),
    ETA_BHOPAL_CHINESE((short)14, 50),
	ETA_BHOPAL_ARYA((short)15, 50),
	MBMC_DYNAMIC_ROUTE_ASSIGN((short)50, 52),
    DISP_DATA((short)30, 22);
    
    private short code;
    private int length;
    private static final Map<Short, Command> lookup = new HashMap<Short, Command>();
    private static final Map<Command, Integer> lookupLength = new HashMap<Command, Integer>();

    private Command(short code, int length) {
        this.code = code;
        this.length = length;
    }

    public short getCode() {
        return code;
    }

    public int getLength() {
        return length;
    }

    public static Command get(short code) {
    	if(lookup.get(code) == null ){
    		return DEFAULT;
    	}else{
    		return lookup.get(code);
    	}
    }
    
    public static int getLengthForCommand(Command cmd){
        return lookupLength.get(cmd);
    }

    static {
        for (Command s : EnumSet.allOf(Command.class)){
            lookup.put(s.getCode(), s);
        }
        for (Command s : EnumSet.allOf(Command.class)){
            lookupLength.put(s, s.getLength());
        }
    }
}
