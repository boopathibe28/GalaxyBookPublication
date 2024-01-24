package com.galaxybookpublication.models.data.response

import com.galaxybookpublication.modelapi.AppointmentListApiResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AppointmentResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val datas: Datas,
    @SerializedName("message") val message: String
) {
    data class Datas(
        @SerializedName("data") val data: List<Data>,
        @SerializedName("meta") val meta: Meta
    ) : Serializable {
        data class Data(
            @SerializedName("uuid") val uuid: String,
            @SerializedName("type") val type: String,
            @SerializedName("appointment_date") val appointment_date: String,
            @SerializedName("attened_date") val attened_date: String,
            @SerializedName("total_amount") val total_amount: String,
            @SerializedName("checkin") val checkin: String,
            @SerializedName("checkout") val checkout: String,
            @SerializedName("remarks") val remarks: String,
            @SerializedName("visited_feedback") val visited_feedback: String,
            @SerializedName("client") val client: Client,
            @SerializedName("status") val status: String
        ) : Serializable {
            data class Client(
                @SerializedName("uuid") val uuid: String,
                @SerializedName("name") val name: String,
                @SerializedName("email") val email: String,
                @SerializedName("phone_number") val phone_number: String,
                @SerializedName("address") val address: String,
                @SerializedName("emergency_contact") val emergency_contact: String,
                @SerializedName("district") val district: District
            ) : Serializable {
                data class District(
                    @SerializedName("uuid") val uuid: String,
                    @SerializedName("name") val name: String,
                    @SerializedName("code") val code: String,
                    @SerializedName("status") val status: String
                ) : Serializable {

                }
            }
        }
    }
    data class Meta(
        @SerializedName("total") val total: Int,
        @SerializedName("count") val count: Int,
        @SerializedName("per_page") val per_page: Int,
        @SerializedName("current_page") val current_page: Int,
        @SerializedName("total_pages") val total_pages: Int
    )
}
