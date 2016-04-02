package com.i10n.tools.skins.exception;

/**
 * SkinParseException is an exception caused while parsing Skin XML's
 * 
 * @author sabarish
 * 
 */
public class SkinParseException extends Exception {

    /**
	 * 
	 */
    private static final long serialVersionUID = 5384160507197259230L;

    public SkinParseException() {
        super();
    }

    public SkinParseException(Throwable cause) {
        super(cause);
    }

    public SkinParseException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SkinParseException(String msg) {
        super(msg);
    }
}
