package com.galaxybookpublication.models.data.response

import com.google.gson.annotations.SerializedName

data class LocationUpdateResponse(
    @SerializedName("success")
    val success: String,
    @SerializedName("data")
    val data: List<Data>,
    @SerializedName("message")
    val message: String
) {

    data class Data(
        @SerializedName("message")
        val msg: String
    ) {

    }
}