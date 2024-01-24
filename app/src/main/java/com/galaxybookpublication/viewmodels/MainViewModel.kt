package com.galaxybookpublication.viewmodels

import android.content.Context
import android.location.Address
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galaxybookpublication.models.data.response.LocationUpdateResponse
import com.galaxybookpublication.models.data.response.SignOutResponse
import com.galaxybookpublication.models.repo.GalaxyRetrofitClient
import com.galaxybookpublication.util.AppUtils
import com.galaxybookpublication.util.OnAddressListener
import com.galaxybookpublication.views.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.http.Field

class MainViewModel : ViewModel() {
    private var _logOutLiveData = MutableLiveData<SignOutResponse>()
    var logOutLiveData: LiveData<SignOutResponse> = _logOutLiveData

    private var _locationLiveData = MutableLiveData<LocationUpdateResponse>()
    var locationLiveData: LiveData<LocationUpdateResponse> = _locationLiveData

    private val apiInterface = GalaxyRetrofitClient.apiService

    fun makeLogout(authToken: String, latitude: String, longitude: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                //  _logInLiveData.value = ResponseStatus.Loading()
                val response = apiInterface.postSignOut(authToken, latitude, longitude)
                if (response.isSuccessful) {
                    _logOutLiveData.postValue(response.body())
                } else {
                  //  when (response.code()) {
                        _logOutLiveData.postValue(response.body())
                        //  401 -> _logInLiveDataError.postValue( "Unauthorized! Invalid phone number or password")
                    //}
                }
            } catch (exception: Exception) {
                Log.e("RESPONSE ERROR", exception.message.toString())
            }
        }
    }

    fun locationUpdate(context: Context, authToken: String, latitude: String, longitude: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                AppUtils.getCompleteReadableAddress(context,
                    MainActivity.wayLatitude,
                    MainActivity.wayLongitude,
                    object : OnAddressListener {
                        override fun result(address: Address?) {
                            var town = "random"
                            var area = "random"
                            if (address != null) {
                                if(address.locality != null && address.subLocality != null) {
                                    town = address.locality
                                    area = address.subLocality
                                } else if(address.adminArea != null && address.locality != null) {
                                    town = address.adminArea
                                    area = address.locality
                                } else if(address.adminArea != null) {
                                    town = address.adminArea
                                    area = address.adminArea
                                }
                            }
                            viewModelScope.launch {
                                val response = apiInterface.postCurrentLocation(
                                    latitude,
                                    longitude,
                                    town,
                                    area,
                                    authToken
                                )
                                if (response.isSuccessful) {
                                    _locationLiveData.postValue(response.body())
                                }
                            }
                        }
                    })
            } catch (exception: Exception) {
                Log.e("RESPONSE ERROR", exception.message.toString())
            }
        }
    }
}