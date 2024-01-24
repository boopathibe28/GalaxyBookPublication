package com.galaxybookpublication.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galaxybookpublication.models.data.response.ProfileResponse
import com.galaxybookpublication.models.repo.GalaxyRetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.File

class ProfileViewModel : ViewModel() {
    private var _profileLiveData = MutableLiveData<ProfileResponse>()
    var profileLiveData: LiveData<ProfileResponse> = _profileLiveData
    private var _profileUpdateErrorLiveData = MutableLiveData<String>()
    var profileUpdateErrorLiveData: LiveData<String> = _profileUpdateErrorLiveData
    private var _profileUpdateLiveData = MutableLiveData<ProfileResponse>()
    var profileUpdateLiveData: LiveData<ProfileResponse> = _profileUpdateLiveData
    var apiInterface = GalaxyRetrofitClient.apiService

    //Get Profile details
    fun getProfile(authToken: String) {
        viewModelScope.launch {
            val response = apiInterface.getProfile("Bearer $authToken")
            if (response.isSuccessful) {
                _profileLiveData.postValue(response.body())
            }
        }
    }

    // Update Profile details
    fun updateProfile(
        authToken: String,
        name: String,
        email: String,
        phone_number: String,
        address: String,
        birth_date: String,
        gender: String,
        emergency_contact: String
    ) {
        viewModelScope.launch {
            val response = apiInterface.updateProfile(
                authToken,
                name,
                email,
                phone_number,
                address,
                birth_date,
                gender,
                emergency_contact
            )
            if (response.isSuccessful) {
                _profileUpdateLiveData.postValue(response.body())
            } else {
                _profileUpdateErrorLiveData.postValue("Error while updating")
            }
        }
    }

    //fun uploadImage()
    fun uploadImage(authToken: String, file: File) {
        viewModelScope.launch {
            val reqFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val multiPartBody = MultipartBody.Part.createFormData(
                "profile_img",
                file.name, reqFile
            )
            val response = apiInterface.uploadImage(authToken, multiPartBody)
            if (response.isSuccessful) {
                _profileLiveData.postValue(response.body())
            }
        }
    }
}