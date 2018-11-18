package com.impakter.seller.object.seller;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileRespond {

    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("data")
    private Data data;
    @Expose
    @SerializedName("status")
    private String status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Data {
        @Expose
        @SerializedName("followers")
        private int followers;
        @Expose
        @SerializedName("location")
        private String location;
        @Expose
        @SerializedName("name")
        private String name;
        @Expose
        @SerializedName("avatar")
        private String avatar;
        @Expose
        @SerializedName("cover")
        private String cover;
        @Expose
        @SerializedName("id")
        private int id;

        public int getFollowers() {
            return followers;
        }

        public void setFollowers(int followers) {
            this.followers = followers;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
