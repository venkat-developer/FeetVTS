/**
 * 
 */
package com.i10n.module.command;

/**
 * @author sreekanth
 *
 */
public class DeleteDriverCommand implements ICommandBean {
	
	private long m_driverid;
	private boolean m_driverDeleted;
	private String m_imei;
	
	public DeleteDriverCommand(Long driverId, boolean mDriverDeleted,
			String mImei) {
		super();
		m_driverid = driverId;
		m_driverDeleted = mDriverDeleted;
		m_imei = mImei;
	}
	
	@Override
	public Command getCommandID() {
//		return Command.DELETEDRIVER;
		return Command.DEFAULT;
	}

	@Override
	public String getDescription() {
		return "DeleteDriverCommand";
	}

	public long getM_driverid() {
		return m_driverid;
	}

	public void setM_driverid(long mDriverid) {
		m_driverid = mDriverid;
	}

	public boolean isM_driverDeleted() {
		return m_driverDeleted;
	}

	public void setM_driverDeleted(boolean mDriverDeleted) {
		m_driverDeleted = mDriverDeleted;
	}

	public String getM_imei() {
		return m_imei;
	}

	public void setM_imei(String mImei) {
		m_imei = mImei;
	}
	
	public String toString(){
		StringBuilder instanceData = new StringBuilder();
		instanceData.append(m_driverid).append("|").append(m_driverDeleted).append("|").append(m_imei);
		return instanceData.toString();
	}

}
