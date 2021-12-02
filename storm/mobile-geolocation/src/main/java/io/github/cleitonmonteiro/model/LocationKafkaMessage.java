package io.github.cleitonmonteiro.model;

public class LocationKafkaMessage extends LocationModel {
    private String provider;
    private double accuracy;
    private double timestamp;
    private String mobileId;

    @Override
    public String toString() {
        return "LocationKafkaMessage{" +
                "provider='" + getProvider() + '\'' +
                ", accuracy=" + getAccuracy() +
                ", timestamp=" + getTimestamp() +
                ", mobileId='" + getMobileId() + '\'' +
                ", latitude=" + getLatitude() +
                ", longitude=" + getLongitude() +
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

    public String getMobileId() {
        return mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }
}
