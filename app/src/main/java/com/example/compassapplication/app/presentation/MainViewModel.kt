package com.example.compassapplication.app.presentation

import android.location.Location
import androidx.lifecycle.*
import com.example.compassapplication.app.framework.util.DataConverter.toAndroidLocation
import com.example.compassapplication.app.framework.util.DataConverter.toDomain
import com.example.compassapplication.core.usecases.LocationUsecase
import com.example.compassapplication.core.usecases.SensorUsecase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by
 * @author Piotr Piskorski
 * @date on 02.04.2020.
 */

enum class InputError(val msg: String?) { // Here we may use @StringRes
    INVALID_FORMAT("Invalid Format"),
    OUT_OF_RANGE("Given location out of range"),
    NONE(null)
}

class MainViewModelFactory(
    private val sensorUsecase: SensorUsecase,
    private val locationUsecase: LocationUsecase
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MainViewModel(
        sensorUsecase, locationUsecase
    ) as T
}

class MainViewModel(
    private val sensorUsecase: SensorUsecase,
    private val locationUsecase: LocationUsecase
) : ViewModel() {
    val cancellationHandler = CoroutineExceptionHandler { _, exception ->
        Timber.d("CoroutineExceptionHandler got $exception")
    }

    /*
    * Count Azimuth, by default to NORTH
    * */
    private var previousAzimuth = 0f
    val azimuth: LiveData<Pair<Float, Float>> = sensorUsecase.getAndRegister().asLiveData().map {
        val rotation = Pair(previousAzimuth, it.value)
        previousAzimuth = it.value
        rotation
    }

    /*
    * LONGITUDE
    * */
    val longitude = object : MutableLiveData<Double?>(0.0) {
        override fun setValue(value: Double?) {
            super.setValue(value)
            Timber.d("Updating longitude")
            updateDestination()
        }
    }
    val longitudeError: LiveData<InputError> = longitude.map {
        when (it) {
            null -> InputError.INVALID_FORMAT
            !in (-180f..180f) -> InputError.OUT_OF_RANGE
            else -> InputError.NONE
        }
    }
    val isLongitudeValid: LiveData<Boolean> = longitudeError.map {
        it == InputError.NONE
    }

    /*
    * LATITUDE
    * */
    val latitude = object : MutableLiveData<Double?>(0.0) {
        override fun setValue(value: Double?) {
            super.setValue(value)
            Timber.d("Updating latitude")
            updateDestination()
        }
    }
    val latitudeError: LiveData<InputError> = latitude.map {
        Timber.d("Edit Lat:$it")
        when (it) {
            null -> InputError.INVALID_FORMAT
            !in (-90f..90f) -> InputError.OUT_OF_RANGE
            else -> InputError.NONE
        }
    }
    val isLatitudeValid: LiveData<Boolean> = latitudeError.map {
        it == InputError.NONE
    }

    /*
    * LATITUDE & LONGITUDE update destination
    * */
    fun updateDestination() {
        viewModelScope.launch(cancellationHandler) {
            val currentLoc = _currentLocation.value
            val lat = if (isLatitudeValid.value == true) latitude.value else null
            val lng = if (isLongitudeValid.value == true) longitude.value else null

            if (isLocationPermissionGranted.value == true
                && currentLoc != null && lat != null && lng != null
            ) {
                Timber.d("Update destination")
                _isCustomAzimuthSet.postValue(true)

                val newDestination = Location("").also {
                    it.latitude = lat
                    it.longitude = lng
                }
                sensorUsecase.setLocationOffset(currentLoc.toDomain(), newDestination.toDomain())
            }
        }
    }

    private val _isCustomAzimuthSet = MutableLiveData(false)
    val isCustomAzimuthSet: LiveData<Boolean> = _isCustomAzimuthSet

    fun resetCustomAzimuth() {
        _isCustomAzimuthSet.value = false
        sensorUsecase.clearLocationOffset()
    }

    /*
    * Location
    * */
    private val _currentLocation = MutableLiveData<Location?>(null)
    val currentLocation: LiveData<Location?> = _currentLocation

    val isLocationPermissionGranted = object : MutableLiveData<Boolean?>(null) {
        override fun setValue(permissionGranted: Boolean?) {
            super.setValue(permissionGranted)
            Timber.d("Location permission changed: $permissionGranted")
            if (permissionGranted == true) {
                viewModelScope.launch(cancellationHandler) {
                    locationUsecase.getAndListenLocation().collect {
                        _currentLocation.postValue(it.toAndroidLocation())
                    }
                }

            }
        }
    }

    fun setWroclaw() {
        latitude.value = 51.107883
        longitude.value = 17.038538
    }

    fun setMountEverest() {
        latitude.value = 27.986065
        longitude.value = 86.922623
    }

    fun setPraga() {
        latitude.value = 50.073658
        longitude.value = 14.418540
    }

    fun setLosAngeles() {
        latitude.value = 34.052235
        longitude.value = -118.243683
    }

    /*
    * Clear
    * */
    override fun onCleared() {
        sensorUsecase.stop()
        super.onCleared()
    }
}
