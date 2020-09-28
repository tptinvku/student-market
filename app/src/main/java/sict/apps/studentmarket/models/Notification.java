package sict.apps.studentmarket.models;

import java.io.Serializable;
import java.util.List;

public class Notification implements Serializable {
    private String _id;
    private String sellerId;
    private List<Content> contents;

    public Notification(String sellerId, List<Content> contents) {
        this.sellerId = sellerId;
        this.contents = contents;
    }

    public String get_id() {
        return _id;
    }

    public String getSellerId() {
        return sellerId;
    }

    public List<Content> getContents() {
        return contents;
    }
}
