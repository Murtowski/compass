package com.example.compassapplication

import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.databinding.BindingAdapter

object CustomViewBinding {

    @BindingAdapter("animatedRotation")
    @JvmStatic fun View.setRotation(pair: Pair<Float, Float>?){
        val (formDegree, toDegree) = pair ?: return

        val anim = RotateAnimation(
            -formDegree,
            -toDegree,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f)
            .also {
                it.duration = 500
                it.repeatCount = 0
                it.fillAfter = true
            }

        this.startAnimation(anim)

    }
}