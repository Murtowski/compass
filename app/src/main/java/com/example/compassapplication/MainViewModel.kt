package com.example.compassapplication

import android.location.Location
import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by
 * @author Piotr Piskorski
 * @date on 02.04.2020.
 */

class MainViewModelFactory(
    private val sensorUsecase: SensorUsecase,
    private val locationUsecase: LocationUsecase
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MainViewModel(
        sensorUsecase, locationUsecase
    ) as T
}

class MainViewModel(
    private val sensorUsecase: SensorUsecase,
    private val locationUsecase: LocationUsecase
): ViewModel(){

    /*
    * Count Azimuth, by default to NORTH
    * */
    private var previousAzimuth = 0f
    val azimuth: LiveData<Pair<Float,Float>>  = sensorUsecase.getAndRegister().asLiveData().map {
            val rotation = Pair(previousAzimuth, it)
            previousAzimuth = it
            rotation
        }

    /*
    * LONGITUDE
    * */
    val longitude = object: MutableLiveData<Double?>(0.0){
        override fun setValue(value: Double?) {
            super.setValue(value)
            Timber.d("Updating longitude")
            updateDestination()
        }
    }
    val longitudeError : LiveData<InputError> = longitude.map {
        when{
            it == null -> InputError.INVALID_FORMAT
            it !in (-180f .. 180f) ->  InputError.OUT_OF_RANGE
            else -> InputError.NONE
        }
    }
    val isLongitudeValid: LiveData<Boolean> = longitudeError.map {
        it == InputError.NONE
    }

    /*
    * LATITUDE
    * */
    val latitude = object: MutableLiveData<Double?>(0.0){
        override fun setValue(value: Double?) {
            super.setValue(value)
            Timber.d("Updating latitude")
            updateDestination()
        }
    }
    val latitudeError : LiveData<InputError> = latitude.map {
        Timber.d("Edit Lat:$it")
        when{
            it == null -> InputError.INVALID_FORMAT
            it !in (-90f .. 90f) ->  InputError.OUT_OF_RANGE
            else -> InputError.NONE
        }
    }
    val isLatitudeValid: LiveData<Boolean> = latitudeError.map {
        it == InputError.NONE
    }

    /*
    * LATITUDE & LONGITUDE validation > automatic update destination
    * */
    private fun updateDestination(){
        Timber.d("Try to update dest")
        viewModelScope.launch {
            delay(500)
            if(isLocationPermissionGranted.value == true
                && currentLocation != null
                && isLatitudeValid.value == true
                && isLongitudeValid.value == true){
                Timber.d("Update destination")
                val newDestination = Location("").also {
                    it.latitude = latitude.value!!
                    it.longitude = longitude.value!!
                }
                sensorUsecase.setLocationOffset(currentLocation!!, newDestination)
            }
        }

    }

    /*
    * Location
    * */

    var currentLocation : Location ?= null
    val isLocationPermissionGranted = object :MutableLiveData<Boolean?>(null){
        override fun setValue(permissionGranted: Boolean?) {
            super.setValue(permissionGranted)
            Timber.d("Location permission changed: $permissionGranted")
            if(permissionGranted == true){
                viewModelScope.launch {
                    locationUsecase.getAndListenLocation().collect {
                        currentLocation = it
                    }
                }

            }
        }
    }

    /*
    * Clear
    * */
    override fun onCleared() {
        sensorUsecase.stop()
        locationUsecase.stop()
        super.onCleared()
    }
}

enum class InputError(val msg: String?){ // Here we may use @StringRes
    INVALID_FORMAT("Invalid Format"),
    OUT_OF_RANGE("Given location out of range"),
    NONE(null)
}