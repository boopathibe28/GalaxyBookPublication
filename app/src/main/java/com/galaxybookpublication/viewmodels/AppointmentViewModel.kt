package com.galaxybookpublication.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galaxybookpublication.models.data.response.AppointmentResponse
import com.galaxybookpublication.models.data.response.OrderCheckedInResponse
import com.galaxybookpublication.models.repo.GalaxyRetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppointmentViewModel : ViewModel() {
    private var _appointmentLiveData = MutableLiveData<AppointmentResponse>()
    var appointmentLiveData: LiveData<AppointmentResponse> = _appointmentLiveData
    private var _appointmentCheckInLiveData = MutableLiveData<OrderCheckedInResponse>()
    var appointmentCheckInLiveData: LiveData<OrderCheckedInResponse> = _appointmentCheckInLiveData
    private var _appointmentType = MutableLiveData<String>()
    var appointmentType: LiveData<String> = _appointmentType
    var apiInterface = GalaxyRetrofitClient.apiService
    var lastUpdatedAppointment : AppointmentResponse.Datas.Data ?= null

    fun orderList(authToken: String) {
        _appointmentType.value = "Order"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiInterface.getOrderAppointment(authToken)
                if (response.isSuccessful) {
                    _appointmentLiveData.postValue(response.body())
                } else {
                    Log.e("ORDER APPOINTMENT", response.body().toString())
                }
            } catch (exception: Exception) {
                Log.e("ORDER APPOINTMENT EXC", "ORDER API Exception")
            }
        }
    }

    fun appointmentList(type: String,status: String,page: String,fromDate: String,toDate: String,authToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiInterface.getAppointment(type = type,status = status,page = page,from_date = fromDate,to_date = toDate,authToken = authToken)
                if (response.isSuccessful) {
                    _appointmentLiveData.postValue(response.body())
                } else {
                    Log.e("PAYMENT APPO ERROR", response.errorBody().toString())
                }
            } catch (exception: Exception) {
                Log.e("PAYMENT APPO EXCEP", exception.toString())
            }
        }
    }

    fun checkInAppointment(appointmentData: AppointmentResponse.Datas.Data, checkinTime: String, latitude: String, longitude: String, attendedDate: String, authToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiInterface.appointmentCheckIn(
                    appointmentData.uuid,
                    "Checkedin",
                    attendedDate,
                    checkinTime,
                    latitude,
                    longitude,
                    authToken
                )
                if (response.isSuccessful) {
                    lastUpdatedAppointment = appointmentData
                    val result = response.body()
                    if(_appointmentCheckInLiveData.value != result)
                        _appointmentCheckInLiveData.postValue(result!!)
                } else {
                    Log.e("VIEWMODEL Error: ", response.errorBody().toString())
                }

            } catch (exception: Exception) {
                Log.e("VIEWMODEL Exception: ", exception.message.toString())
            }
        }
    }

    fun specimenAppointmentList(authToken: String) {
        _appointmentType.value = "Specimen"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiInterface.getSpecimenAppointmentList(authToken)
                if (response.isSuccessful) {
                    _appointmentLiveData.postValue(response.body())
                } else {

                }
            } catch (exception: Exception) {

            }
        }
    }
}