package com.galaxybookpublication.modelapi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ClaimListApiResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private Data data;

    @SerializedName("message")
    @Expose
    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public class Meta {
        @SerializedName("total")
        @Expose
        private String total;
        @SerializedName("count")
        @Expose
        private String count;
        @SerializedName("per_page")
        @Expose
        private String per_page;
        @SerializedName("current_page")
        @Expose
        private String current_page;
        @SerializedName("total_pages")
        @Expose
        private String total_pages;

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getPer_page() {
            return per_page;
        }

        public void setPer_page(String per_page) {
            this.per_page = per_page;
        }

        public String getCurrent_page() {
            return current_page;
        }

        public void setCurrent_page(String current_page) {
            this.current_page = current_page;
        }

        public String getTotal_pages() {
            return total_pages;
        }

        public void setTotal_pages(String total_pages) {
            this.total_pages = total_pages;
        }
    }
    public class Data{
        @SerializedName("data")
        @Expose
        private List<Datum> data;
        @SerializedName("meta")
        @Expose
        private Meta meta;

        public Meta getMeta() {
            return meta;
        }

        public void setMeta(Meta meta) {
            this.meta = meta;
        }

        public List<Datum> getData() {
            return data;
        }

        public void setData(List<Datum> data) {
            this.data = data;
        }
    }
    public class Datum {

        @SerializedName("uuid")
        @Expose
        private String uuid;
        @SerializedName("claim_for_label")
        @Expose
        private String claim_for_label;
        @SerializedName("claim_for")
        @Expose
        private String claim_for;
        @SerializedName("amount")
        @Expose
        private String amount;
        @SerializedName("proof")
        @Expose
        private String proof;
        @SerializedName("status")
        @Expose
        private String status;

        public String getClaim_for_label() {
            return claim_for_label;
        }

        public void setClaim_for_label(String claim_for_label) {
            this.claim_for_label = claim_for_label;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getClaim_for() {
            return claim_for;
        }

        public void setClaim_for(String claim_for) {
            this.claim_for = claim_for;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getProof() {
            return proof;
        }

        public void setProof(String proof) {
            this.proof = proof;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
