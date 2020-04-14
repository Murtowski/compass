package com.example.compassapplication

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.compassapplication.util.MainCoroutineRule
import com.example.compassapplication.util.getOrAwaitValue
import com.example.compassapplication.util.observeForTesting
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    // Run tasks synchronously
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: MainViewModel

    private val azimuthValues = (1..10)

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)

        val sensorUsecase = spyk<SensorUsecase>()
        every { sensorUsecase.getAndRegister() } returns azimuthValues.asFlow().map { it.toFloat() }

        val locationUsecase = mockk<LocationUsecase>()
        every { locationUsecase.getAndListenLocation() } returns flow { emit(Location("")) }

        viewModel = MainViewModel(sensorUsecase, locationUsecase)
    }

    @Test
    fun `test lat validation`(){
        viewModel.latitude.value = null // Biding adapter returns null if it cannot parse

        assert(viewModel.latitudeError.getOrAwaitValue() == InputError.INVALID_FORMAT)
        assert(viewModel.isLatitudeValid.getOrAwaitValue() == false)

        viewModel.latitude.value = -99.0
        assert(viewModel.isLatitudeValid.getOrAwaitValue() == false)

        viewModel.latitude.value = 0.0
        assert(viewModel.isLatitudeValid.getOrAwaitValue() == true)
        assert(viewModel.latitude.getOrAwaitValue() != null)
    }

    @Test
    fun `test lng validation`(){
        viewModel.longitude.value = null
        assert(viewModel.isLongitudeValid.getOrAwaitValue() == false)

        viewModel.longitude.value = 0.0

        assert(viewModel.isLongitudeValid.getOrAwaitValue() == true)
        assert(viewModel.longitude.getOrAwaitValue() != null)
    }

    @Test
    fun `test update location`(){
        assert(viewModel.currentLocation.getOrAwaitValue() == null)

        viewModel.isLocationPermissionGranted.value = true
        viewModel.isLocationPermissionGranted.getOrAwaitValue()

        assert(viewModel.currentLocation.getOrAwaitValue() != null)
    }

    @Test
    fun `test update azimuth`(){
        assert(viewModel.isCustomAzimuthSet.getOrAwaitValue() == false)

        `test update location`()
        `test lat validation`()
        `test lng validation`()

        viewModel.updateDestination()

        assert(viewModel.isCustomAzimuthSet.getOrAwaitValue() == true){
            println("latValid: ${viewModel.isLatitudeValid.getOrAwaitValue()}")
            println("lngValid: ${viewModel.isLongitudeValid.getOrAwaitValue()}")
            println("loc: ${viewModel.currentLocation.getOrAwaitValue() != null}")
            println("permision: ${viewModel.isLocationPermissionGranted.getOrAwaitValue()}")
        }

        viewModel.resetCustomAzimuth()

        assert(viewModel.isCustomAzimuthSet.getOrAwaitValue() == false)
    }




}