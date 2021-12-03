package io.github.cleitonmonteiro.model;

import org.bson.types.ObjectId;

public class UserModel {
    private ObjectId _id;
    private String name;

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "_id=" + _id +
                ", name='" + name + '\'' +
                '}';
    }
}
