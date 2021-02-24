package com.example.compassapplication.core.data

import com.example.compassapplication.core.domain.DomainLocation

interface LocationSourceListener {
    fun locationChanged(currentLocation: DomainLocation)
}

interface LocationSource {
    fun startListening(listener: LocationSourceListener)
    fun stop()
}
