package com.galaxybookpublication.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galaxybookpublication.models.data.response.AppointmentResponse
import com.galaxybookpublication.models.data.response.AttendanceResponse
import com.galaxybookpublication.models.data.response.TodayLogResponse
import com.galaxybookpublication.models.repo.GalaxyRetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class DashboardViewModel : ViewModel() {
    private val _dashboardLiveData = MutableLiveData<AttendanceResponse>()
    private val _dashboardLiveDataError = MutableLiveData<String>()
    val dashboardLiveData: LiveData<AttendanceResponse> = _dashboardLiveData
    val dashboardLiveDataError: LiveData<String> = _dashboardLiveDataError
    private val _dashboardTodayAttendanceLiveData = MutableLiveData<TodayLogResponse>()
    val dashboardTodayAttendanceLiveData: LiveData<TodayLogResponse> = _dashboardTodayAttendanceLiveData
    private val _dashboardAppointmentsLiveData = MutableLiveData<AppointmentResponse>()
    val dashboardAppointmentsLiveData: LiveData<AppointmentResponse> = _dashboardAppointmentsLiveData
    private val apiInterface = GalaxyRetrofitClient.apiService

    fun markAttendance(latitude: String, longitude: String, presense: String, checkedInOut_proof: File, authToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            /*try {
                Log.e("MARK ATTENDANCE", "Entered coroutine")
                val response =
                    apiInterface.postMarkAttendance(latitude, longitude, presense,checkedInOut_proof, authToken)
                Log.e("MARK ATTENDANCE", response.isSuccessful.toString())
                if (response.isSuccessful) {
                    _dashboardLiveData.postValue(response.body())
                } else {
                    _dashboardLiveDataError.postValue("Error")
                   // when (response.code()) {
                        //401 -> _logInLiveDataError.postValue( "Unauthorized! Invalid phone number or password")
                  //  }
                }
            } catch (e: Exception) {
                Log.e("RESPONSE ERROR", e.message.toString())
            }*/




            try {
                var multiPartBody: MultipartBody.Part
                if (presense.equals("Checkin")) {
                    val reqFile = RequestBody.create("image/*".toMediaTypeOrNull(), checkedInOut_proof)
                     multiPartBody = MultipartBody.Part.createFormData("checkedin_proof",
                        checkedInOut_proof.name, reqFile)
                }
                else{
                    val reqFile = RequestBody.create("image/*".toMediaTypeOrNull(), checkedInOut_proof)
                     multiPartBody = MultipartBody.Part.createFormData("checkedout_proof",
                         checkedInOut_proof.name, reqFile)
                }
                val latitude_: RequestBody = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    latitude
                )
                val longitude_: RequestBody = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    longitude
                )
                val presense_: RequestBody = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    presense
                )

                val response = apiInterface.postMarkAttendance(
                    latitude_,
                    longitude_,
                    presense_,
                    multiPartBody,
                    authToken
                )
                if (response.isSuccessful) {
                    _dashboardLiveData.postValue(response.body())
                } else {
                    _dashboardLiveDataError.postValue(response.errorBody().toString())
                     when (response.code()) {
                    //401 -> _logInLiveDataError.postValue( "Unauthorized! Invalid phone number or password")
                      }
                }
            }
            catch (exception: Exception) {
                Log.e("CHECKOUT EXCEPTION", exception.message.toString())
            }

        }
    }

    fun todayLog(authToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiInterface.getTodayLog(authToken)
                if (response.isSuccessful) {
                    _dashboardTodayAttendanceLiveData.postValue(response.body())
                } else {
                    Log.e("RESPONSE ERROR", response.message())
                }
            } catch (e: Exception) {
                Log.e("RESPONSE EXCEPTION", e.message.toString())
            }
        }
    }

    fun totalPendingAppointments(authToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiInterface.getAppointment(upcoming = true, authToken = authToken)
                if (response.isSuccessful) {
                    _dashboardAppointmentsLiveData.postValue(response.body())
                } else {
                    Log.e("Dashboard Error", response.message())
                }
            } catch (e: Exception) {
                Log.e("Dashboard Exception", e.message.toString())
            }
        }
    }
}