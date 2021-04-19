package com.example.compassapplication.core.usecases

import com.example.compassapplication.core.data.LocationSource
import com.example.compassapplication.core.data.LocationSourceListener
import com.example.compassapplication.core.domain.DomainLocation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber

interface LocationUsecase {
    fun getAndListenLocation(): Flow<DomainLocation>
}

@ExperimentalCoroutinesApi
internal class LocationUsecaseImpl(
    private val locationSource: LocationSource
) : LocationUsecase {

    override fun getAndListenLocation(): Flow<DomainLocation> = callbackFlow {
        Timber.d("Registering new listener to Location")
        val locationCallback = object : LocationSourceListener {
            override fun locationChanged(currentLocation: DomainLocation) {
                offer(currentLocation)
            }
        }

        locationSource.startListening(locationCallback)

        awaitClose { locationSource.stop() }
    }
}
