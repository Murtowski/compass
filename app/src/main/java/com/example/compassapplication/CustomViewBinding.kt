package com.example.compassapplication

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
    @JvmStatic fun TextView.setText(value: Float?) {
        if (value == null) return
        this.text = value.toString()
    }

    @InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
    @JvmStatic fun TextView.getTextString(): Float? {
        return java.lang.Float.valueOf(this.text.toString())
    }
}