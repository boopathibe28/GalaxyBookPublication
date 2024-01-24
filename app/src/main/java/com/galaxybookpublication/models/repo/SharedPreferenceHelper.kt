package com.galaxybookpublication.models.repo

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object SharedPreferenceHelper {

    val PREF_NAME = "GalaxyBookPublication"
    val USER_NAME = "USER_NAME"
    val USER_ID = "USER_ID"
    val USER_PASSWORD = "PASSWORD"
    val EMAIL_ID = "EMAIL_ID"
    val ADDRESS = "ADDRESS"
    val DOB = "DOB"
    val GENDER = "GENDER"
    val PROFILE_IMAGE = "PROFILE_IMAGE"
    val EMERGENCY_NUMBER = "EMERGENCY_NUMBER"
    val PHONE_NO = "PHONE_NO"
    val AUTH_TOKEN = "AUTH_TOKEN"
    val START_TIME_KEY = "START_KEY"
    val STOP_TIME_KEY = "STOP_KEY"
    val STOP_TIME_MILSEC = "STOP_TIME_MILSEC"
    val COUNTING_KEY = "COUNTING_KEY"
    val RECEIVING_AMT = "RECEIVING_AMT"
    val IS_TIMER_STARTED = "IS_TIMER_STARTED"


    fun defaultPreference(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    fun customPreference(context: Context, name: String? = PREF_NAME): SharedPreferences =
        context.getSharedPreferences(name, Context.MODE_PRIVATE)

    private inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }

    fun SharedPreferences.Editor.put(pair: Pair<String, Any>) {
        val key = pair.first
        val value = pair.second
        when (value) {
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Boolean -> putBoolean(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            else -> error("Only primitive types can be stored in SharedPreferences")
        }
    }

    var SharedPreferences.userName
        get() = getString(USER_NAME, "")
        set(value) {
            editMe {
                it.putString(USER_NAME, value)
            }
        }

    var SharedPreferences.password
        get() = getString(USER_PASSWORD, "")
        set(value) {
            editMe {
                it.putString(USER_PASSWORD, value)
            }
        }

    var SharedPreferences.emaiId
        get() = getString(EMAIL_ID, "")
        set(value) {
            editMe {
                it.putString(EMAIL_ID, value)
            }
        }

    var SharedPreferences.phoneNo
        get() = getString(PHONE_NO, "")
        set(value) {
            editMe {
                it.putString(PHONE_NO, value)
            }
        }

    var SharedPreferences.authToken
        get() = getString(AUTH_TOKEN, "")
        set(value) {
            editMe {
                it.putString(AUTH_TOKEN, value)
            }
        }

    var SharedPreferences.userId
        get() = getString(USER_ID, "")
        set(value) {
            editMe {
                it.putString(USER_ID, value)
            }
        }

    var SharedPreferences.address
        get() = getString(ADDRESS, "")
        set(value) {
            editMe {
                it.putString(ADDRESS, value)
            }
        }

    var SharedPreferences.dob
        get() = getString(DOB, "")
        set(value) {
            editMe {
                it.putString(DOB, value)
            }
        }

    var SharedPreferences.gender
        get() = getString(GENDER, "")
        set(value) {
            editMe {
                it.putString(GENDER, value)
            }
        }

    var SharedPreferences.emergencyContact
        get() = getString(EMERGENCY_NUMBER, "")
        set(value) {
            editMe {
                it.putString(EMERGENCY_NUMBER, value)
            }
        }

    var SharedPreferences.profileImage
        get() = getString(PROFILE_IMAGE, "")
        set(value) {
            editMe {
                it.putString(PROFILE_IMAGE, value)
            }
        }

    var SharedPreferences.receivedAmt
        get() = getString(RECEIVING_AMT, "")
        set(value) {
            editMe {
                it.putString(RECEIVING_AMT, value)
            }
        }

    var SharedPreferences.clearValues
        get() = {
            ->
           /* getString(PREF_NAME, "")
            getString(USER_NAME, "")
            getString(USER_ID, "")
            getString(USER_PASSWORD, "")
            getString(EMAIL_ID, "")
            getString(PHONE_NO, "")
            getString(AUTH_TOKEN, "")
            getString(START_TIME_KEY, "")
            getString(STOP_TIME_KEY, "")
            getString(COUNTING_KEY, "")*/
        }
        set(value) {
            editMe {
                it.clear()
               /* it.putString(PREF_NAME, value.toString())
                it.putString(USER_NAME, value.toString())
                it.putString(USER_ID, value.toString())
                it.putString(USER_PASSWORD, value.toString())
                it.putString(EMAIL_ID, value.toString())
                it.putString(PHONE_NO, value.toString())
                it.putString(AUTH_TOKEN, value.toString())
                it.putString(START_TIME_KEY, value.toString())
                it.putString(STOP_TIME_KEY, value.toString())
                it.putString(COUNTING_KEY, value.toString())*/
            }
        }


    var SharedPreferences.countTimer
        get() = getBoolean(COUNTING_KEY, false)
        set(value) {
            editMe {
                it.putBoolean(COUNTING_KEY, value)
            }
        }

    var SharedPreferences.startTimer
        get() = getBoolean(START_TIME_KEY, false)
        set(value) {
            editMe {
                it.putBoolean(START_TIME_KEY, value)
            }
        }

    var SharedPreferences.lastNoticedTime
        get() = getInt(STOP_TIME_KEY, 0)
        set(value) {
            editMe {
                it.putInt(STOP_TIME_KEY, value)
            }
        }

    var SharedPreferences.isTimerStarted
        get() = getBoolean(IS_TIMER_STARTED, false)
        set(value) {
            editMe {
                it.putBoolean(IS_TIMER_STARTED, value)
            }
        }

    var SharedPreferences.stopTimeMillSec
        get() = getLong(STOP_TIME_MILSEC, 0)
        set(value) {
            editMe {
                it.putLong(STOP_TIME_MILSEC, value)
            }
        }
}