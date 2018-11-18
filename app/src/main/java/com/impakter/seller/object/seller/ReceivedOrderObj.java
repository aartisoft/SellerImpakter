package com.impakter.seller.object.seller;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReceivedOrderObj {

    @Expose
    @SerializedName("statusAction")
    private List<StatusAction> statusAction;
    @Expose
    @SerializedName("isForward")
    private boolean isForward;
    @Expose
    @SerializedName("status")
    private int status;
    @Expose
    @SerializedName("arrPhoto")
    private List<String> arrPhoto;
    @Expose
    @SerializedName("orderDate")
    private int orderDate;
    @Expose
    @SerializedName("quantity")
    private int quantity;
    @Expose
    @SerializedName("items")
    private String items;
    @Expose
    @SerializedName("userId")
    private int userId;
    @Expose
    @SerializedName("orderId")
    private int orderId;

    public List<StatusAction> getStatusAction() {
        return statusAction;
    }

    public void setStatusAction(List<StatusAction> statusAction) {
        this.statusAction = statusAction;
    }

    public boolean getIsForward() {
        return isForward;
    }

    public void setIsForward(boolean isForward) {
        this.isForward = isForward;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<String> getArrPhoto() {
        return arrPhoto;
    }

    public void setArrPhoto(List<String> arrPhoto) {
        this.arrPhoto = arrPhoto;
    }

    public int getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(int orderDate) {
        this.orderDate = orderDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
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
}
