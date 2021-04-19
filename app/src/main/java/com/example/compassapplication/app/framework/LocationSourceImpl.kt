package com.example.compassapplication.app.framework

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.example.compassapplication.app.framework.util.DataConverter.toDomain
import com.example.compassapplication.core.data.LocationSource
import com.example.compassapplication.core.data.LocationSourceListener
import com.google.android.gms.location.FusedLocationProviderClient
import timber.log.Timber

class LocationSourceImpl(
    private val locationManager: LocationManager,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : LocationSource, LocationListener {

    private var listener: LocationSourceListener? = null

    override fun startListening(listener: LocationSourceListener) {
        this.listener = listener

        try {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                Timber.d("Fused got last location")
                location?.let { notNullLocation ->
                    onLocationChanged(notNullLocation)
                }
            }
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000,
                10f, this
            )
        } catch (e: SecurityException) {
            throw e // allow to crash
        }
    }

    override fun stop() {
        locationManager.removeUpdates(this)
    }

    override fun onLocationChanged(location: Location) {
        Timber.d("New location: $location")
        listener?.locationChanged(location.toDomain())
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        /*non-op*/
    }
}
