package com.example.compassapplication

import android.app.Application
import android.content.Context
import android.hardware.SensorEvent
import android.hardware.SensorManager
import androidx.lifecycle.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Created by
 * @author Piotr Piskorski
 * @date on 02.04.2020.
 */

class MainViewModelFactory(
    private val sensorUsecase: SensorUsecase
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MainViewModel(
        sensorUsecase
    ) as T
}

class MainViewModel(
    private val sensorUsecase: SensorUsecase
): ViewModel(){

    private var previousAzimuth = 0f

    val azimuth: LiveData<Pair<Float,Float>>  = sensorUsecase.getAndRegister().asLiveData().map {
            Timber.d("Receiver new: $it")
            val rotation = Pair(previousAzimuth, it)
            previousAzimuth = it
            rotation
        }

    var longitude = MutableLiveData(0f)
    var latitude = MutableLiveData(0f)

    override fun onCleared() {
        sensorUsecase.stop()
        super.onCleared()
    }
}