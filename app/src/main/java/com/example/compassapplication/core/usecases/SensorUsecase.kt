package com.example.compassapplication.core.usecases

import com.example.compassapplication.core.data.SensorListener
import com.example.compassapplication.core.data.SensorSource
import com.example.compassapplication.core.domain.SensorSample
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber

interface SensorUsecase {
    fun getAndListenSensor(): Flow<SensorSample>
}

@ExperimentalCoroutinesApi
internal class SensorUsecaseImpl(
    private val sensorSource: SensorSource
) : SensorUsecase {

    override fun getAndListenSensor(): Flow<SensorSample> = callbackFlow {
        Timber.d("Registering new listener to Sensor")
        val sensorCallback = object : SensorListener {
            override fun onSensorData(data: SensorSample) {
                offer(data)
            }
        }
        sensorSource.registerListenerAndStart(sensorCallback)

        awaitClose { sensorSource.unregisterListenerAndStop() }
    }
}
