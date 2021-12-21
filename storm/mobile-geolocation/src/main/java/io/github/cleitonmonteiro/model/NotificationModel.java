package io.github.cleitonmonteiro.model;

import org.bson.types.ObjectId;

import java.io.Serializable;

public class NotificationModel implements Serializable {
    private ObjectId userId;
    private ObjectId mobileId;
    private boolean fromTrack;
    private PositionModel position;

    public NotificationModel(ObjectId userId, ObjectId mobileId, boolean fromTrack, PositionModel position) {
        this.userId = userId;
        this.mobileId = mobileId;
        this.fromTrack = fromTrack;
        this.position = position;
    }

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

    public PositionModel getPosition() {
        return position;
    }

    public void setPosition(PositionModel position) {
        this.position = position;
    }
}
