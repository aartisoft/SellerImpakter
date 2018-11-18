package com.impakter.seller.object.seller;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReplyCommentRespond {

    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("data")
    private List<Data> data;
    @Expose
    @SerializedName("allPage")
    private int allPage;
    @Expose
    @SerializedName("status")
    private String status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public int getAllPage() {
        return allPage;
    }

    public void setAllPage(int allPage) {
        this.allPage = allPage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Data {
        @Expose
        @SerializedName("date")
        private int date;
        @Expose
        @SerializedName("content")
        private String content;
        @Expose
        @SerializedName("fullName")
        private String fullName;
        @Expose
        @SerializedName("avatar")
        private String avatar;
        @Expose
        @SerializedName("userId")
        private int userId;
        @Expose
        @SerializedName("id")
        private int id;

        public int getDate() {
            return date;
        }

        public void setDate(int date) {
            this.date = date;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
