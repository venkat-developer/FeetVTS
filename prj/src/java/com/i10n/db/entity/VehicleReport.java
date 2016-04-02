package com.i10n.db.entity;

import java.util.ArrayList;
import java.util.Date;

import com.googlecode.jsonplugin.annotations.JSON;
import com.i10n.db.entity.IEntity.IEntity;

/**
 * @author dharamraju.v
 *
 */
public class VehicleReport implements IEntity<VehicleReport> {

      private String imageName;

        private String imageMap;

        private double distance;

        private Date startDate;

        private Date endDate;

        private double maxspeed;

        private double minspeed;

        private double avgspeed;
      
        private ArrayList<String> putValueForInteractiveGraph;
        
        public double getAvgspeed() {
            return avgspeed;
        }

        public void setAvgspeed(double avgspeed) {
            this.avgspeed = avgspeed;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        @JSON(format = "MM/dd/yyyy  HH:mm:ss")
        public Date getEndDate() {
            return endDate;
        }
        @JSON(format = "MM/dd/yyyy  HH:mm:ss")
        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }

        public String getImageMap() {
            return imageMap;
        }

        public void setImageMap(String imageMap) {
            this.imageMap = imageMap;
        }

        public String getImageName() {
            return imageName;
        }

        public void setImageName(String imageName) {
            this.imageName = imageName;
        }

        public double getMaxspeed() {
            return maxspeed;
        }

        public void setMaxspeed(double maxspeed) {
            this.maxspeed = maxspeed;
        }

        public double getMinspeed() {
            return minspeed;
        }

        public void setMinspeed(double minspeed) {
            this.minspeed = minspeed;
        }

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }  
  
    @Override
    public boolean equalsEntity(VehicleReport object) {
        // TODO Auto-generated method stub
        return false;
    }

	/**
	 * @return the putValueForInteractiveGraph
	 */
	public ArrayList<String> getPutValueForInteractiveGraph() {
		return putValueForInteractiveGraph;
	}

	/**
	 * @param putValueForInteractiveGraph the putValueForInteractiveGraph to set
	 */
	public void setPutValueForInteractiveGraph(
			ArrayList<String> putValueForInteractiveGraph) {
		this.putValueForInteractiveGraph = putValueForInteractiveGraph;
	}

}