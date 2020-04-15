package com.example.compassapplication.app.framework

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.compassapplication.core.data.SensorListener
import com.example.compassapplication.core.data.SensorSource
import com.example.compassapplication.core.domain.SensorSample
import com.example.compassapplication.core.domain.SensorType

class SensorSourceImpl(private var sensorManager: SensorManager):
    SensorSource,
    SensorEventListener {

    var listener: SensorListener?= null

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

            listener?.onSensorData(SensorSample(event.values, event.getType()))
        }
    }
}

fun SensorEvent.getType(): SensorType{
    return when(sensor.type){
        Sensor.TYPE_MAGNETIC_FIELD -> SensorType.MAGNETOMETER
        Sensor.TYPE_ACCELEROMETER -> SensorType.ACCELEROMETER
        else -> throw IllegalArgumentException("We should use that sensor: ${sensor.type}")
    }
}
