package com.example.compassapplication.core.usecases

import com.example.compassapplication.core.data.LocationSource
import com.example.compassapplication.core.data.LocationSourceListener
import com.example.compassapplication.core.domain.DomainLocation
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import timber.log.Timber

interface LocationUsecase{
    fun getAndListenLocation(): Flow<DomainLocation>
    fun stop()
}

internal class LocationUsecaseImpl(
    private val locationSource: LocationSource
): LocationUsecase,
    LocationSourceListener {

    private val channel = ConflatedBroadcastChannel<DomainLocation>()

    override fun getAndListenLocation(): Flow<DomainLocation> {
        Timber.d("Registering new listener to Location")
        locationSource.startListening(this)
        return channel.asFlow()
    }

    override fun stop() {
        locationSource.stop()
    }

    override fun locationChanged(currentLocation: DomainLocation) {
        if (!channel.isClosedForSend) {
            channel.offer(currentLocation)
        }
    }
}