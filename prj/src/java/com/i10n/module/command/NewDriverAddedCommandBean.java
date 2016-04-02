package com.i10n.module.command;

/**
 * 
 * @author Praveen DS (c) Versata 2009
 */
public class NewDriverAddedCommandBean implements ICommandBean {
    private boolean isAdded;
    private String m_imei;
    private int m_driverId; // -1 if no driver id is returned from the module
    private String m_driverName;

    /**
     * 
     * @param isAdded
     * @param imei
     * @param driverId
     * @param driverName
     */
    public NewDriverAddedCommandBean(boolean isAdded, String imei, int driverId, String driverName) {
        this.isAdded = isAdded;
        this.m_imei = imei;
        this.m_driverId = driverId;
        this.m_driverName = driverName;
    }

    @Override
    public Command getCommandID() {
//        return Command.NEWDRIVERADDED;
        return Command.DEFAULT;
    }

    @Override
    public String getDescription() {
        return "NewDriverAddedCommand";
    }
    
    public String toString(){
    	StringBuilder str = new StringBuilder();
    	str.append("isAdded: ").append(isAdded).append("\nIMEI: ").append(m_imei).append("\nDriver Id: ").append(m_driverId)
    	.append("\n").append(m_driverName);
    	return str.toString();
    }

	public boolean isAdded() {
		return isAdded;
	}

	public void setAdded(boolean isAdded) {
		this.isAdded = isAdded;
	}

	public String getM_imei() {
		return m_imei;
	}

	public void setM_imei(String m_imei) {
		this.m_imei = m_imei;
	}

	public int getM_driverId() {
		return m_driverId;
	}

	public void setM_driverId(int id) {
		m_driverId = id;
	}

	public String getM_driverName() {
		return m_driverName;
	}

	public void setM_driverName(String name) {
		m_driverName = name;
	}

}
