package sict.apps.studentmarket.models;

import java.io.Serializable;

public class Category implements Serializable {
    private String _id;
    private String category_imagePath;
    private String category_name;

    public Category(String category_name) {
        this.category_name = category_name;
    }

    public Category(String _id, String category_name) {
        this._id = _id;
        this.category_name = category_name;
    }

    public Category(String _id, String category_imagePath, String category_name) {
        this._id = _id;
        this.category_imagePath = category_imagePath;
        this.category_name = category_name;
    }

    public String get_id() {
        return _id;
    }

    public String getCategory_imagePath() {
        return category_imagePath;
    }

    public String getCategory_name() {
        return category_name;
    }
}
