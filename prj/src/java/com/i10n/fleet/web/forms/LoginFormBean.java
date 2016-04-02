package com.i10n.fleet.web.forms;

/**
 * Bean to store Login Form details
 * 
 * @author sabarish
 * 
 */
public class LoginFormBean {
    private String m_user;
    private String m_password;

    /**
     * Return the user
     * 
     * @return
     */
    public String getUser() {
        return m_user;
    }

    /**
     * Sets the user
     * 
     * @param user
     */
    public void setUser(String user) {
        this.m_user = user;
    }

    /**
     * Returns the password
     * 
     * @return
     */
    public String getPassword() {
        return m_password;
    }

    /**
     * Sets the password
     * 
     * @param password
     */
    public void setPassword(String password) {
        this.m_password = password;
    }

}
