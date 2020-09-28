package sict.apps.studentmarket.models;

import java.io.Serializable;
import java.util.List;

import okhttp3.MultipartBody;

public class User implements Serializable {
    private String _id;
    private String username;
    private String address;
    private long phone;
    private boolean gender;
    private String email;
    private String password;
    private String token;
    // preload
    public User(String token, String _id, String username, long phone, boolean gender, String email) {
        this.token = token;
        this._id = _id;
        this.username = username;
        this.phone = phone;
        this.gender = gender;
        this.email = email;
    }


    //signup
    public User(String username, long phone, String email, String password, boolean gender) {
        this.username = username;
        this.phone = phone;
        this.gender = gender;
        this.email = email;
        this.password = password;
    }
    // signin
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public String get_id() {
        return _id;
    }

    public String getUsername() {
        return username;
    }

    public String getAddress() {
        return address;
    }

    public long getPhone() {
        return phone;
    }

    public boolean isGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
