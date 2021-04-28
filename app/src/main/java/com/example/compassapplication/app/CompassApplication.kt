package com.example.compassapplication.app

import android.app.Application
import android.content.Context
import android.hardware.SensorManager
import android.location.LocationManager
import com.example.compassapplication.BuildConfig
import com.example.compassapplication.app.framework.LocationSourceImpl
import com.example.compassapplication.app.framework.SensorInterpreterImpl
import com.example.compassapplication.app.framework.SensorSourceImpl
import com.example.compassapplication.app.presentation.main.MainViewModel
import com.example.compassapplication.app.presentation.main.MainViewModelFactory
import com.example.compassapplication.core.data.LocationSource
import com.example.compassapplication.core.data.SensorInterpreter
import com.example.compassapplication.core.data.SensorSource
import com.example.compassapplication.core.usecases.LocationUsecase
import com.example.compassapplication.core.usecases.LocationUsecaseImpl
import com.example.compassapplication.core.usecases.SensorUsecase
import com.example.compassapplication.core.usecases.SensorUsecaseImpl
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import timber.log.Timber

class CompassApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        startKoin {
            // declare used Android context
            androidContext(this@CompassApplication)
            // declare modules
            modules(
                listOf(
                    sensorModule,
                    locationModule,
                    viewModule
                )
            )
        }
    }

    private val sensorModule = module {
        factory { applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
        factory<SensorSource> { SensorSourceImpl(get()) }
        single<SensorInterpreter> { SensorInterpreterImpl() }
        single<SensorUsecase> { (SensorUsecaseImpl(get())) }
    }

    private val locationModule = module {
        factory { applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager }
        factory {
            LocationServices.getFusedLocationProviderClient(applicationContext)
                as FusedLocationProviderClient
        }
        factory<LocationSource> { LocationSourceImpl(get(), get()) }
        single<LocationUsecase> { LocationUsecaseImpl(get()) }
    }

    private val viewModule: Module = module {
        factory { MainViewModelFactory(get(), get(), get()) }
        viewModel { MainViewModel(get(), get(), get()) }
    }
}
