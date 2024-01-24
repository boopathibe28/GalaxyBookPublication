package com.galaxybookpublication.models.interfaces

import com.galaxybookpublication.models.data.*
import com.galaxybookpublication.models.data.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface GalaxyApi {

    //Authentication
    @POST("api/v1/sign-in")
    @FormUrlEncoded
    suspend fun postSignIn(
        @Field("code") code: String,
        @Field("password") password: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String
    ): Response<ProfileResponse>

    @FormUrlEncoded
    @POST("api/v1/sign-out")
    suspend fun postSignOut(
        @Header("Authorization") token: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String
    ): Response<SignOutResponse>

    //Profile
    @GET("api/v1/profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<ProfileResponse>

    @PUT("api/v1/profile/update")
    @FormUrlEncoded
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone_number") phone_number: String,
        @Field("address") address: String,
        @Field("birth_date") birth_date: String,
        @Field("gender") gender: String,
        @Field("emergency_contact") emergency_contact: String,
    ): Response<ProfileResponse>

    @PUT("api/v1/profile/change-password")
    @FormUrlEncoded
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Field("old_password") name: String,
        @Field("password") email: String,
        @Field("password_confirmation") address: String
    ): Response<ProfileResponse>

    @POST("api/v1/profile/profile-picture")
    @Multipart
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part imageData: MultipartBody.Part
    ): Response<ProfileResponse>

    //Attendance
    @POST("api/v1/attendance/current-location")
    @FormUrlEncoded
    suspend fun postCurrentLocation(
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("town") town: String,
        @Field("area") area: String,
        @Header("Authorization") token: String,
    ): Response<LocationUpdateResponse>

   /* @POST("api/v1/attendance/mark-presense")
    @FormUrlEncoded
    suspend fun postMarkAttendance(
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("presense") presense: String,
        @Header("Authorization") token: String,
    ): Response<AttendanceResponse>*/

    @Multipart
    @POST("api/v1/attendance/mark-presense")
    suspend fun postMarkAttendance(
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("presense") presense: RequestBody,
        @Part imageData: MultipartBody.Part,
        @Header("Authorization") authToken: String
    ): Response<AttendanceResponse>


    @GET("api/v1/attendance/today-log")
    suspend fun getTodayLog(@Header("Authorization") token: String): Response<TodayLogResponse>

    @GET("api/v1/appointment")
    suspend fun getAppointment(
        @Query("type") type: String? = null,
        @Query("status") status: String? = null,
        @Query("page") page: String? = null,
        @Query("from_date") from_date: String? = null,
        @Query("to_date") to_date: String? = null,
        @Query("upcoming") upcoming: Boolean? = null,
        @Header("Authorization") authToken: String
    ): Response<AppointmentResponse>


    @FormUrlEncoded
    @POST("api/v2/client/{orderId}/payment")
    suspend fun postOrderPayment(
        @Path("orderId") orderId: String,
        @Field("appointment_id") appointmentId: String,
        @Field("amount") amount: String,
        @Field("paid_at") paidAt: String,
        @Field("mode") mode: String,
        @Field("reference_no") referenceNo: String,
        @Field("remarks") remarks: String,
        @Header("Authorization") authToken: String
    ): Response<PaymentAppointmentResponse>

    @Multipart
    @POST("api/v1/appointment/{appointmentId}/checkout")
    suspend fun postCheckoutAppointment(
        @Path("appointmentId") appointmentId: String,
        @Part("checkout") checkout: RequestBody,
        @Part("total_amount") totalAmount: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("visited_feedback") visitedFeedback: RequestBody,
        @Part imageData: MultipartBody.Part,
        @Header("Authorization") authToken: String
    ): Response<CheckoutAppointmentResponse>

    @PUT("api/v1/appointment/{appointmentId}")
    @FormUrlEncoded
    suspend fun appointmentCheckIn(
        @Path("appointmentId") appointmentId: String,
        @Field("status") status: String,
        @Field("attened_date") attened_date: String,
        @Field("checkin") checkin: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Header("Authorization") authToken: String
    ): Response<OrderCheckedInResponse>

    @GET("api/v1/order")
    suspend fun getClientAppointment(
        @Query("status") status: String,
        @Query("client_id") client_id: String,
        @Header("Authorization") authToken: String
    ): Response<ClientAppointmentResponse>

    @GET("api/v1/client")
    suspend fun getClient(): Response<ClientResponse>

    @GET("api/v1/order")
    suspend fun getOrderAppointment(@Header("Authorization") token: String): Response<AppointmentResponse>

    @GET("api/v1/payment")
    suspend fun getPaymentAppointmentList(@Header("Authorization") token: String): Response<AppointmentResponse>

    @GET("api/v1/specimen")
    suspend fun getSpecimenAppointmentList(@Header("Authorization") token: String): Response<AppointmentResponse>

}