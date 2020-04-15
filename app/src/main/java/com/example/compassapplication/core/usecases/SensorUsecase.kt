package com.example.compassapplication.core.usecases

import com.example.compassapplication.core.data.SensorInterpreter
import com.example.compassapplication.core.data.SensorListener
import com.example.compassapplication.core.data.SensorSource
import com.example.compassapplication.core.domain.Azimuth
import com.example.compassapplication.core.domain.DomainLocation
import com.example.compassapplication.core.domain.SensorSample
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import timber.log.Timber

interface SensorUsecase {
    fun getAndRegister(): Flow<Azimuth>
    fun setLocationOffset(currentLocation: DomainLocation, destinationLocation: DomainLocation)
    fun clearLocationOffset()
    fun stop()


}

internal class SensorUsecaseImpl(private val sensorSource: SensorSource,
                                 private val sensorInterpreter: SensorInterpreter
): SensorUsecase, SensorListener {

    // only the the most recently sent value is received, while previously sent elements are lost
    private val channel = ConflatedBroadcastChannel<Azimuth>()

    override fun setLocationOffset(currentLocation: DomainLocation, destinationLocation: DomainLocation){
        sensorInterpreter.addLocationAngle(currentLocation, destinationLocation)
    }

    override fun clearLocationOffset() {
        sensorInterpreter.clearLocationAngle()
    }

    override fun getAndRegister(): Flow<Azimuth> {
        Timber.d("Registering new listener to Sensor")
        sensorSource.registerListenerAndStart(this)
        return channel.asFlow()
    }

    override fun stop() {
        sensorSource.unregisterListenerAndStop()
    }

    override fun onSensorData(data: SensorSample) {
        if (!channel.isClosedForSend) {
            sensorInterpreter.calculateNorthAngle(data)?.let {
                channel.offer(Azimuth(it))
            }
        }
    }
}