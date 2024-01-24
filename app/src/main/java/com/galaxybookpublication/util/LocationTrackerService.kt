package com.galaxybookpublication.util

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import java.util.Timer
import java.util.TimerTask

class LocationTrackerService : Service() {
    var _mContext = this
    private var locationRequest: LocationRequest? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var wayLatitude = 0.0
    private var wayLongitude = 0.0

    companion object {
        const val LOCATION_UPDATED = "locationUpdated"
        const val LONGITUTE = "longitude"
        const val LATITUDE = "latitude"
        var isServiceRunning = false
    }

    override fun onCreate() {
        super.onCreate()
        isServiceRunning = true
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3 * 60 * 1000).apply {
            setMinUpdateDistanceMeters(0.0f)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChanel()
        }
    }

    private fun createNotificationChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val NOTIFICATION_CHANNEL_ID = "com.galaxybookpublication"
            val channelName = "Background Service"
            val chan = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_NONE
            )
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager =
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            manager.createNotificationChannel(chan)
            val notificationBuilder =
                NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            val notification: Notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
            startForeground(2, notification)
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(!this@LocationTrackerService::mFusedLocationClient.isInitialized) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            if (ActivityCompat.checkSelfPermission(
                    _mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    _mContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //return
            }
            mFusedLocationClient.requestLocationUpdates(
                locationRequest!!,
                locationCallback,
                Looper.getMainLooper()
            )
        }
        return START_STICKY
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList: List<Location> = locationResult.locations
            if (locationList.isNotEmpty()) {
                val location: Location = locationList[0]
                wayLatitude = location.latitude
                wayLongitude = location.longitude

                val intent = Intent(LOCATION_UPDATED)
                intent.putExtra(LONGITUTE, wayLongitude)
                intent.putExtra(LATITUDE, wayLatitude)
                sendBroadcast(intent)
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.e("LocationTrackerService","onDestroy")
        isServiceRunning = false
        mFusedLocationClient.removeLocationUpdates(locationCallback)
    }
}