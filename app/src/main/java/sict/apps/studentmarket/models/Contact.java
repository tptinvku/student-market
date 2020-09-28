package sict.apps.studentmarket.models;

import java.io.Serializable;

public class Contact implements Serializable {
    private String email;
    private int phone;

    public Contact(String email, int phone) {
        this.email = email;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public int getPhone() {
        return phone;
    }
}
