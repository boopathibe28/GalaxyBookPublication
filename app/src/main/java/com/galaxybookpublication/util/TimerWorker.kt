/*
package com.galaxybookpublication.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.galaxybookpublication.models.repo.SharedPreferenceHelper
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.countTimer
import java.util.*

class TimerWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    var sharedPref = SharedPreferenceHelper.customPreference(context)

    private fun timerStringFromLong(ms: Long): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60) % 60)
        val hours = (ms / (1000 * 60 * 60) % 24)
        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hours: Long, minutes: Long, seconds: Long): String {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    override suspend fun doWork(): Result {
        return Result.success()
    }
}*/
