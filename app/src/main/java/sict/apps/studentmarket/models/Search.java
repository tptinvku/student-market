package sict.apps.studentmarket.models;

import java.io.Serializable;

public class Search implements Serializable {
    private String userId;
    private String keyword;

    public Search(String userId, String keyword) {
        this.userId = userId;
        this.keyword = keyword;
    }

    public String getUserId() {
        return userId;
    }

    public String getKeyword() {
        return keyword;
    }
}
