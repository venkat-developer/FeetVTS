package com.i10n.mina.codec.decoder;

/**
 * 
 * @author Praveen DS (c) i10n 2009
 */
public class ModuleDataException extends Exception {
    /**
     * ID to identify version of the serialized object
     */
    private static final long serialVersionUID = 7725392012278572880L;

    private ErrorCode m_errorCode;

    /**
     * 
     * @author Praveen DS (c) i10n 2009
     */
    public enum ErrorCode {
        CRC_FAILED(100), 
        BYTE_LENGTH_HIGHER(200), 
        BYTE_LENGTH_LESSER(300),
        UNEXPECTED_VALUE_FOR_FIELD(400),
        UNEXPECTED_BYTE_LENGTH(500);
        private int m_status;

        private ErrorCode(int statusCode) {
            m_status = statusCode;
        }

        public int getStatus() {
            return m_status;
        }
    }

    /**
     * 
     * @param message
     * @param errorCode
     */
    public ModuleDataException(String message, ModuleDataException.ErrorCode errorCode) {
        this(message, null, errorCode);
    }

    /**
     * 
     * @param message
     * @param exception
     * @param errorCode
     */
    public ModuleDataException(String message, Exception exception, ModuleDataException.ErrorCode errorCode) {
        super(message, exception);
        if (errorCode == null) {
            throw new IllegalArgumentException("Error code cannot be null");
        }
        m_errorCode = errorCode;
    }

    /**
     * 
     * @param message
     * @param throwable
     * @param errorCode
     */
    public ModuleDataException(String message, Throwable throwable, ModuleDataException.ErrorCode errorCode) {
        super(message, throwable);
        if (errorCode == null) {
            throw new IllegalArgumentException("Error code cannot be null");
        }
        m_errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode(){
        return m_errorCode;
    }

}
