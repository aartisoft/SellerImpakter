package com.impakter.seller.object.seller;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatRespond {

    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("data")
    private Data data;
    @Expose
    @SerializedName("status")
    private String status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
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
        @SerializedName("summary30dayUnit")
        private int summary30dayUnit;
        @Expose
        @SerializedName("summary30daySales")
        private float summary30daySales;
        @Expose
        @SerializedName("summary15dayUnit")
        private int summary15dayUnit;
        @Expose
        @SerializedName("summary15daySales")
        private float summary15daySales;
        @Expose
        @SerializedName("summaryTodayUnit")
        private int summaryTodayUnit;
        @Expose
        @SerializedName("summaryTodaySales")
        private float summaryTodaySales;
        @Expose
        @SerializedName("balance")
        private float balance;
        @Expose
        @SerializedName("percentGrowth")
        private float percentGrowth;
        @Expose
        @SerializedName("totalOrderShiped")
        private int totalOrderShiped;
        @Expose
        @SerializedName("totalOrder")
        private int totalOrder;
        @Expose
        @SerializedName("secondLastPayment")
        private float secondLastPayment;
        @Expose
        @SerializedName("mostRecentPayment")
        private float mostRecentPayment;
        @Expose
        @SerializedName("totalRevenue")
        private float totalRevenue;

        public int getSummary30dayUnit() {
            return summary30dayUnit;
        }

        public void setSummary30dayUnit(int summary30dayUnit) {
            this.summary30dayUnit = summary30dayUnit;
        }

        public float getSummary30daySales() {
            return summary30daySales;
        }

        public void setSummary30daySales(float summary30daySales) {
            this.summary30daySales = summary30daySales;
        }

        public int getSummary15dayUnit() {
            return summary15dayUnit;
        }

        public void setSummary15dayUnit(int summary15dayUnit) {
            this.summary15dayUnit = summary15dayUnit;
        }

        public float getSummary15daySales() {
            return summary15daySales;
        }

        public void setSummary15daySales(float summary15daySales) {
            this.summary15daySales = summary15daySales;
        }

        public int getSummaryTodayUnit() {
            return summaryTodayUnit;
        }

        public void setSummaryTodayUnit(int summaryTodayUnit) {
            this.summaryTodayUnit = summaryTodayUnit;
        }

        public float getSummaryTodaySales() {
            return summaryTodaySales;
        }

        public void setSummaryTodaySales(float summaryTodaySales) {
            this.summaryTodaySales = summaryTodaySales;
        }

        public float getBalance() {
            return balance;
        }

        public void setBalance(float balance) {
            this.balance = balance;
        }

        public float getPercentGrowth() {
            return percentGrowth;
        }

        public void setPercentGrowth(float percentGrowth) {
            this.percentGrowth = percentGrowth;
        }

        public int getTotalOrderShiped() {
            return totalOrderShiped;
        }

        public void setTotalOrderShiped(int totalOrderShiped) {
            this.totalOrderShiped = totalOrderShiped;
        }

        public int getTotalOrder() {
            return totalOrder;
        }

        public void setTotalOrder(int totalOrder) {
            this.totalOrder = totalOrder;
        }

        public float getSecondLastPayment() {
            return secondLastPayment;
        }

        public void setSecondLastPayment(float secondLastPayment) {
            this.secondLastPayment = secondLastPayment;
        }

        public float getMostRecentPayment() {
            return mostRecentPayment;
        }

        public void setMostRecentPayment(float mostRecentPayment) {
            this.mostRecentPayment = mostRecentPayment;
        }

        public float getTotalRevenue() {
            return totalRevenue;
        }

        public void setTotalRevenue(float totalRevenue) {
            this.totalRevenue = totalRevenue;
        }
    }
}
