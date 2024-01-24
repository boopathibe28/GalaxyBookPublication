package com.galaxybookpublication.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import java.util.Locale

interface OnAddressListener {
    fun result(address: Address?)
}

object AppUtils {

    fun getCompleteReadableAddress(context: Context, latitude: Double, longitude: Double,
                                   onAddressListener: OnAddressListener) {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latitude, longitude, 1,
                object: Geocoder.GeocodeListener {
                    override fun onGeocode(p0: MutableList<Address>) {
                        if (p0.isNotEmpty()) {
                            onAddressListener.result(p0[0])
                        } else
                            onAddressListener.result(null)
                    }

                })
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1) as List<Address>
                if (addresses.isNotEmpty()) {
                    onAddressListener.result(addresses[0])
                } else
                    onAddressListener.result(null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}