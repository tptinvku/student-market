package sict.apps.studentmarket.models;

import java.io.Serializable;
import java.util.List;

public class React implements Serializable {
    private String _id;
    private String postId;
    private String userId;
    private List<Likes> likes;
    // like
    public React(String postId, String userId) {
        this.postId = postId;
        this.userId = userId;
    }

    public React(String _id, String postId, List<Likes> likes) {
        this._id = _id;
        this.postId = postId;
        this.likes = likes;
    }

    public String get_id() {
        return _id;
    }

    public String getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
    }

    public List<Likes> getLikes() {
        return likes;
    }
}
