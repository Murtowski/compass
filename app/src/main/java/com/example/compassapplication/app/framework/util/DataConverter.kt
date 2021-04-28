package com.example.compassapplication.app.framework.util

import android.location.Location
import com.example.compassapplication.core.domain.DomainLocation

object DataConverter {

    fun Location.toDomain() = DomainLocation(this.latitude.toFloat(), this.longitude.toFloat())

    fun DomainLocation.toAndroidLocation() = Location("").apply {
        latitude = lat.toDouble()
        longitude = lng.toDouble()
    }
}
