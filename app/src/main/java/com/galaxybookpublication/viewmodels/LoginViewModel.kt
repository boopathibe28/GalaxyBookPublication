package com.galaxybookpublication.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galaxybookpublication.models.data.response.ProfileResponse
import com.galaxybookpublication.models.repo.GalaxyRetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    //variable declaration
    private val apiInterface = GalaxyRetrofitClient.apiService
    private val _logInLiveData = MutableLiveData<ProfileResponse>()
    private val _logInLiveDataError = MutableLiveData<String>()
    val logInLiveData: LiveData<ProfileResponse> = _logInLiveData
    val logInLiveDataError: LiveData<String> = _logInLiveDataError

    fun loginCall(userName: String, password: String, latitude: String, longitude: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiInterface.postSignIn(userName, password,latitude,longitude)
                if (response.isSuccessful) {
                    _logInLiveData.postValue(response.body())
                } else {
                    _logInLiveDataError.postValue("Unauthorized! Invalid phone number or password")
                }
            } catch (e: Exception) {
                _logInLiveDataError.postValue("Unauthorized! Invalid phone number or password")
                Log.e("RESPONSE ERROR", e.message.toString())
            }
        }
    }
}