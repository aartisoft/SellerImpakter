package com.impakter.seller.object.seller;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForwardOrderRespond {
    @SerializedName("email")
    private String email;
    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("statusAction")
    private List<StatusAction> statusAction;
    @Expose
    @SerializedName("status")
    private String status;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<StatusAction> getStatusAction() {
        return statusAction;
    }

    public void setStatusAction(List<StatusAction> statusAction) {
        this.statusAction = statusAction;
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
}
