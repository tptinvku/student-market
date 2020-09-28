package sict.apps.studentmarket.models;

import java.io.Serializable;

public class Message implements Serializable {
    private String _id;
    private String cvstId;
    private String senderId;
    private String message;

    public Message(String cvstId, String senderId, String message) {
        this.cvstId = cvstId;
        this.senderId = senderId;
        this.message = message;
    }

    public String get_id() {
        return _id;
    }

    public String getCvstId() {
        return cvstId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }
}
