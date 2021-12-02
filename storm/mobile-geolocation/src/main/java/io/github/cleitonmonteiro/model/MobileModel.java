package io.github.cleitonmonteiro.model;

import org.bson.types.ObjectId;

public class MobileModel {
    private ObjectId _id;
    private String description;

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
