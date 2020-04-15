package com.example.compassapplication.core.domain

enum class SensorType{
    ACCELEROMETER,
    MAGNETOMETER
}

data class SensorSample (
    val values: FloatArray,
    val sensorType: SensorType
)