package com.galaxybookpublication.views

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.galaxybookpublication.R
import com.galaxybookpublication.databinding.ActivityLoginBinding
import com.galaxybookpublication.models.repo.SharedPreferenceHelper
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.address
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.authToken
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.dob
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.emaiId
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.emergencyContact
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.gender
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.phoneNo
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.profileImage
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.userId
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.userName
import com.galaxybookpublication.util.AppUtils
import com.galaxybookpublication.viewmodels.LoginViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    //variable declaration
    private lateinit var bindingLogin: ActivityLoginBinding
    private val loginViewModel by lazy { ViewModelProvider(this)[LoginViewModel::class.java] }
    private lateinit var preference: SharedPreferences
    private var locationRequest: LocationRequest? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var isValidDetailsForLogin = false

    companion object {
        var PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        }
        var wayLatitude: String = ""
        var wayLongitude: String = ""
    }

    private fun checkPermissionRequired() {
        if (!hasPermissions(this, PERMISSIONS)) {
            permReqLauncher.launch(PERMISSIONS)
        }
    }

    private fun hasPermissions(context: Context, permission: Array<String>): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager() && permission.all {
                ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        } else {
            permission.all {
                ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            bindingLogin.progressBar.visibility = View.GONE
            return
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            mFusedLocationClient.requestLocationUpdates(
                locationRequest!!,
                locationCallback,
                Looper.getMainLooper()
            )
        }

        task.addOnFailureListener { exception ->
            bindingLogin.progressBar.visibility = View.GONE
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        this@LoginActivity,
                        100
                    )
                } catch (sendEx: Exception) {
                    // Ignore the error.
                    sendEx.printStackTrace()
                }
            }
        }
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList: List<Location> = locationResult.locations
            if (locationList.isNotEmpty()) {
                val location: Location = locationList[0]
                wayLatitude = location.latitude.toString()
                wayLongitude = location.longitude.toString()
                mFusedLocationClient.removeLocationUpdates(this)
                if(isValidDetailsForLogin) {
                    login()
                }
            }
        }
    }

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            val granted = permissions.entries.all {
                it.value
            }
            if (!granted) {
                showPermissionDialog()
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                && !Environment.isExternalStorageManager()
            ) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                startActivity(intent)
            } else {
                fetchLocation()
            }
        }

    private fun showPermissionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permission required!")
        builder.setMessage("App Needs Camera, Storage and Location Permission to continue!, tap \"Allow all time in the next screen\"")
        builder.setPositiveButton("Grant") { dialog, which ->
            dialog.cancel()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val uri = Uri.fromParts("package", this.packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
            finish()
        }
        builder.setCancelable(false)
        builder.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1 * 1000).apply {
            setMinUpdateDistanceMeters(0.0f)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()
        //initialize variables
        bindingLogin = DataBindingUtil.setContentView(this, R.layout.activity_login)
        preference = SharedPreferenceHelper.customPreference(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // clickListeners
        bindingLogin.btnLogin.setOnClickListener {
            setLoginButtonClickListener()
        }

        //liveData Observer LoginError
        loginViewModel.logInLiveDataError.observe(this, Observer {
            bindingLogin.progressBar.visibility = View.GONE
            if (it != null) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })

        //liveData Observer LoginSuccess
        loginViewModel.logInLiveData.observe(this) {
            bindingLogin.progressBar.visibility = View.GONE
            if (it != null) {
                preference.userId = it.data.uuid
                preference.userName = it.data.name
                preference.emaiId = it.data.email
                preference.phoneNo = it.data.phoneNumber
                preference.authToken = it.data.authToken
                preference.address = it.data.address
                preference.dob = it.data.birthDate
                preference.gender = it.data.gender
                preference.emergencyContact = it.data.emergencyContact
                preference.profileImage = it.data.profileImg
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Unauthorized! Invalid EmployeeId or Password",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkPermissionRequired()
    }

    private fun setLoginButtonClickListener() {
        validateDataAndCallLoginApi()
    }

    private fun validateDataAndCallLoginApi() {
        hideSoftKeyboard()
        if (bindingLogin.tvUserName.text.toString().trim().isEmpty()) {
            Toast.makeText(applicationContext, "Enter the UserName", Toast.LENGTH_SHORT).show()
        } else if (bindingLogin.tvPassword.text.toString().trim().isEmpty()) {
            Toast.makeText(applicationContext, "Enter the Password", Toast.LENGTH_SHORT).show()
        } else if ((wayLatitude == "0.0" && wayLongitude == "0.0") || (wayLatitude == "" && wayLongitude == "")) {
            isValidDetailsForLogin = true
            if (hasPermissions(this, PERMISSIONS)) {
                bindingLogin.progressBar.visibility = View.VISIBLE
                fetchLocation()
            }
        } else {
            login()
        }
    }

    private fun login() {
        Log.e("LATITUDE LOGIN", wayLatitude)
        Log.e("LOGITUDE LOGIN", wayLongitude)
        loginViewModel.loginCall(
            bindingLogin.tvUserName.text.toString(),
            bindingLogin.tvPassword.text.toString(),
            wayLatitude,
            wayLongitude
        )
    }

    private fun hideSoftKeyboard() {
        //hide keyboard
        val view: View? = this.currentFocus
        if (view != null) {
            val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100 && resultCode == RESULT_OK) {
            bindingLogin.progressBar.visibility = View.VISIBLE
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                bindingLogin.progressBar.visibility = View.GONE
                return
            }
            mFusedLocationClient.requestLocationUpdates(
                locationRequest!!,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mFusedLocationClient.removeLocationUpdates(locationCallback)
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }
}