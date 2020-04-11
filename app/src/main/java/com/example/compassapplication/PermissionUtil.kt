package com.example.compassapplication

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object PermissionUtil {

    const val LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION

    fun isPermissionGranted(context: Context, permission: String): Boolean{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        } else {
           true
        }
    }

    fun isLocationPermissionGranted(context: Context): Boolean{
        return isPermissionGranted(context, LOCATION_PERMISSION)
    }

    fun justifyAskingForPermission(permissionsArray: Array<String>,
                                   activity: Activity,
                                   requestPermission: (permissions: Array<String>) -> Unit){
        if(permissionsArray.isEmpty()) return

        val messageBuild = StringBuilder()

        if(permissionsArray.contains(LOCATION_PERMISSION)) {
            messageBuild.append("Location permission is required to count azimuth")
        }


        MaterialAlertDialogBuilder(activity)
            .setTitle("Permission explanation")
            .setMessage(messageBuild.toString())
            .setPositiveButton("Understand") { _, _ -> requestPermission(permissionsArray)}
            .setCancelable(false)
            .show()
    }

    fun justifyAskingForLocationPermission(activity: Activity, requestPermission: (permissions: Array<String>) -> Unit){
        justifyAskingForPermission(arrayOf(LOCATION_PERMISSION), activity, requestPermission)
    }
}