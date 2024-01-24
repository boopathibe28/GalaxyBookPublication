package com.galaxybookpublication.modelapi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PaymentOrderListApiResponse {
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
        @SerializedName("payments")
        @Expose
        private Payments payments;
        @SerializedName("summary")
        @Expose
        private Summary summary;

        public Payments getPayments() {
            return payments;
        }

        public void setPayments(Payments payments) {
            this.payments = payments;
        }

        public Summary getSummary() {
            return summary;
        }

        public void setSummary(Summary summary) {
            this.summary = summary;
        }
    }

    public class Datum {
        @SerializedName("uuid")
        @Expose
        private String uuid;
        @SerializedName("amount")
        @Expose
        private String amount;
        @SerializedName("employee")
        @Expose
        private String employee;
        @SerializedName("paid_at")
        @Expose
        private String paid_at;
        @SerializedName("mode")
        @Expose
        private String mode;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("reference_no")
        @Expose
        private String reference_no;
        @SerializedName("remarks")
        @Expose
        private String remarks;
        @SerializedName("receipt")
        @Expose
        private String receipt;
        @SerializedName("status")
        @Expose
        private String status;

        public String getEmployee() {
            return employee;
        }

        public void setEmployee(String employee) {
            this.employee = employee;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getPaid_at() {
            return paid_at;
        }

        public void setPaid_at(String paid_at) {
            this.paid_at = paid_at;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getReference_no() {
            return reference_no;
        }

        public void setReference_no(String reference_no) {
            this.reference_no = reference_no;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getReceipt() {
            return receipt;
        }

        public void setReceipt(String receipt) {
            this.receipt = receipt;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
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


    public class Payments {
        @SerializedName("data")
        @Expose
        private List<Datum> data;
        @SerializedName("meta")
        @Expose
        private Meta meta;

        public List<Datum> getData() {
            return data;
        }

        public void setData(List<Datum> data) {
            this.data = data;
        }

        public Meta getMeta() {
            return meta;
        }

        public void setMeta(Meta meta) {
            this.meta = meta;
        }
    }

    public class Summary {
        @SerializedName("total_amount")
        @Expose
        private String total_amount;
        @SerializedName("received_amount")
        @Expose
        private String received_amount;
        @SerializedName("pending_amount")
        @Expose
        private String pending_amount;

        public String getTotal_amount() {
            return total_amount;
        }

        public void setTotal_amount(String total_amount) {
            this.total_amount = total_amount;
        }

        public String getReceived_amount() {
            return received_amount;
        }

        public void setReceived_amount(String received_amount) {
            this.received_amount = received_amount;
        }

        public String getPending_amount() {
            return pending_amount;
        }

        public void setPending_amount(String pending_amount) {
            this.pending_amount = pending_amount;
        }
    }
}
