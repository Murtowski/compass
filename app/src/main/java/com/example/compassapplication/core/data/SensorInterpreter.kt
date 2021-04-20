package com.example.compassapplication.core.data

import com.example.compassapplication.core.domain.DomainLocation
import com.example.compassapplication.core.domain.SensorSample

interface SensorInterpreter {
    fun calculateNorthAngle(data: SensorSample, locationAngle: Float): Float?
    fun calculateLocationAngle(
        currentLocation: DomainLocation,
        destinationLocation: DomainLocation
    ): Float
}
