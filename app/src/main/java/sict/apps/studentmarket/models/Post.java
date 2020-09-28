package sict.apps.studentmarket.models;

import java.io.Serializable;
import java.util.List;

public class Post implements Serializable {
    private String _id;
    private String categoryId;
    private List<String> product_imageList;
    private String product_imagePath;
    private String product_name;
    private double product_price;
    private String product_description;
    private String userId;
    private String address;
    private long timestamp;
    private int likes;
    private int comments;
    private Contact contact;

    // update
    public Post(String categoryId, String product_name, double product_price, String product_description, String address, Contact contact) {
        this.categoryId = categoryId;
        this.product_name = product_name;
        this.product_price = product_price;
        this.product_description = product_description;
        this.address = address;
        this.contact = contact;
    }

    // info
    public Post(String _id, String categoryId, List<String> product_imageList, String product_imagePath, String product_name, double product_price, String product_description) {
        this._id = _id;
        this.categoryId = categoryId;
        this.product_imageList = product_imageList;
        this.product_imagePath = product_imagePath;
        this.product_name = product_name;
        this.product_price = product_price;
        this.product_description = product_description;
    }
    // create post
    public Post(String categoryId, String product_name, double product_price,
                String product_description, List<String> product_imageList, String userId,
                String address) {
        this.categoryId = categoryId;
        this.product_name = product_name;
        this.product_price = product_price;
        this.product_description = product_description;
        this.product_imageList = product_imageList;
        this.userId = userId;
        this.address = address;
    }
    // item cart
    public Post(String _id, String categoryId, String product_imagePath, String product_name, double product_price) {
        this._id = _id;
        this.categoryId = categoryId;
        this.product_imagePath = product_imagePath;
        this.product_name = product_name;
        this.product_price = product_price;
    }
    // posts
    public Post(String _id, String userId, String categoryId, List<String> product_imageList,
                String product_imagePath, String product_name, double product_price,
                String product_description, String address, long timestamp, int likes, Contact contact) {
        this._id = _id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.product_imageList = product_imageList;
        this.product_imagePath = product_imagePath;
        this.product_name = product_name;
        this.product_price = product_price;
        this.product_description = product_description;
        this.address = address;
        this.timestamp = timestamp;
        this.likes = likes;
        this.contact = contact;
    }

    // new posts
    public Post(String _id, String userId, String categoryId, List<String> product_imageList,
                String product_imagePath, String product_name, double product_price,
                String product_description, String address, long timestamp, int likes) {
        this._id = _id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.product_imageList = product_imageList;
        this.product_imagePath = product_imagePath;
        this.product_name = product_name;
        this.product_price = product_price;
        this.product_description = product_description;
        this.address = address;
        this.timestamp = timestamp;
        this.likes = likes;
    }

    // splash
    public Post(String _id, String categoryId, String product_name, double product_price, String product_description) {
        this._id = _id;
        this.categoryId = categoryId;
        this.product_name = product_name;
        this.product_price = product_price;
        this.product_description = product_description;
    }


    public String get_id() {
        return _id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public List<String>  getProduct_imageList() {
        return product_imageList;
    }

    public String getProduct_imagePath() {
        return product_imagePath;
    }

    public String getProduct_name() {
        return product_name;
    }

    public double getProduct_price() {
        return product_price;
    }

    public String getProduct_description() {
        return product_description;
    }

    public String getUserId() {
        return userId;
    }

    public String getAddress() {
        return address;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setProduct_imageList(List<String> product_imageList) {
        this.product_imageList = product_imageList;
    }

    public void setProduct_imagePath(String product_imagePath) {
        this.product_imagePath = product_imagePath;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setProduct_price(double product_price) {
        this.product_price = product_price;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public Contact getContact() {
        return contact;
    }
}
