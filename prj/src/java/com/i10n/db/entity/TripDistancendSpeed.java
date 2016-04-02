package com.i10n.db.entity;

public class TripDistancendSpeed {
	
   private double speed;
   private double distance;
	public TripDistancendSpeed(double speed, double distance) {
		this.speed=speed;
		this.distance=distance;
		
		// TODO Auto-generated constructor stub
	}
	public double getSpeed() {
		return speed;
	}
	public void setSpeed(double speed) {
		this.speed = speed;
		
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}

}
