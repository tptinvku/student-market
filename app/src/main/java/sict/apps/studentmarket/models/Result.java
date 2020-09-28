package sict.apps.studentmarket.models;

import java.io.Serializable;

public class Result implements Serializable {
    private String _id;
    private String categoryId;
    private String product_imagePath;
    private String product_name;
    private double product_price;

    public Result(String _id, String categoryId, String product_imagePath, String product_name, double product_price) {
        this._id = _id;
        this.categoryId = categoryId;
        this.product_imagePath = product_imagePath;
        this.product_name = product_name;
        this.product_price = product_price;
    }

    public String get_id() {
        return _id;
    }

    public String getCategoryId() {
        return categoryId;
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
}
