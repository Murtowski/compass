package com.example.compassapplication.app.framework

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.location.Location
import com.example.compassapplication.core.data.SensorInterpreter
import com.example.compassapplication.core.domain.DomainLocation
import com.example.compassapplication.core.domain.SensorSample
import com.example.compassapplication.core.domain.SensorType
import timber.log.Timber
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

const val alpha = 0.97f

class SensorInterpreterImpl(private val sensorManager: SensorManager) :
    SensorInterpreter {

    private val gravity = floatArrayOf(0f, 0f, 0f)
    private val magnetic = floatArrayOf(0f, 0f, 0f)

    private var offsetAngle = 0f

    override fun clearLocationAngle() {
        offsetAngle = 0f
    }

    override fun addLocationAngle(
        currentLocation: DomainLocation,
        destinationLocation: DomainLocation
    ) {
        offsetAngle = calculateBearingAngle(
            currentLocation.lat, currentLocation.lng,
            destinationLocation.lat, destinationLocation.lng
        )
        Timber.d("New Location offet counted: $offsetAngle")
    }

    private fun calculateBearingAngle(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Float {
        val Phi1 = Math.toRadians(lat1)
        val Phi2 = Math.toRadians(lat2)
        val DeltaLambda = Math.toRadians(lon2 - lon1)
        val Theta: Double = atan2(
            sin(DeltaLambda) * cos(Phi2),
            cos(Phi1) * sin(Phi2) - sin(Phi1) * cos(Phi2) * cos(DeltaLambda)
        )
        return Math.toDegrees(Theta).toFloat()
    }

    override fun calculateNorthAngle(data: SensorSample): Float? {
        if (data.sensorType == SensorType.ACCELEROMETER) {
            for (i in 0..2) {
                gravity[i] = alpha * gravity[i] + (1 - alpha) * data.values[i]
            }

        }

        if (data.sensorType == SensorType.MAGNETOMETER) {
            for (i in 0..2) {
                magnetic[i] = alpha * magnetic[i] + (1 - alpha) * data.values[i]
            }
        }

        val R = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        val I = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

        return if (SensorManager.getRotationMatrix(R, I, gravity, magnetic)) {
            val orientation = floatArrayOf(0f, 0f, 0f)
            SensorManager.getOrientation(R, orientation)
            val azimuth = Math.toDegrees(orientation[0].toDouble())
            azimuth.toFloat() - offsetAngle
        } else {
            null
        }
    }
}
