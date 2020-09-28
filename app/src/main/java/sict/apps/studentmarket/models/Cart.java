package sict.apps.studentmarket.models;

import java.io.Serializable;

public class Cart implements Serializable {
    private Post item;
    private int qty;
    private double price;

    public Cart(Post item, int qty, double price) {
        this.item = item;
        this.qty = qty;
        this.price = price;
    }

    public Post getItem() {
        return item;
    }

    public void setItem(Post item) {
        this.item = item;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
