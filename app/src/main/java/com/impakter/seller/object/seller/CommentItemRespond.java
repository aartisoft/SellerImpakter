package com.impakter.seller.object.seller;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommentItemRespond {

    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("data")
    private List<Data> data;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Data {
        @Expose
        @SerializedName("replyToName")
        private String replyToName;
        @Expose
        @SerializedName("replyToId")
        private int replyToId;
        @Expose
        @SerializedName("dateCreated")
        private int dateCreated;
        @Expose
        @SerializedName("userAvatar")
        private String userAvatar;
        @Expose
        @SerializedName("userName")
        private String userName;
        @Expose
        @SerializedName("userId")
        private int userId;
        @Expose
        @SerializedName("content")
        private String content;

        public String getReplyToName() {
            return replyToName;
        }

        public void setReplyToName(String replyToName) {
            this.replyToName = replyToName;
        }

        public int getReplyToId() {
            return replyToId;
        }

        public void setReplyToId(int replyToId) {
            this.replyToId = replyToId;
        }

        public int getDateCreated() {
            return dateCreated;
        }

        public void setDateCreated(int dateCreated) {
            this.dateCreated = dateCreated;
        }

        public String getUserAvatar() {
            return userAvatar;
        }

        public void setUserAvatar(String userAvatar) {
            this.userAvatar = userAvatar;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
