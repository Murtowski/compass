package com.example.compassapplication

import android.hardware.Sensor
import android.hardware.SensorEvent

/**
 * Created by
 * @author Piotr Piskorski
 * @date on 02.04.2020.
 */
interface SensorInterpreter {
    fun newData(data: SensorEvent)
}


class SensorInterpreterImpl(): SensorInterpreter{

    val channel = Chan

    override fun newData(data: SensorEvent) {
        if(data.isMagnetic()){

        }

        if(data.isAccelerometer()){

        }

    }
}


fun SensorEvent.isMagnetic(): Boolean{
    return this.sensor.type == Sensor.TYPE_MAGNETIC_FIELD
}

fun SensorEvent.isAccelerometer(): Boolean{
    return this.sensor.type == Sensor.TYPE_ACCELEROMETER
}