package com.galaxybookpublication.models.data.response

import com.google.gson.annotations.SerializedName

data class SignInResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Data,
) {
    data class Data(
        @SerializedName("uuid") val uuid: String,
        @SerializedName("name") val name: String,
        @SerializedName("email") val email: String,
        @SerializedName("phone_number") val phoneNumber: String,
        @SerializedName("auth_token") val authToken: String
    ) {

    }
}