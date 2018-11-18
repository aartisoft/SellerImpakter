package com.impakter.seller.object.seller;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReceivedOrderDetailRespond {


    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("statusAction")
    private List<StatusAction> statusAction;
    @SerializedName("isForward")
    private boolean isForward;
    @Expose
    @SerializedName("statusOrder")
    private int statusOrder;
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

    public List<StatusAction> getStatusAction() {
        return statusAction;
    }

    public boolean isForward() {
        return isForward;
    }

    public void setForward(boolean forward) {
        isForward = forward;
    }

    public void setStatusAction(List<StatusAction> statusAction) {
        this.statusAction = statusAction;
    }

    public int getStatusOrder() {
        return statusOrder;
    }

    public void setStatusOrder(int statusOrder) {
        this.statusOrder = statusOrder;
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

    public static class StatusAction {
        @Expose
        @SerializedName("statusShiped")
        private boolean statusShiped;
        @Expose
        @SerializedName("statusToShip")
        private boolean statusToShip;
        @Expose
        @SerializedName("statusProcessed")
        private boolean statusProcessed;

        public boolean getStatusShiped() {
            return statusShiped;
        }

        public void setStatusShiped(boolean statusShiped) {
            this.statusShiped = statusShiped;
        }

        public boolean getStatusToShip() {
            return statusToShip;
        }

        public void setStatusToShip(boolean statusToShip) {
            this.statusToShip = statusToShip;
        }

        public boolean getStatusProcessed() {
            return statusProcessed;
        }

        public void setStatusProcessed(boolean statusProcessed) {
            this.statusProcessed = statusProcessed;
        }
    }

    public static class Data implements Parcelable {
        @SerializedName("rating")
        private float rating;
        @SerializedName("description")
        private String description;
        @Expose
        @SerializedName("totalItemPrice")
        private float totalItemPrice;
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
            totalItemPrice = in.readFloat();
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
            dest.writeFloat(totalItemPrice);
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

        public float getTotalItemPrice() {
            return totalItemPrice;
        }

        public void setTotalItemPrice(float totalItemPrice) {
            this.totalItemPrice = totalItemPrice;
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
