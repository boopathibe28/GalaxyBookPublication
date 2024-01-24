package com.galaxybookpublication.models.data.response

import com.google.gson.annotations.SerializedName

data class PaymentAppointmentResponse(
    @SerializedName("success") val success: String,
    @SerializedName("data") val data: Data,
    @SerializedName("message") val message: String
) {
    data class Data(
        @SerializedName("uuid") val uuid: String,
        @SerializedName("amount") val amount: String,
        @SerializedName("paid_at") val paid_at: String,
        @SerializedName("mode") val mode: String,
        @SerializedName("reference_no") val reference_no: String,
        @SerializedName("remarks") val remarks: String,
        @SerializedName("status") val status: String,
    ) {

    }
}
