package com.galaxybookpublication.modelapi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClaimEditApiResponse {
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

    public class Data {

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


        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getClaim_for_label() {
            return claim_for_label;
        }

        public void setClaim_for_label(String claim_for_label) {
            this.claim_for_label = claim_for_label;
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
