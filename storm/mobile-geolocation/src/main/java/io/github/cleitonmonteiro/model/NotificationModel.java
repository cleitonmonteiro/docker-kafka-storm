package io.github.cleitonmonteiro.model;

import org.bson.types.ObjectId;

public class NotificationModel {
    private ObjectId userId;

    public NotificationModel(ObjectId userId, ObjectId mobileId, boolean fromTrack) {
        this.userId = userId;
        this.mobileId = mobileId;
        this.fromTrack = fromTrack;
    }

    private ObjectId mobileId;
    private boolean fromTrack;

    @Override
    public String toString() {
        return "NotificationModel{" +
                "userId=" + userId +
                ", mobileId=" + mobileId +
                ", fromTrack=" + fromTrack +
                '}';
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public ObjectId getMobileId() {
        return mobileId;
    }

    public void setMobileId(ObjectId mobileId) {
        this.mobileId = mobileId;
    }

    public boolean isFromTrack() {
        return fromTrack;
    }

    public void setFromTrack(boolean fromTrack) {
        this.fromTrack = fromTrack;
    }
}
