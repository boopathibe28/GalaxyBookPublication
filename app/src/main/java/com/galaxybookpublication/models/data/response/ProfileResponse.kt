package com.galaxybookpublication.models.data.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: Data,
    @SerializedName("message") val message: String
) {
    data class Data(
        @SerializedName("uuid") val uuid: String,
        @SerializedName("name") val name: String,
        @SerializedName("email") val email: String,
        @SerializedName("email_verified_at") val emailVerifiedAt: String,
        @SerializedName("phone_number") val phoneNumber: String,
        @SerializedName("address") val address: String,
        @SerializedName("gender") val gender: String,
        @SerializedName("birth_date") val birthDate: String,
        @SerializedName("emergency_contact") val emergencyContact: String,
        @SerializedName("profile_img") val profileImg: String,
        @SerializedName("aadhar_number") val aadharNumber: String,
        @SerializedName("aadhar_image") val aadharImage: String,
        @SerializedName("current_location") val currentLocation: CurrentLocation,
        @SerializedName("auth_token") val authToken: String
    ) {
        data class CurrentLocation(
            @SerializedName("latitude") val latitude: String,
            @SerializedName("longitude") val longitude: String
        )
    }
}
