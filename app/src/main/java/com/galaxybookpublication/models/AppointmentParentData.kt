package com.galaxybookpublication.models

import com.galaxybookpublication.models.data.response.AppointmentResponse

data class AppointmentParentData(
    val parentTitle: String? = null,
    var type: Int = Constants.PARENT,
    var subList: MutableList<AppointmentResponse.Datas.Data> = ArrayList(),
    var isExpanded: Boolean = false
) {
    object Constants {
        const val PARENT = 0
        const val CHILD = 1
    }
}