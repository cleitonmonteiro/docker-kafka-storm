package io.github.cleitonmonteiro.model;

import org.bson.types.ObjectId;

public class SubscriptionModel {
    private ObjectId _id;
    private ObjectId mobileId;
    private ObjectId userId;
    private boolean track;
    private double distanceToNotifier;
    private double latitude;
    private double longitude;

    @Override
    public String toString() {
        return "SubscriptionModel{" +
                "_id=" + _id +
                ", mobileId=" + mobileId +
                ", userId=" + userId +
                ", track=" + track +
                ", distanceToNotifier=" + distanceToNotifier +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public ObjectId getMobileId() {
        return mobileId;
    }

    public void setMobileId(ObjectId mobileId) {
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

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }
}
