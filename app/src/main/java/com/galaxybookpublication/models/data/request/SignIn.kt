package com.galaxybookpublication.models.data.request

import com.google.gson.annotations.SerializedName

data class SignIn(
    @SerializedName("phone_number") val phone_number: String,
    @SerializedName("password") val password:String) {
}