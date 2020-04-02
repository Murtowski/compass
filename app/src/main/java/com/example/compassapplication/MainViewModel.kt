package com.example.compassapplication

import android.hardware.SensorEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.launch

/**
 * Created by
 * @author Piotr Piskorski
 * @date on 02.04.2020.
 */
class MainViewModel(private val sensorSource: SensorSource,
                    private val sensorInterpreter: SensorInterpreter): ViewModel(), SensorListener{
    // only the the most recently sent value is received, while previously sent elements are lost
    val channel = ConflatedBroadcastChannel<SensorEvent>()

    init {
        sensorSource.registerListenerAndStart(this)
    }

    override fun onSensorData(data: SensorEvent) {
        viewModelScope.launch {
            channel.send(data)
        }

        sensorInterpreter.newData(data)
    }

    override fun onCleared() {
        super.onCleared()
        sensorSource.unregisterListenerAndStop()
        channel.close()
    }
}