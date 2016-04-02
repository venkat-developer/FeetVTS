/**
 * 
 */
package com.i10n.fleet.util;

/**
 * @author HEMANT
 *
 */
public class ModuleBooleanParameters {

	boolean dontUpdateLatLongMaxSpeedDistance;

	boolean updateAnalogValuesOnly;

	boolean updateFirstPingCounter;

	boolean resetSpeedToZero;

	public ModuleBooleanParameters(
			boolean dontUpdateLatLongMaxSpeedDistance,
			boolean updateFirstPingCounter, boolean resetSpeedToZero,
			boolean updateAnalogValuesOnly) {
		super();
		this.dontUpdateLatLongMaxSpeedDistance = dontUpdateLatLongMaxSpeedDistance;
		this.updateFirstPingCounter = updateFirstPingCounter;
		this.updateAnalogValuesOnly = updateAnalogValuesOnly;
		this.resetSpeedToZero = resetSpeedToZero;
	}

	public boolean isDontUpdateLatLongMaxSpeedDistance() {
		return dontUpdateLatLongMaxSpeedDistance;
	}

	public void setDontUpdateLatLongMaxSpeedDistance(
			boolean dontUpdateLatLongMaxSpeedDistance) {
		this.dontUpdateLatLongMaxSpeedDistance = dontUpdateLatLongMaxSpeedDistance;
	}

	public boolean isUpdateAnalogValuesOnly() {
		return updateAnalogValuesOnly;
	}

	public void setUpdateAnalogValuesOnly(boolean updateAnalogValuesOnly) {
		this.updateAnalogValuesOnly = updateAnalogValuesOnly;
	}

	public boolean isUpdateFirstPingCounter() {
		return updateFirstPingCounter;
	}

	public void setUpdateFirstPingCounter(boolean updateFirstPingCounter) {
		this.updateFirstPingCounter = updateFirstPingCounter;
	}

	public boolean isResetSpeedToZero() {
		return resetSpeedToZero;
	}

	public void setResetSpeedToZero(boolean resetSpeedToZero) {
		this.resetSpeedToZero = resetSpeedToZero;
	}
}