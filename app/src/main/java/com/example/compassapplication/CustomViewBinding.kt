package com.example.compassapplication

import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import timber.log.Timber

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
}