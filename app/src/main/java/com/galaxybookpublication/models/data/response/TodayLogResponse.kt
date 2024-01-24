package com.galaxybookpublication.models.data.response

import com.google.gson.annotations.SerializedName

data class TodayLogResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<Data>,
    @SerializedName("message") val message: String,
) {
    data class Data(
        @SerializedName("uuid") val uuid: String,
        @SerializedName("checkedin_at") val checkedin_at: String,
        @SerializedName("checkedin_latitude") val checkedin_latitude: String,
        @SerializedName("checkedin_longitude") val checkedin_longitude: String,
        @SerializedName("checkedout_at") val checkedout_at: String,
        @SerializedName("checkedout_latitude") val checkedout_latitude: String,
        @SerializedName("checkedout_longitude") val checkedout_longitude: String
    ) {

    }
}
