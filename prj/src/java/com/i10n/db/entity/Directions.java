package com.i10n.db.entity;
import java.util.List;

import org.postgis.Point;
 
/**
 * @author joshua
 *
 */
public class Directions {
     
    private Point origin;
    private Point destination;
    private List<Point> route;
    private long distance;
     
    public Point getDestination() {
        return destination;
    }
     
    public long getDistance() {
        return distance;
    }
     
    public Point getOrigin() {
        return origin;
    }
     
    public List<Point> getRoute() {
        return route;
    }
     
    public void setDestination(Point destination) {
        this.destination = destination;
    }
     
    public void setDistance(long distance) {
        this.distance = distance;
    }
     
    public void setOrigin(Point origin) {
        this.origin = origin;
    }
     
    public void setRoute(List<Point> route) {
        this.route = route;
    }
} 