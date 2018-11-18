package com.impakter.seller.object.seller;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReceivedIssueRespond {

    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("data")
    private List<Data> data;
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
        @SerializedName("status")
        private int status;
        @Expose
        @SerializedName("note")
        private String note;
        @Expose
        @SerializedName("type")
        private int type;
        @Expose
        @SerializedName("images")
        private List<String> images;
        @Expose
        @SerializedName("orderDate")
        private long orderDate;
        @Expose
        @SerializedName("userId")
        private int userId;
        @Expose
        @SerializedName("orderReturnId")
        private int orderReturnId;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        public long getOrderDate() {
            return orderDate;
        }

        public void setOrderDate(long orderDate) {
            this.orderDate = orderDate;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getOrderReturnId() {
            return orderReturnId;
        }

        public void setOrderReturnId(int orderReturnId) {
            this.orderReturnId = orderReturnId;
        }
    }
}
