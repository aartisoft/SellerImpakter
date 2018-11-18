package com.impakter.seller.object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NotificationRespond {

    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("data")
    private List<Data> data;
    @Expose
    @SerializedName("count")
    private int count;
    @Expose
    @SerializedName("allPages")
    private int allPages;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getAllPages() {
        return allPages;
    }

    public void setAllPages(int allPages) {
        this.allPages = allPages;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Data {
        @Expose
        @SerializedName("type")
        private int type;
        @Expose
        @SerializedName("params")
        private Params params;
        @Expose
        @SerializedName("content")
        private String content;
        @Expose
        @SerializedName("id")
        private int id;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public Params getParams() {
            return params;
        }

        public void setParams(Params params) {
            this.params = params;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class Params {
        @SerializedName("commentUserName")
        private String commentUserName;
        @SerializedName("commentId")
        private int commentId;
        @SerializedName("content")
        private String content;
        @Expose
        @SerializedName("type")
        private String type;
        @Expose
        @SerializedName("time")
        private int time;
        @Expose
        @SerializedName("image")
        private String image;
        @Expose
        @SerializedName("id")
        private int id;

        public String getCommentUserName() {
            return commentUserName;
        }

        public void setCommentUserName(String commentUserName) {
            this.commentUserName = commentUserName;
        }

        public int getCommentId() {
            return commentId;
        }

        public void setCommentId(int commentId) {
            this.commentId = commentId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
