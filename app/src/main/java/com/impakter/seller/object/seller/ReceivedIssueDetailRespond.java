package com.impakter.seller.object.seller;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReceivedIssueDetailRespond {

    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("statusIssue")
    private int statusIssue;
    @Expose
    @SerializedName("data")
    private List<Data> data;
    @Expose
    @SerializedName("status")
    private String status;
    @SerializedName("userId")
    private int userId;
    @SerializedName("userName")
    private String userName;
    @SerializedName("userAvatar")
    private String userAvatar;
    @SerializedName("note")
    private String note;

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

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusIssue() {
        return statusIssue;
    }

    public void setStatusIssue(int statusIssue) {
        this.statusIssue = statusIssue;
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

    public static class Data  implements Parcelable{
        @SerializedName("rating")
        private float rating;
        @SerializedName("description")
        private String description;
        @Expose
        @SerializedName("price")
        private float price;
        @Expose
        @SerializedName("shipping")
        private float shipping;
        @Expose
        @SerializedName("totalPrice")
        private float totalPrice;
        @Expose
        @SerializedName("quantity")
        private int quantity;
        @Expose
        @SerializedName("options")
        private List<String> options;
        @Expose
        @SerializedName("sellerName")
        private String sellerName;
        @Expose
        @SerializedName("image")
        private String image;
        @Expose
        @SerializedName("productName")
        private String productName;
        @Expose
        @SerializedName("productId")
        private int productId;
        @Expose
        @SerializedName("orderItemId")
        private int orderItemId;

        protected Data(Parcel in) {
            rating = in.readFloat();
            description = in.readString();
            price = in.readFloat();
            shipping = in.readFloat();
            totalPrice = in.readFloat();
            quantity = in.readInt();
            options = in.createStringArrayList();
            sellerName = in.readString();
            image = in.readString();
            productName = in.readString();
            productId = in.readInt();
            orderItemId = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeFloat(rating);
            dest.writeString(description);
            dest.writeFloat(price);
            dest.writeFloat(shipping);
            dest.writeFloat(totalPrice);
            dest.writeInt(quantity);
            dest.writeStringList(options);
            dest.writeString(sellerName);
            dest.writeString(image);
            dest.writeString(productName);
            dest.writeInt(productId);
            dest.writeInt(orderItemId);
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

        public float getRating() {
            return rating;
        }

        public void setRating(float rating) {
            this.rating = rating;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public float getPrice() {
            return price;
        }

        public void setPrice(float price) {
            this.price = price;
        }

        public float getShipping() {
            return shipping;
        }

        public void setShipping(float shipping) {
            this.shipping = shipping;
        }

        public float getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(float totalPrice) {
            this.totalPrice = totalPrice;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public List<String> getOptions() {
            return options;
        }

        public void setOptions(List<String> options) {
            this.options = options;
        }

        public String getSellerName() {
            return sellerName;
        }

        public void setSellerName(String sellerName) {
            this.sellerName = sellerName;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public int getOrderItemId() {
            return orderItemId;
        }

        public void setOrderItemId(int orderItemId) {
            this.orderItemId = orderItemId;
        }
    }
}
