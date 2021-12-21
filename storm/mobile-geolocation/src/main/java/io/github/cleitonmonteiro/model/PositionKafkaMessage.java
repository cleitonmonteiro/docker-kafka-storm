package io.github.cleitonmonteiro.model;

import org.bson.types.ObjectId;

public class PositionKafkaMessage extends PositionModel {
    private String provider;
    private double accuracy;
    private double timestamp;
    private ObjectId mobileId;

    @Override
    public String toString() {
        return "LocationKafkaMessage{" +
                "provider='" + provider + '\'' +
                ", accuracy=" + accuracy +
                ", timestamp=" + timestamp +
                ", mobileId=" + mobileId +
                '}';
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public ObjectId getMobileId() {
        return mobileId;
    }

    public void setMobileId(ObjectId mobileId) {
        this.mobileId = mobileId;
    }
}
