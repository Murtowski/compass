package com.example.compassapplication.core.data

import com.example.compassapplication.core.domain.DomainLocation
import com.example.compassapplication.core.domain.SensorSample

interface SensorInterpreter {
    fun calculateNorthAngle(data: SensorSample): Float?
    fun addLocationAngle(currentLocation: DomainLocation, destinationLocation: DomainLocation)
    fun clearLocationAngle()
}