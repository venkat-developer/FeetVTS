package com.i10n.module.command;

public class PollCommandBean implements ICommandBean {
    private short m_pollId;
    private String m_imei;

    public PollCommandBean(short pollId, String imei) {
        this.m_pollId = pollId;
        this.m_imei = imei;
    }

    @Override
    public Command getCommandID() {
//        return Command.POLL;
        return Command.DEFAULT;
    }

    @Override
    public String getDescription() {
        return "PollCommand";
    }

    /**
     * @return the m_pollId
     */
    public short getPollId() {
        return m_pollId;
    }

    /**
     * @return the m_imei
     */
    public String getImei() {
        return m_imei;
    }
    
    public String toString(){
    	StringBuilder str = new StringBuilder();
    	
    	str.append(m_pollId).append("|").append(m_imei);
    	
    	return str.toString();
    }

}
