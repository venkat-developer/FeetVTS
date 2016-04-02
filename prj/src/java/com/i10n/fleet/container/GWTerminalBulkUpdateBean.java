package com.i10n.fleet.container;

import java.util.Date;

/**
 * @author joshua
 *
 */
public class GWTerminalBulkUpdateBean implements Comparable<GWTerminalBulkUpdateBean> {

	
	private int analogue1;
	
	private int analogue2;
	
	private int analogue3;
	
	private int analogue4;
	
	private int  analogue5;
	
	private String digitalinput;
	
	private Date occurredAt = new Date();
	
	public GWTerminalBulkUpdateBean(int analogue1, int analogue2, int analogue3,
			int analogue4, int analogue5, String digitalinput, Date occurredAt) {
		super();
		this.analogue1 = analogue1;
		this.analogue2 = analogue2;
		this.analogue3 = analogue3;
		this.analogue4 = analogue4;
		this.analogue5 = analogue5;
		this.digitalinput = digitalinput;
		this.occurredAt = occurredAt;
	}

	 @Override
	public String toString() {
		return "GWTeminalBulkUpdateBean [analogue1=" + analogue1
				+ ", analogue2=" + analogue2 + ", analogue3=" + analogue3
				+ ", analogue4=" + analogue4 + ", analogue5=" + analogue5
				+ ", digitalinput=" + digitalinput + ", occurredAt="
				+ occurredAt + "]";
	}

	public int getAnalogue1() {
		return analogue1;
	}

	public void setAnalogue1(int analogue1) {
		this.analogue1 = analogue1;
	}

	public int getAnalogue2() {
		return analogue2;
	}

	public void setAnalogue2(int analogue2) {
		this.analogue2 = analogue2;
	}

	public int getAnalogue3() {
		return analogue3;
	}

	public void setAnalogue3(int analogue3) {
		this.analogue3 = analogue3;
	}

	public int getAnalogue4() {
		return analogue4;
	}

	public void setAnalogue4(int analogue4) {
		this.analogue4 = analogue4;
	}

	public int getAnalogue5() {
		return analogue5;
	}

	public void setAnalogue5(int analogue5) {
		this.analogue5 = analogue5;
	}

	public Date getOccurredAt() {
		return occurredAt;
	}

	public void setOccurredAt(Date occurredAt) {
		this.occurredAt = occurredAt;
	}
	
	public String getDigitalinput() {
		return digitalinput;
	}

	public void setDigitalinput(String digitalinput) {
		this.digitalinput = digitalinput;
	}
	
	@Override
	public int compareTo(GWTerminalBulkUpdateBean bulk) {
		return occurredAt.compareTo(bulk.getOccurredAt());
	}

}
