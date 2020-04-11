package com.example.compassapplication

import android.hardware.SensorEvent
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow

interface SensorUsecase {
    fun getAndRegister(): Flow<Float>
    fun stop()

}

class SensorUsecaseImpl(private val sensorSource: SensorSource,
                        private val sensorInterpreter: SensorInterpreter
): SensorUsecase, SensorListener{

    // only the the most recently sent value is received, while previously sent elements are lost
    private val channel = ConflatedBroadcastChannel<Float>()

    override fun getAndRegister(): Flow<Float> {
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