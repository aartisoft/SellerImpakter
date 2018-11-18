package com.impakter.seller.object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MessageRespond {

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
        @SerializedName("content")
        private String content;
        @Expose
        @SerializedName("time")
        private String time;
        @Expose
        @SerializedName("receiverId")
        private int receiverId;
        @Expose
        @SerializedName("senderId")
        private int senderId;
        @Expose
        @SerializedName("messId")
        private int messId;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(int receiverId) {
            this.receiverId = receiverId;
        }

        public int getSenderId() {
            return senderId;
        }

        public void setSenderId(int senderId) {
            this.senderId = senderId;
        }

        public int getMessId() {
            return messId;
        }

        public void setMessId(int messId) {
            this.messId = messId;
        }
    }
}
