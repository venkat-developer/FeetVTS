package com.i10n.fleet.util;

import org.postgis.Point;

public class CustomCoordinates{
    static final double sq2p1 = 2.414213562373095048802e0;
    static final double sq2m1  = .414213562373095048802e0;
    static final double p4  = .161536412982230228262e2;
    static final double p3  = .26842548195503973794141e3;
    static final double p2  = .11530293515404850115428136e4;
    static final double p1  = .178040631643319697105464587e4;
    static final double p0  = .89678597403663861959987488e3;
    static final double q4  = .5895697050844462222791e2;
    static final double q3  = .536265374031215315104235e3;
    static final double q2  = .16667838148816337184521798e4;
    static final double q1  = .207933497444540981287275926e4;
    static final double q0  = .89678597403663861962481162e3;
    static final double PIO2 = 1.5707963267948966135E0;
    static final double nan = (0.0/0.0);

    /**
     * @param oldLat1
     * @param oldLon1
     * @param newLat2
     * @param newLon2
     * @return distance in kilometers
     */
	public static double distance(double oldLat1, double oldLon1,double newLat2,double newLon2){
		double oldLat=Math.toRadians(oldLat1),
		oldLong=Math.toRadians(oldLon1),
		newLat=Math.toRadians(newLat2),
		newLong=Math.toRadians(newLon2);
	
		double r=6371; // in km
		double deltaLat=newLat-oldLat;
		double deltaLong=newLong-oldLong;
		double a=Math.sin(deltaLat/2.0D)*Math.sin(deltaLat/2.0D)+
			     Math.cos(oldLat)*Math.cos(newLat)*Math.sin(deltaLong/2.0D)*Math.sin(deltaLong/2.0D);
		double c=2D * atan2(Math.sqrt(a), Math.sqrt(1.0D - a));
		double d=r*c;
		return d;
	}
	
	
	public static double distance(Point oldLatLng, Point newLatLng){
		return distance(oldLatLng.x, oldLatLng.y, newLatLng.x,newLatLng.y);
	}
	
	public static double atan2(double arg1, double arg2){
		if(arg1+arg2 == arg1){
			if(arg1 >= 0)
				return PIO2;
			return -PIO2;
		}
		arg1 = atan(arg1/arg2);
		if(arg2 < 0){
			if(arg1 <= 0)
				return arg1 + Math.PI;
			return arg1 - Math.PI;
		}
		return arg1;
	}

	public static double atan(double arg){
        if(arg > 0)
            return msatan(arg);
        return -msatan(-arg);
    }
	
	 private static double msatan(double arg){
		 if(arg < sq2m1)
			 return mxatan(arg);
	     if(arg > sq2p1)
	         return PIO2 - mxatan(1/arg);
	     return PIO2/2 + mxatan((arg-1)/(arg+1));
	 }
	    
	 private static double mxatan(double arg){
		 double argsq, value;
	     argsq = arg*arg;
	     value = ((((p4*argsq + p3)*argsq + p2)*argsq + p1)*argsq + p0);
	     value = value/(((((argsq + q4)*argsq + q3)*argsq + q2)*argsq + q1)*argsq + q0);
	     return value*arg;
	 }
   	
	 
}