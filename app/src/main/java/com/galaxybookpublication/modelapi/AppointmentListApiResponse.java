package com.galaxybookpublication.modelapi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppointmentListApiResponse {
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

    public class Client {
        @SerializedName("uuid")
        @Expose
        private String uuid;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("phone_number")
        @Expose
        private String phone_number;
        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("emergency_contact")
        @Expose
        private String emergency_contact;
        @SerializedName("district")
        @Expose
        private District district;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone_number() {
            return phone_number;
        }

        public void setPhone_number(String phone_number) {
            this.phone_number = phone_number;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getEmergency_contact() {
            return emergency_contact;
        }

        public void setEmergency_contact(String emergency_contact) {
            this.emergency_contact = emergency_contact;
        }

        public District getDistrict() {
            return district;
        }

        public void setDistrict(District district) {
            this.district = district;
        }
    }


    public class Data {
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

    public class Datum {
        @SerializedName("uuid")
        @Expose
        private String uuid;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("appointment_date")
        @Expose
        private String appointment_date;
        @SerializedName("attened_date")
        @Expose
        private String attened_date;
        @SerializedName("checkin")
        @Expose
        private String checkin;
        @SerializedName("checkout")
        @Expose
        private String checkout;
        @SerializedName("total_amount")
        @Expose
        private String total_amount;
        @SerializedName("remarks")
        @Expose
        private String remarks;
        @SerializedName("visited_feedback")
        @Expose
        private String visited_feedback;
        @SerializedName("visited_evidence")
        @Expose
        private String visited_evidence;
        @SerializedName("client")
        @Expose
        private Client client;
        @SerializedName("status")
        @Expose
        private String status;


        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAppointment_date() {
            return appointment_date;
        }

        public void setAppointment_date(String appointment_date) {
            this.appointment_date = appointment_date;
        }

        public String getAttened_date() {
            return attened_date;
        }

        public void setAttened_date(String attened_date) {
            this.attened_date = attened_date;
        }

        public String getCheckin() {
            return checkin;
        }

        public void setCheckin(String checkin) {
            this.checkin = checkin;
        }

        public String getCheckout() {
            return checkout;
        }

        public void setCheckout(String checkout) {
            this.checkout = checkout;
        }

        public String getTotal_amount() {
            return total_amount;
        }

        public void setTotal_amount(String total_amount) {
            this.total_amount = total_amount;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getVisited_feedback() {
            return visited_feedback;
        }

        public void setVisited_feedback(String visited_feedback) {
            this.visited_feedback = visited_feedback;
        }

        public String getVisited_evidence() {
            return visited_evidence;
        }

        public void setVisited_evidence(String visited_evidence) {
            this.visited_evidence = visited_evidence;
        }

        public Client getClient() {
            return client;
        }

        public void setClient(Client client) {
            this.client = client;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public class District {
        @SerializedName("uuid")
        @Expose
        private String uuid;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("code")
        @Expose
        private String code;
        @SerializedName("status")
        @Expose
        private String status;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
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
        private Integer total;
        @SerializedName("count")
        @Expose
        private Integer count;
        @SerializedName("per_page")
        @Expose
        private Integer per_page;
        @SerializedName("current_page")
        @Expose
        private Integer current_page;
        @SerializedName("total_pages")
        @Expose
        private Integer total_pages;

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Integer getPer_page() {
            return per_page;
        }

        public void setPer_page(Integer per_page) {
            this.per_page = per_page;
        }

        public Integer getCurrent_page() {
            return current_page;
        }

        public void setCurrent_page(Integer current_page) {
            this.current_page = current_page;
        }

        public Integer getTotal_pages() {
            return total_pages;
        }

        public void setTotal_pages(Integer total_pages) {
            this.total_pages = total_pages;
        }
    }
}
