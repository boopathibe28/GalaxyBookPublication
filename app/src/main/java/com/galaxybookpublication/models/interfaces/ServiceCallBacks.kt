package com.galaxybookpublication.models.interfaces

import android.content.Intent
import com.galaxybookpublication.views.MainActivity

interface ServiceCallBacks {
    fun onStopService(activity: MainActivity)
}