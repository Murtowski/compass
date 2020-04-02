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

