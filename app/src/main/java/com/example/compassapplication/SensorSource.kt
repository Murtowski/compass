package com.example.compassapplication

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

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

class SensorSourceImpl(private val sensorManager: SensorManager):
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

interface SensorInterpreter {
    fun newData(data: SensorEvent): Float?
}

const val alpha = 0.97f

class SensorInterpreterImpl(private val sensorManager: SensorManager): SensorInterpreter{


    val gravity = floatArrayOf(0f,0f,0f)
    val magnetic = floatArrayOf(0f,0f,0f)

    override fun newData(data: SensorEvent): Float? {
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
            return azimuth.toFloat()
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

