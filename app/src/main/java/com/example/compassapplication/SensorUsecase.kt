package com.example.compassapplication

import android.hardware.SensorEvent
import android.location.Location
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import timber.log.Timber

interface SensorUsecase {
    fun getAndRegister(): Flow<Float>
    fun setLocationOffset(currentLocation: Location, destinationLocation: Location)
    fun clearLocationOffset()
    fun stop()


}

internal class SensorUsecaseImpl(private val sensorSource: SensorSource,
                        private val sensorInterpreter: SensorInterpreter
): SensorUsecase, SensorListener{

    // only the the most recently sent value is received, while previously sent elements are lost
    private val channel = ConflatedBroadcastChannel<Float>()

    override fun setLocationOffset(currentLocation: Location, destinationLocation: Location){
        sensorInterpreter.addLocationAngle(currentLocation, destinationLocation)
    }

    override fun clearLocationOffset() {
        sensorInterpreter.clearLocationAngle()
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