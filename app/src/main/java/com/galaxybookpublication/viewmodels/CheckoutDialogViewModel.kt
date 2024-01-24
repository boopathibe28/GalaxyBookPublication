package com.galaxybookpublication.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galaxybookpublication.models.data.response.CheckoutAppointmentResponse
import com.galaxybookpublication.models.repo.GalaxyRetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class CheckoutDialogViewModel : ViewModel() {
    private var _checkoutLiveData = MutableLiveData<CheckoutAppointmentResponse>()
    var checkoutLiveData: LiveData<CheckoutAppointmentResponse> = _checkoutLiveData
    private val apiInterface = GalaxyRetrofitClient.apiService

    fun checkout(
        appointmentId: String,
        checkoutTime: String,
        total_Amt: String,
        latitude: String,
        longitude: String,
        visitedFeedback: String,
        visitedEvidence: File,
        authToken: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val reqFile = RequestBody.create("image/*".toMediaTypeOrNull(), visitedEvidence)
                val multiPartBody = MultipartBody.Part.createFormData(
                    "visited_evidence",
                    visitedEvidence.name, reqFile
                )
                val checkoutTime: RequestBody = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    checkoutTime
                )
                val total_Amt: RequestBody = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    total_Amt
                )
                val latitude: RequestBody = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    latitude
                )
                val longitude: RequestBody = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    longitude
                )
                val visitedFeedback: RequestBody = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    visitedFeedback
                )
                val response = apiInterface.postCheckoutAppointment(
                    appointmentId,
                    checkoutTime,
                    total_Amt,
                    latitude,
                    longitude,
                    visitedFeedback,
                    multiPartBody,
                    authToken
                )
                if (response.isSuccessful) {
                    _checkoutLiveData.postValue(response.body())
                } else {
                    Log.e("CHECKOUT ERROR", response.errorBody().toString())
                }
            } catch (exception: Exception) {
                Log.e("CHECKOUT EXCEPTION", exception.message.toString())
            }
        }
    }
}