package sict.apps.studentmarket.models;

import java.io.Serializable;

public class RoomChat implements Serializable {
    private String _id;
    private String sellerId;
    private String userId;

    public RoomChat(String sellerId, String userId) {
        this.sellerId = sellerId;
        this.userId = userId;
    }
}
