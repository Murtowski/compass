package com.example.compassapplication

import android.hardware.SensorEvent
import android.hardware.SensorManager
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

interface SensorUsecase {
    fun getAndRegister(): Flow<Float>
    fun stop()fun updateSensorManager(manager: SensorManager)


}

class SensorUsecaseImpl(private val sensorSource: SensorSource,
                        private val sensorInterpreter: SensorInterpreter
): SensorUsecase, SensorListener{

    // only the the most recently sent value is received, while previously sent elements are lost
    private val channel = ConflatedBroadcastChannel<Float>()
    private val startAzimuth = 0f

    override fun updateSensorManager(manager: SensorManager){
        this.sensorSource.updateSensorManager(manager)
    }

    override fun getAndRegister(): Flow<Float> {
        Timber.d("Registering new listener to Sensor")
        sensorSource.registerListenerAndStart(this)
        return channel.asFlow()
    }

    override fun stop() {
        sensorSource.unregisterListenerAndStop()
    }

    override fun onSensorData(data: SensorEvent) {
        if (!channel.isClosedForSend) {
            sensorInterpreter.newData(data)?.let {
                channel.offer(it)
            }
        }
    }
}