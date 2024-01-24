package com.galaxybookpublication.models.data.response

import com.google.gson.annotations.SerializedName

data class ClientResponse(
    @SerializedName("success") val success: String,
    @SerializedName("data") val data: List<Data>,
    @SerializedName("message") val message: String
) {
    data class Data(
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
            @SerializedName("status") val status: String,
        )
        {

        }
    }
}
