package io.github.cleitonmonteiro.model;

import java.io.Serializable;

public class PositionModel implements Serializable {
    private double latitude;
    private double longitude;

    public PositionModel() {
        super();
    }
    public PositionModel(double latitude, double longitude) {
        this.setLatitude(latitude);
        this.setLongitude(longitude);
    }

    @Override
    public String toString() {
        return "LocationModel{" +
                "latitude=" + getLatitude() +
                ", longitude=" + getLongitude() +
                '}';
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
