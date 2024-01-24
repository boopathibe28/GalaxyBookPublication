package com.galaxybookpublication.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galaxybookpublication.models.data.response.ClientResponse
import com.galaxybookpublication.models.repo.GalaxyRetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CollectionViewModel : ViewModel() {
    private val _clientLiveData = MutableLiveData<ClientResponse>()
    val clientLiveData: LiveData<ClientResponse> = _clientLiveData
    private val apiInterface = GalaxyRetrofitClient.apiService

    fun getClient(authToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiInterface.getClient()
                if (response.isSuccessful) {
                    _clientLiveData.postValue(response.body())
                } else {
                    //
                }
            } catch (exception: Exception) {
//
            }
        }
    }

    fun createPaymentAppointment(
        authToken: String,
        order_id: String,
        appointment_date: String,
        total_amount: String,
        remarks: String
    ) {

    }

    fun createSpecimenAppointment(
        authToken: String,
        client_id: String,
        appointment_date: String,
        remarks: String
    ) {

    }

    fun createOrderAppointment(
        authToken: String,
        order_id: String,
        appointment_date: String,
        total_amount: String,
        remarks: String
    ) {

    }
}