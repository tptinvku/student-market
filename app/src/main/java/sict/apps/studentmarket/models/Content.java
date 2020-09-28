package sict.apps.studentmarket.models;

import java.io.Serializable;

public class Content implements Serializable {
     private String _id;
     private String postId;
     private String senderId;
     private String content;

    public Content(String postId, String senderId, String content) {
        this.postId = postId;
        this.senderId = senderId;
        this.content = content;
    }


    public Content(String content) {
        this.content = content;
    }

    public String get_id() {
        return _id;
    }

    public String getPostId() {
        return postId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getContent() {
        return content;
    }


}
