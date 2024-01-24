package com.galaxybookpublication.models.data.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ClientAppointmentResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: List<Data>,
    @SerializedName("message")
    val message: String
) {
    data class Data(
        @SerializedName("uuid")
        val uuid: String,
        @SerializedName("order_number")
        val order_number: String,
        @SerializedName("product")
        val product: String,
        @SerializedName("total_amount")
        val total_amount: String,
        @SerializedName("received_amount")
        val received_amount: String,
        @SerializedName("pending_amount")
        val pending_amount: String,
        @SerializedName("order_summary")
        val order_summary: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("payment_status")
        val payment_status: String
    ) : Serializable {

    }
}