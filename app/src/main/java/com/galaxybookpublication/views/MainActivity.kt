package com.galaxybookpublication.views

import android.Manifest
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.galaxybookpublication.R
import com.galaxybookpublication.api.CommonFunctions
import com.galaxybookpublication.databinding.ActivityMainBinding
import com.galaxybookpublication.models.repo.SharedPreferenceHelper
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.authToken
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.isTimerStarted
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.profileImage
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.startTimer
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.userName
import com.galaxybookpublication.util.LocationTrackerService
import com.galaxybookpublication.util.LocationTrackerService.Companion.LATITUDE
import com.galaxybookpublication.util.LocationTrackerService.Companion.LOCATION_UPDATED
import com.galaxybookpublication.util.LocationTrackerService.Companion.LONGITUTE
import com.galaxybookpublication.util.OnCheckInListener
import com.galaxybookpublication.viewmodels.MainViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var bindingMain: ActivityMainBinding
    private lateinit var navController: NavController
    lateinit var preference: SharedPreferences
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var authToken: String = ""
    val fragmentDashboard = DashboardFragment()
    private var locationRequest: LocationRequest? = null
    private var onCheckInListener: OnCheckInListener? = null
    private var checkInType = ""

    companion object {

        @kotlin.jvm.JvmField
        var hashMapPaymentAmtTrack = HashMap<String, Int>()

        var PERMISSIONS =
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        var wayLatitude: Double = 0.0
        var wayLongitude: Double = 0.0
    }

    private val mainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    private val updateLocation: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            wayLatitude = intent.getDoubleExtra(LATITUDE, 0.0)
            wayLongitude = intent.getDoubleExtra(LONGITUTE, 0.0)
            val wayLatitudeStr = wayLatitude.toString()
            val wayLongitudeStr = wayLongitude.toString()
            if ((wayLongitudeStr.isNotEmpty() && wayLatitudeStr.isNotEmpty()) &&
                (wayLatitude != 0.0 && wayLongitude != 0.0) && authToken.isNotEmpty()
            ) {
                mainViewModel.locationUpdate(
                    applicationContext,
                    "Bearer $authToken",
                    wayLatitudeStr,
                    wayLongitudeStr
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1 * 1000).apply {
            setMinUpdateDistanceMeters(0.0f)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()

        preference = SharedPreferenceHelper.customPreference(this)
        bindingMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingMain.root)

        setSupportActionBar(bindingMain.appBarMain.toolbar)


        val drawerLayout: DrawerLayout = bindingMain.drawerLayout
        val navView: NavigationView = bindingMain.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)
        val headerView = bindingMain.navView.getHeaderView(0)
        Glide.with(this)
            .load(preference.profileImage)
            .placeholder(R.drawable.person_white_24)
            .circleCrop()
            .into(headerView.findViewById(R.id.imageView))
        val textView = headerView.findViewById<TextView>(R.id.tvUserName)
        textView.text = preference.userName
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_dashboard, R.id.nav_appointment,R.id.nav_appointments_new,R.id.nav_claim,R.id.nav_client,R.id.nav_specimen, R.id.nav_profile, R.id.nav_logout),
            drawerLayout
        )
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        setNavigationMenuClick()
        authToken = preference.authToken.toString()

        mainViewModel.logOutLiveData.observe(this, Observer {
            //if(it.)
            val serviceIntent = Intent(this, LocationTrackerService::class.java)
            stopService(serviceIntent)
            preference.edit().clear().apply()
            authToken = ""
            val intent = Intent(baseContext, SplashActivity::class.java)
            startActivity(intent)
            finish()
        })

        mainViewModel.locationLiveData.observe(this, Observer {
            Log.e("LocationTracking", it.message)
//                Toast.makeText(this, "LOCATION UPDATED:" + it.message, Toast.LENGTH_SHORT).show()
        })
    }

    private fun setNavigationMenuClick() {
        bindingMain.navView.setNavigationItemSelectedListener { menuItem ->
            bindingMain.drawerLayout.close()
            menuItem.isChecked = !menuItem.isChecked
            when (menuItem.itemId) {
                R.id.nav_dashboard -> {
                    navController.navigate(R.id.action_global_dashboard_fragment)
                }

                R.id.nav_appointment -> {
                    if (!preference.isTimerStarted) {
                        Toast.makeText(
                            applicationContext,
                            "Checkin for the day to proceed!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        navController.navigate(R.id.action_global_appointment_fragment)
                    }
                }
                R.id.nav_appointments_new -> {
                    if (!preference.isTimerStarted) {
                        Toast.makeText(applicationContext, "Checkin for the day to proceed!", Toast.LENGTH_SHORT).show()
                    } else {
                        navController.navigate(R.id.action_global_appointment_new_fragment)
                    }
                }
                R.id.nav_client ->{
                    navController.navigate(R.id.action_global_client_fragment)
                }
                R.id.nav_claim -> {
                    navController.navigate(R.id.action_global_claim)
                }
                R.id.nav_specimen ->{
                    navController.navigate(R.id.action_global_specimen_fragment)
                }

                R.id.nav_profile -> {
                    navController.navigate(R.id.action_global_profile_fragment)
                }

                R.id.nav_logout -> {
                    if (preference.isTimerStarted) {
                        Toast.makeText(
                            baseContext,
                            "Checkout for the day and logout",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        preference.startTimer = false
                        fragmentDashboard.onStopService(this)
                        if (wayLatitude == 0.0 || wayLongitude == 0.0) {
                            fetchLocationLogout()
                        } else {
                            mainViewModel.makeLogout(
                                "Bearer ${preference.authToken}",
                                wayLatitude.toString(),
                                wayLongitude.toString()
                            )
                        }
                    }
                }
            }
            true
        }
    }

    private fun fetchLocationLogout() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            mFusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    var wayLatitudeStr = it.latitude.toString()
                    var wayLongitudeStr = it.longitude.toString()
                    if (wayLatitudeStr.startsWith("0", true)) {
                        wayLatitudeStr = wayLatitudeStr.replaceFirst("0", "")
                    }
                    if (wayLongitudeStr.startsWith("0", true)) {
                        wayLongitudeStr = wayLongitudeStr.replaceFirst("0", "")
                    }
                    if (wayLatitudeStr.isNotEmpty() && wayLongitudeStr.isNotEmpty()) {
                        mainViewModel.makeLogout(
                            "Bearer ${preference.authToken}",
                            wayLatitudeStr,
                            wayLongitudeStr
                        )
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Please wait while fetching your location",
                            Toast.LENGTH_SHORT
                        ).show()
                        mFusedLocationClient.requestLocationUpdates(
                            locationRequest!!,
                            locationCallback,
                            Looper.getMainLooper()
                        )
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Please wait while fetching your location",
                        Toast.LENGTH_SHORT
                    ).show()
                    mFusedLocationClient.requestLocationUpdates(
                        locationRequest!!,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                }
            }
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        this@MainActivity,
                        101
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
                wayLatitude = location.latitude
                wayLongitude = location.longitude
                mFusedLocationClient.removeLocationUpdates(this)
                Log.e("checkInType",checkInType)
                if(checkInType.isNotEmpty()) {
                    if(checkInType == "checkIn") {
                        onCheckInListener?.onCheckIn()
                    } else if(checkInType == "checkOut") {
                        onCheckInListener?.onCheckOut()
                    }
                } else {
                    onCheckInListener?.dismissLoader()
                }
            }
        }
    }

    fun startCheckIn(isCheckIn: Boolean) {
        if(isCheckIn) {
            checkInType = "checkIn"
        } else {
            checkInType = "checkOut"
        }
        if (hasPermissions(this, PERMISSIONS)) {
            onCheckInListener?.showLoader()
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest!!)

            val client: SettingsClient = LocationServices.getSettingsClient(this)
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

            task.addOnSuccessListener { locationSettingsResponse ->
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    checkInType = ""
                    onCheckInListener?.dismissLoader()
                }
                if (wayLatitude == 0.0 || wayLongitude == 0.0) {
                    mFusedLocationClient.requestLocationUpdates(
                        locationRequest!!,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                } else {
                    checkInType = ""
                    if (isCheckIn) {
                        onCheckInListener?.onCheckIn()
                    } else {
                        val serviceIntent = Intent(this, LocationTrackerService::class.java)
                        stopService(serviceIntent)
                        onCheckInListener?.onCheckOut()
                    }
                }
                if (isCheckIn) {
                    fetchLocation()
                }
            }

            task.addOnFailureListener { exception ->
                onCheckInListener?.dismissLoader()
                if (exception is ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        if (isCheckIn) {
                            exception.startResolutionForResult(
                                this@MainActivity,
                                100
                            )
                        }
                        else {
                            exception.startResolutionForResult(
                                this@MainActivity,
                                110
                            )
                        }
                    } catch (sendEx: Exception) {
                        // Ignore the error.
                        sendEx.printStackTrace()
                    }
                } else {
                    checkInType = ""
                }
            }
        } else {
            permReqLauncher.launch(PERMISSIONS)
        }
    }

    fun checkPermissionRequired() {
        if (hasPermissions(this, PERMISSIONS)) {
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest!!)

            val client: SettingsClient = LocationServices.getSettingsClient(this)
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

            task.addOnSuccessListener { locationSettingsResponse ->
                fetchLocation()
            }

            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        exception.startResolutionForResult(
                            this@MainActivity,
                            100
                        )
                    } catch (sendEx: Exception) {
                        // Ignore the error.
                        sendEx.printStackTrace()
                    }
                }
            }
        } else {
            permReqLauncher.launch(PERMISSIONS)
        }
    }

    private fun fetchLocation() {
        if (!LocationTrackerService.isServiceRunning) {
            val serviceIntent = Intent(this, LocationTrackerService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
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
            }
        }

    private fun showPermissionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permission required!")
        builder.setMessage("Background Location Permission Needed!, tap \"Allow all time in the next screen\"")
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

    private fun hasPermissions(context: Context, permission: Array<String>): Boolean =
        permission.all {
            ActivityCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        Log.e("onResume", "on resume called")
        if (CommonFunctions.isFromAppointments.equals("true")){
            CommonFunctions.isFromAppointments = ""
            navController.navigate(R.id.action_global_appointment_fragment)
        }
    }

    override fun onStart() {
        super.onStart()
        if (preference.isTimerStarted)
            checkPermissionRequired()
        registerReceiver(updateLocation, IntentFilter(LOCATION_UPDATED))
        Log.e("onStart", "on start called")
    }

    override fun onPause() {
        super.onPause()
        Log.e("onPause", "on pause called")
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            if(resultCode == RESULT_OK) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {
                    onCheckInListener?.dismissLoader()
                }
                onCheckInListener?.showLoader()
                mFusedLocationClient.requestLocationUpdates(
                    locationRequest!!,
                    locationCallback,
                    Looper.getMainLooper()
                )
                fetchLocation()
            }
            else if(resultCode == RESULT_CANCELED) {
                Toast.makeText(applicationContext,"Location permission required to access application!",Toast.LENGTH_SHORT).show()
                finish()
            }
            else {
                checkInType = ""
            }
        }
        else if (requestCode == 110) {
            if(resultCode == RESULT_OK) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    onCheckInListener?.dismissLoader()
                }
                onCheckInListener?.showLoader()
                mFusedLocationClient.requestLocationUpdates(
                    locationRequest!!,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
            else {
                checkInType = ""
            }
        }
    }

    fun setOnCheckInListener(onCheckInListener: OnCheckInListener) {
        this.onCheckInListener = onCheckInListener
    }

}

