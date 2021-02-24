package com.example.compassapplication.core.data

import com.example.compassapplication.core.domain.SensorSample

/**
 * Created by
 * @author Piotr Piskorski
 * @date on 02.04.2020.
 */

interface SensorListener {
    fun onSensorData(data: SensorSample)
}

interface SensorSource {
    fun registerListenerAndStart(listener: SensorListener)
    fun unregisterListenerAndStop()
}
