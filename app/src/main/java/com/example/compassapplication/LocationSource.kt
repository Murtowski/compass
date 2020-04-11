package com.example.compassapplication

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.google.android.gms.location.FusedLocationProviderClient
import timber.log.Timber

interface LocationSourceListener{
    fun locationChanged(currentLocation: Location)
}

interface LocationSource {
    fun startListening(listener: LocationSourceListener)
    fun stop()
}

class LocationSourceImpl(
    private val locationManager: LocationManager,
    private val fusedLocationProviderClient: FusedLocationProviderClient
): LocationSource, LocationListener{

    private var listener: LocationSourceListener ?= null

    override fun startListening(listener: LocationSourceListener) {
        this.listener = listener

        try {
            onLocationChanged(locationManager.getLastKnownLocation(""))
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000,
                10f, this)
        }catch (e: SecurityException){
            throw e // allow to crash
        }

    }

    override fun stop() {
        locationManager.removeUpdates(this)
    }

    override fun onLocationChanged(location: Location?) {
        Timber.d("New location: ${location?.toString()}")
        location?.let {
            listener?.locationChanged(it)
        }

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderEnabled(provider: String?) {}

    override fun onProviderDisabled(provider: String?) {}
}