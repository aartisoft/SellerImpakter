package com.impakter.seller.object.seller;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListCommentRespond {

    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("data")
    private List<Data> data;
    @Expose
    @SerializedName("totalComment")
    private int totalComment;
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

    public int getTotalComment() {
        return totalComment;
    }

    public void setTotalComment(int totalComment) {
        this.totalComment = totalComment;
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

    public static class Data implements Parcelable{
        @Expose
        @SerializedName("reply")
        private List<Reply> reply;
        @Expose
        @SerializedName("moreReply")
        private boolean moreReply;
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

        protected Data(Parcel in) {
            moreReply = in.readByte() != 0;
            date = in.readInt();
            content = in.readString();
            fullName = in.readString();
            avatar = in.readString();
            userId = in.readInt();
            id = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte((byte) (moreReply ? 1 : 0));
            dest.writeInt(date);
            dest.writeString(content);
            dest.writeString(fullName);
            dest.writeString(avatar);
            dest.writeInt(userId);
            dest.writeInt(id);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Data> CREATOR = new Creator<Data>() {
            @Override
            public Data createFromParcel(Parcel in) {
                return new Data(in);
            }

            @Override
            public Data[] newArray(int size) {
                return new Data[size];
            }
        };

        public List<Reply> getReply() {
            return reply;
        }

        public void setReply(List<Reply> reply) {
            this.reply = reply;
        }

        public boolean getMoreReply() {
            return moreReply;
        }

        public void setMoreReply(boolean moreReply) {
            this.moreReply = moreReply;
        }

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

    public static class Reply {
        @Expose
        @SerializedName("replyDate")
        private int replyDate;
        @Expose
        @SerializedName("replyContent")
        private String replyContent;
        @Expose
        @SerializedName("replyFullName")
        private String replyFullName;
        @Expose
        @SerializedName("replyAvatar")
        private String replyAvatar;
        @Expose
        @SerializedName("replyUserId")
        private int replyUserId;
        @Expose
        @SerializedName("replyId")
        private int replyId;

        public int getReplyDate() {
            return replyDate;
        }

        public void setReplyDate(int replyDate) {
            this.replyDate = replyDate;
        }

        public String getReplyContent() {
            return replyContent;
        }

        public void setReplyContent(String replyContent) {
            this.replyContent = replyContent;
        }

        public String getReplyFullName() {
            return replyFullName;
        }

        public void setReplyFullName(String replyFullName) {
            this.replyFullName = replyFullName;
        }

        public String getReplyAvatar() {
            return replyAvatar;
        }

        public void setReplyAvatar(String replyAvatar) {
            this.replyAvatar = replyAvatar;
        }

        public int getReplyUserId() {
            return replyUserId;
        }

        public void setReplyUserId(int replyUserId) {
            this.replyUserId = replyUserId;
        }

        public int getReplyId() {
            return replyId;
        }

        public void setReplyId(int replyId) {
            this.replyId = replyId;
        }
    }
}
