package com.impakter.seller.object;

import com.google.gson.annotations.SerializedName;

public class BaseMessageRespond {
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    @SerializedName("email")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
