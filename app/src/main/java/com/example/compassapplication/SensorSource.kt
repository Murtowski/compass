package com.example.compassapplication

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import timber.log.Timber
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Created by
 * @author Piotr Piskorski
 * @date on 02.04.2020.
 */

interface SensorListener{
    fun onSensorData(data: SensorEvent)
}


interface SensorSource{
    fun registerListenerAndStart(listener: SensorListener)
    fun unregisterListenerAndStop()

}

class SensorSourceImpl(private var sensorManager: SensorManager):
    SensorSource,
    SensorEventListener{

    var listener: SensorListener ?= null

    override fun registerListenerAndStart(listener: SensorListener) {
        this.listener = listener
        startListening()
    }

    override fun unregisterListenerAndStop() {
        this.listener = null
        stopListening()
    }

    private fun startListening(){
        val sensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        val sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val sampling = SensorManager.SENSOR_DELAY_GAME
        sensorManager.registerListener(this, sensorMagnetic, sampling)
        sensorManager.registerListener(this, sensorAccelerometer, sampling)
    }

    private fun stopListening(){
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            listener?.onSensorData(event)
        }
    }
}


const val alpha = 0.97f

interface SensorInterpreter {
    fun calculateNorthAngle(data: SensorEvent): Float?
    fun addLocationAngle(currentLocation: Location, destinationLocation: Location)
}

class SensorInterpreterImpl(private val sensorManager: SensorManager): SensorInterpreter{

    val gravity = floatArrayOf(0f,0f,0f)
    val magnetic = floatArrayOf(0f,0f,0f)

    var offsetAngle = 0f

    override fun addLocationAngle(currentLocation: Location, destinationLocation: Location) {
        offsetAngle = calculateBearingAngle(
            currentLocation.latitude, currentLocation.longitude,
            destinationLocation.latitude, destinationLocation.longitude
        )
        Timber.d("New Location offet counted: $offsetAngle")
    }

    fun calculateBearingAngle(
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

    override fun calculateNorthAngle(data: SensorEvent): Float? {
        if(data.isAccelerometer()){
            for(i in 0..2){
                gravity[i] = alpha * gravity[i] + (1- alpha) * data.values[i]
            }

        }

        if(data.isMagnetic()){
            for(i in 0..2){
                magnetic[i] = alpha * magnetic[i] + (1 - alpha) * data.values[i]
            }
        }

        val R = floatArrayOf(0f,0f,0f,0f,0f,0f,0f,0f,0f)
        val I = floatArrayOf(0f,0f,0f,0f,0f,0f,0f,0f,0f)
        if(SensorManager.getRotationMatrix(R, I, gravity, magnetic)){
            val orientation = floatArrayOf(0f,0f,0f)
            SensorManager.getOrientation(R, orientation)
            val azimuth = Math.toDegrees(orientation[0].toDouble())
            return azimuth.toFloat() - offsetAngle
        }else
            return null



    }
}


fun SensorEvent.isMagnetic(): Boolean{
    return this.sensor.type == Sensor.TYPE_MAGNETIC_FIELD
}

fun SensorEvent.isAccelerometer(): Boolean{
    return this.sensor.type == Sensor.TYPE_ACCELEROMETER
}

