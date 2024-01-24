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

class ChangePasswordViewModel : ViewModel() {
    private val _changePasswordLiveData = MutableLiveData<ProfileResponse>()
    private val _changePasswordDataError = MutableLiveData<String>()
    private val apiInterface = GalaxyRetrofitClient.apiService
    val changePasswordLiveData: LiveData<ProfileResponse> = _changePasswordLiveData
    val changePasswordLiveDataError: LiveData<String> = _changePasswordDataError

    fun changePassword(
        auth: String,
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response =
                    apiInterface.changePassword(auth, oldPassword, newPassword, confirmPassword)
                if (response.isSuccessful) {
                    _changePasswordLiveData.postValue(response.body())
                } else {
                    when (response.code()) {
                        401 -> _changePasswordDataError.postValue("Unauthorized! Wrong")
                    }
                }
            } catch (e: Exception) {
                Log.e("RESPONSE ERROR", e.message.toString())
            }
        }
    }
}