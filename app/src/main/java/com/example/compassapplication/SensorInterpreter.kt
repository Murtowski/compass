package com.example.compassapplication

import android.hardware.Sensor
import android.hardware.SensorEvent
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

/**
 * Created by
 * @author Piotr Piskorski
 * @date on 02.04.2020.
 */
//interface SensorInterpreter {
//    fun newData(data: SensorEvent)
//}
//
//const val alpha = 0.97f
//
//class SensorInterpreterImpl(): SensorInterpreter{
//
//
//    val gravity = arrayOf(0f,0f,0f)
//    val magnetic = arrayOf(0f,0f,0f)
//
//    override fun newData(data: SensorEvent) {
//        if(data.isAccelerometer()){
//            for(i in 0..2){
//                gravity[i] = alpha * gravity[i] + (1- alpha) * data.values[i]
//            }
//
//        }
//
//        if(data.isMagnetic()){
//            for(i in 0..2){
//                gravity[i] = alpha * gravity[i] + (1 - alpha) * data.values[i]
//            }
//        }
//
//        val success =
//
//
//
//    }
//}
//
//
//fun SensorEvent.isMagnetic(): Boolean{
//    return this.sensor.type == Sensor.TYPE_MAGNETIC_FIELD
//}
//
//fun SensorEvent.isAccelerometer(): Boolean{
//    return this.sensor.type == Sensor.TYPE_ACCELEROMETER
//}