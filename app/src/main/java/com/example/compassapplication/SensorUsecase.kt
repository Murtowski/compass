package com.example.compassapplication

import android.hardware.SensorEvent
import android.location.Location
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import timber.log.Timber

interface SensorUsecase {
    fun getAndRegister(): Flow<Float>
    fun setLocationOffset(location: Location)
    fun stop()


}

internal class SensorUsecaseImpl(private val sensorSource: SensorSource,
                        private val sensorInterpreter: SensorInterpreter
): SensorUsecase, SensorListener{

    // only the the most recently sent value is received, while previously sent elements are lost
    private val channel = ConflatedBroadcastChannel<Float>()

    override fun setLocationOffset(location: Location) {
        val current = Location("").apply {
            latitude = 0.0
            longitude = 0.0
        }
        sensorInterpreter.addLocationAngle(current, location)
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
            sensorInterpreter.calculateNorthAngle(data)?.let {
                channel.offer(it)
            }
        }
    }
}