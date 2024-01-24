package com.galaxybookpublication.models.data.response

import com.google.gson.annotations.SerializedName

data class CheckoutAppointmentResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: Data,
    @SerializedName("message") val message: String
) {
    data class Data(
        @SerializedName("uuid") val uuid: String,
        @SerializedName("type") val type: String,
        @SerializedName("appointment_date") val appointment_date: String,
        @SerializedName("attened_date") val attened_date: String,
        @SerializedName("checkin") val checkin: String,
        @SerializedName("checkout") val checkout: String,
        @SerializedName("total_amount") val total_amount: String,
        @SerializedName("remarks") val remarks: String,
        @SerializedName("visited_feedback") val visited_feedback: String,
        @SerializedName("client") val client: Client,
        @SerializedName("status") val status: String
    ) {
        data class Client(
            @SerializedName("uuid") val uuid: String,
            @SerializedName("name") val name: String,
            @SerializedName("email") val email: String,
            @SerializedName("phone_number") val phone_number: String,
            @SerializedName("address") val address: String,
            @SerializedName("emergency_contact") val emergency_contact: String,
            @SerializedName("district") val district: District,
        ) {
            data class District(
                @SerializedName("uuid") val uuid: String,
                @SerializedName("name") val name: String,
                @SerializedName("code") val code: String,
                @SerializedName("status") val status: String
            ) {
            }

        }

    }
}