package com.example.compassapplication

import android.hardware.SensorEvent
import android.location.Location
import android.location.LocationManager
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import timber.log.Timber

interface LocationUsecase{
    fun getAndListenLocation(): Flow<Location>
    fun stop()
}

internal class LocationUsecaseImpl(
    private val locationSource: LocationSource
):LocationUsecase, LocationSourceListener{

    private val channel = ConflatedBroadcastChannel<Location>()

    override fun getAndListenLocation(): Flow<Location> {
        Timber.d("Registering new listener to Location")
        locationSource.startListening(this)
        return channel.asFlow()
    }

    override fun stop() {
        locationSource.stop()
    }

    override fun locationChanged(currentLocation: Location) {
        if (!channel.isClosedForSend) {
            channel.offer(currentLocation)
        }
    }
}