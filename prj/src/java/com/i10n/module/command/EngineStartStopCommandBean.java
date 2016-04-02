package com.i10n.module.command;

public class EngineStartStopCommandBean implements ICommandBean {
    private boolean m_engineStarted;
    private String m_imei;
    private double m_latitude;
    private double m_longitude;

    public EngineStartStopCommandBean(boolean engineStarted, String imei, double latitude, double longitude) {
        this.m_engineStarted = engineStarted;
        this.m_imei = imei;
        this.m_latitude = latitude;
        this.m_longitude = longitude;
    }

    @Override
    public Command getCommandID() {
//        return Command.ENGINESTARTSTOP;
        return Command.DEFAULT;
    }

    @Override
    public String getDescription() {
        return "EngineStartStopCommand";
    }

    /**
     * @return the engineStarted
     */
    public boolean isEngineStarted() {
        return m_engineStarted;
    }

    /**
     * @return the imei
     */
    public String getImei() {
        return m_imei;
    }
    
    public String toString(){
    	StringBuilder retVal = new StringBuilder();
    	retVal.append(m_engineStarted).append("|").append(m_imei).append("|").append(m_latitude).append("|").append(m_longitude);
    	return retVal.toString();
    }

	public double getLatitude() {
		return m_latitude;
	}

	public void setLatitude(double latitude) {
		this.m_latitude = latitude;
	}

	public double getLongitude() {
		return m_longitude;
	}

	public void setLongitude(double longitude) {
		this.m_longitude = longitude;
	}

}
