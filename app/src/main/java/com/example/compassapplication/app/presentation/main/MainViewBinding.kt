package com.example.compassapplication.app.presentation.main

import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.example.compassapplication.core.domain.DomainLocation

object MainViewBinding {

    @BindingAdapter("animatedRotation")
    @JvmStatic
    fun View.setRotation(pair: Pair<Float, Float>?) {
        pair?.let {
            val (formDegree, toDegree) = it

            val anim = RotateAnimation(
                -formDegree,
                -toDegree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 500
                repeatCount = 0
                fillAfter = true
            }

            startAnimation(anim)
        }
    }

    @BindingAdapter("android:text")
    @JvmStatic
    fun TextView.setText(value: Float?) {
        when (value) {
            null,
            this.getTextString() -> Unit // Non-op
            else -> this.text = value.toString()
        }
    }

    @InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
    @JvmStatic
    fun TextView.getTextString(): Float? = try {
        this.text.toString().toFloat()
    } catch (e: NumberFormatException) {
        null
    }


    @BindingAdapter("permissionGranted", "locationFound")
    @JvmStatic
    fun View.changeEnableStatus(permissionGranted: Boolean?, locationFound: DomainLocation?) {
        isEnabled = (permissionGranted ?: true) && locationFound != null
    }

    @BindingAdapter("permissionVisibility")
    @JvmStatic
    fun View.changeVisibility(permissionGranted: Boolean?) = when {
        permissionGranted == null -> visibility = View.VISIBLE
        permissionGranted -> visibility = View.GONE
        else -> visibility = View.VISIBLE
    }

    @BindingAdapter("visible")
    @JvmStatic
    fun View.visible(visible: Boolean) {
        visibility = if (visible) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    @BindingAdapter("currentLocation")
    @JvmStatic
    fun TextView.setCurrentLocation(location: DomainLocation?) {
        text = if (location == null) {
            "Searching..."
        } else {
            "${location.lat} / ${location.lng}"
        }
    }
}
