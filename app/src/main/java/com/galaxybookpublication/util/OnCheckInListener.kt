package com.galaxybookpublication.util

interface OnCheckInListener {
    fun onCheckIn()
    fun onCheckOut()
    fun dismissLoader()
    fun showLoader()
}