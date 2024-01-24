package com.galaxybookpublication.util

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.galaxybookpublication.models.repo.SharedPreferenceHelper
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.lastNoticedTime
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.stopTimeMillSec
import java.util.*
import java.util.concurrent.TimeUnit


class StopwatchService : Service() {
    override fun onBind(intent: Intent): IBinder? = null

    private val timer = Timer()

    companion object {
        const val TIMER_UPDATED = "timerUpdated"
        const val TIMER_EXTRA = "timeExtra"
        var IS_ACTIVITY_RUNNING = false
    }

    override fun onCreate() {
        super.onCreate()
        IS_ACTIVITY_RUNNING = true
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e("", "START SERVICE")
        var time = intent.getDoubleExtra(TIMER_EXTRA, 0.0)
        val preference = SharedPreferenceHelper.customPreference(baseContext)
        Log.e("SERVICE PREF TIMER", preference.lastNoticedTime.toString())
        Log.e("SERVICE PREF TIMER_MILL", preference.stopTimeMillSec.toString())
        if (preference.lastNoticedTime != 0) {
            val differ =
                TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - preference.stopTimeMillSec
            time += preference.lastNoticedTime + differ
            Log.e("SERVICE PREF DIFF", differ.toString())
        }

        timer.scheduleAtFixedRate(TimeTask(time), 0, 1000)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        timer.cancel()
        IS_ACTIVITY_RUNNING = false
        Log.e("SERVICE ON DESTROY", "DESTROYED")
        super.onDestroy()
    }

    private inner class TimeTask(private var time: Double) : TimerTask() {

        override fun run() {
            Log.e("", "TIME TASK RUNNING")
            val intent = Intent(TIMER_UPDATED)
            time++
            intent.putExtra(TIMER_EXTRA, time)
            sendBroadcast(intent)
        }
    }
}