package com.i10n.module.command;

/**
 * 
 * @author Praveen DS (c) Versata 2009
 */
public class CommandInterpretationResult {
    private ICommandBean m_command = null;
    private IResponse m_response = null;
    
    public CommandInterpretationResult(ICommandBean commandBean, IResponse response){
        this.m_command = commandBean;
        this.m_response = response;
    }

    /**
     * @return the m_command
     */
    public ICommandBean getCommand() {
        return m_command;
    }

    /**
     * @param m_command the m_command to set
     */
    public void setCommand(ICommandBean m_command) {
        this.m_command = m_command;
    }

    /**
     * @return the m_response
     */
    public IResponse getResponse() {
        return m_response;
    }

    /**
     * @param m_response the m_response to set
     */
    public void setResponse(IResponse m_response) {
        this.m_response = m_response;
    }
}
