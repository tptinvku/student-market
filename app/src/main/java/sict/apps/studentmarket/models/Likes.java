package sict.apps.studentmarket.models;

import java.io.Serializable;

public class Likes implements Serializable {
    private String _id;
    private String userId;

    public Likes(String _id, String userId) {
        this._id = _id;
        this.userId = userId;
    }

    public String get_id() {
        return _id;
    }

    public String getUserId() {
        return userId;
    }
}
