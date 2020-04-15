package com.example.compassapplication.app.presentation.utils

import android.location.Location
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter


object CustomViewBinding {

    @BindingAdapter("animatedRotation")
    @JvmStatic fun View.setRotation(pair: Pair<Float, Float>?){
        pair?.let {
            val (formDegree, toDegree) = it

            val anim = RotateAnimation(
                -formDegree,
                -toDegree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f)
                .apply{
                    duration = 500
                    repeatCount = 0
                    fillAfter = true
                }

            this.startAnimation(anim)
        }
    }

    @BindingAdapter("android:text")
    @JvmStatic fun TextView.setText(value: Double?) {
        if (value == null ) return
        if (value == this.getTextString()) return
        this.text = value.toString()
    }

    @InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
    @JvmStatic fun TextView.getTextString(): Double? {
        return try {
            this.text.toString().toDouble()
        }catch (e: NumberFormatException){
            null
        }
    }

    @BindingAdapter("permissionGranted", "locationFound")
    @JvmStatic fun View.changeEnableStatus(permissionGranted: Boolean?, locationFound: Location?) {
        isEnabled = (permissionGranted ?: true) && locationFound != null
    }

    @BindingAdapter("permissionVisibility")
    @JvmStatic fun View.changeVisibility(permissionGranted: Boolean?){
        if(permissionGranted == null ) visibility = View.VISIBLE
        else if(permissionGranted) visibility = View.GONE
        else visibility = View.VISIBLE
    }

    @BindingAdapter("visible")
    @JvmStatic fun View.visible(visibile: Boolean){
        if(visibile)
            visibility = View.VISIBLE
        else
            visibility = View.INVISIBLE
    }

    @BindingAdapter("currentLocation")
    @JvmStatic fun TextView.setCurrentLocation(location: Location?){
        if(location == null) text = "Searching..."
        else text = "${location.latitude} / ${location.longitude}"
    }
}