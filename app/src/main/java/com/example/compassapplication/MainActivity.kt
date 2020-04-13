package com.example.compassapplication

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import com.example.compassapplication.databinding.ActivityMainBinding
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.jar.Manifest


class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_LOCATION = 123

    private lateinit var binding: ActivityMainBinding

    private val viewModelFactory: MainViewModelFactory by inject()
    private val viewModel by  viewModels<MainViewModel> { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        requestPermission()
    }

    private fun requestPermission(){
        if(PermissionUtil.isLocationPermissionGranted(this)){
            Timber.d("Permissions Granted")
            viewModel.isLocationPermissionGranted.value = true
        }else {
            viewModel.isLocationPermissionGranted.value = false
            PermissionUtil.justifyAskingForLocationPermission(this) { permissions ->
                ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    REQUEST_CODE_LOCATION
                );
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> requestPermission()
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}
