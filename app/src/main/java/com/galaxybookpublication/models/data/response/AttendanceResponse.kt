package com.galaxybookpublication.models.data.response

import com.google.gson.annotations.SerializedName

data class AttendanceResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
) {

}