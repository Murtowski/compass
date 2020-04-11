package com.example.compassapplication

import android.app.Application
import android.hardware.SensorEvent
import androidx.lifecycle.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.launch

/**
 * Created by
 * @author Piotr Piskorski
 * @date on 02.04.2020.
 */

class DiscoverDivicesViewModelFactory(
    private val sensorUsecase: SensorUsecase
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MainViewModel(
        sensorUsecase
    ) as T
}

class MainViewModel(private val sensor: SensorUsecase): ViewModel(){

    val azimuth: LiveData<Float> by lazy {
        sensor.getAndRegister().asLiveData()
    }

    override fun onCleared() {
        sensor.stop()
        super.onCleared()
    }
}