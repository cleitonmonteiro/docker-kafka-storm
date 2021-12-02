package io.github.cleitonmonteiro.model;

import org.bson.types.ObjectId;

public class SubscriptionModel {
    private ObjectId _id;
    private String mobileId;
    private boolean track;
    private double distanceToNotifier;
    private double latitude;
    private double longitude;

    @Override
    public String toString() {
        return "SubscriptionModel{" +
                "_id=" + get_id() +
                ", mobileId='" + getMobileId() + '\'' +
                ", track=" + isTrack() +
                ", distanceToNotifier=" + getDistanceToNotifier() +
                ", latitude=" + getLatitude() +
                ", longitude=" + getLongitude() +
                '}';
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getMobileId() {
        return mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public boolean isTrack() {
        return track;
    }

    public void setTrack(boolean track) {
        this.track = track;
    }

    public double getDistanceToNotifier() {
        return distanceToNotifier;
    }

    public void setDistanceToNotifier(double distanceToNotifier) {
        this.distanceToNotifier = distanceToNotifier;
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
