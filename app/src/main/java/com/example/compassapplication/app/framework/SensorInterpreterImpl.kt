package com.example.compassapplication.app.framework

import android.hardware.SensorManager
import com.example.compassapplication.core.data.SensorInterpreter
import com.example.compassapplication.core.domain.DomainLocation
import com.example.compassapplication.core.domain.SensorSample
import com.example.compassapplication.core.domain.SensorType
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

const val alpha = 0.97f

class SensorInterpreterImpl : SensorInterpreter {

    private val gravity = floatArrayOf(0f, 0f, 0f)
    private val magnetic = floatArrayOf(0f, 0f, 0f)

    override fun calculateLocationAngle(
        currentLocation: DomainLocation,
        destinationLocation: DomainLocation
    ) = calculateBearingAngle(
        currentLocation.lat, currentLocation.lng,
        destinationLocation.lat, destinationLocation.lng
    )

    private fun calculateBearingAngle(
        lat1: Float,
        lon1: Float,
        lat2: Float,
        lon2: Float
    ): Float {
        val phi1 = Math.toRadians(lat1.toDouble())
        val phi2 = Math.toRadians(lat2.toDouble())
        val deltaLambda = Math.toRadians((lon2 - lon1).toDouble())
        val theta: Double = atan2(
            sin(deltaLambda) * cos(phi2),
            cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(deltaLambda)
        )
        return Math.toDegrees(theta).toFloat()
    }

    override fun calculateNorthAngle(data: SensorSample, locationAngle: Float): Float? {
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
            azimuth.toFloat() - locationAngle
        } else {
            null
        }
    }
}
