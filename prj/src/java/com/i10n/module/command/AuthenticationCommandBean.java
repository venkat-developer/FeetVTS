package com.i10n.module.command;

/**
 * Please refer https://spreadsheets.google.com/a/i10n.com/ccc?key=rot1aPy9pzD_6MM0U1iUezQ&hl=en
 * 
 * @author Praveen DS (c) Versata 2009
 */
public class AuthenticationCommandBean implements ICommandBean {
    private short m_authentication;
    private String m_imei;
    private int m_driverID;

    public AuthenticationCommandBean(short authentication, String imei, int driverID) {
        m_authentication = authentication;
        m_imei = imei;
        m_driverID = driverID;
    }

    /**
     * @return the m_authentication
     */
    public short isAuthentication() {
        return m_authentication;
    }

    /**
     * @return the m_imei
     */
    public String getImei() {
        return m_imei;
    }

    /**
     * @return the m_driverID
     */
    public int getDriverID() {
        return m_driverID;
    }

    /**
     * 
     * @return
     */
    public String getDescription() {
        return "AuthenticationCommand";
    }

    /**
     * 
     */
    public Command getCommandID() {
//        return Command.AUTHENTICATION;
    	return Command.DEFAULT;
    }
    
    public String toString(){
    	StringBuilder str = new StringBuilder();
    	str.append(m_authentication).append("|").append(m_imei).append("|").append(m_driverID);
    	return str.toString();
    }
}
