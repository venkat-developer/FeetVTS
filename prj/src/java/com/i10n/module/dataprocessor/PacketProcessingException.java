package com.i10n.module.dataprocessor;

/**
 * 
 * @author Vishnu
 */
public class PacketProcessingException extends Exception {
    /**
     * ID to identify version of the serialized object
     */
    private static final long serialVersionUID = 7725392012211572880L;

    private PacketErrorCode m_errorCode;

    /**
     * 
     */
    public enum PacketErrorCode {
        TRIP_NOT_ADDED(1),
        VEHICLE_ID_IS_ZERO(2),
        PACKET_REPEATED(3),
        BULK_DATA_EMPTY(4),
        PACKET_ERRATA(5),
        NAVTEQ_IMEI(6),
        QUEUE_FULL(7),
        PROCESSING_TOOK_TOO_LONG(8),
        UNTIMELY_RESPONSE_TO_MODULE(9);
        
        private int m_status;

        private PacketErrorCode(int statusCode) {
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
    public PacketProcessingException(String message, PacketProcessingException.PacketErrorCode errorCode) {
        this(message, null, errorCode);
    }


    /**
     * 
     * @param message
     * @param exception
     * @param errorCode
     */
    public PacketProcessingException(PacketErrorCode errorCode) {
        super("Exception Code : "+errorCode.m_status, new Exception());
        m_errorCode = errorCode;
    }
    /**
     * 
     * @param message
     * @param exception
     * @param errorCode
     */
    public PacketProcessingException(String message, Exception exception, PacketProcessingException.PacketErrorCode errorCode) {
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
    public PacketProcessingException(String message, Throwable throwable, PacketProcessingException.PacketErrorCode errorCode) {
        super(message, throwable);
        if (errorCode == null) {
            throw new IllegalArgumentException("Error code cannot be null");
        }
        m_errorCode = errorCode;
    }
    
    public PacketErrorCode getErrorCode(){
        return m_errorCode;
    }
    

}
