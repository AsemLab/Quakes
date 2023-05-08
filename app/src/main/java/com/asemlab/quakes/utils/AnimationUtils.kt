package com.asemlab.quakes.utils

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View

fun View.slideDown(height: Int) {
    val animator =
        ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, translationY, height.toFloat()).apply {
            duration = 500
            repeatCount = 0
        }
    animator.start()
}

fun View.slideUp() {
    val animator =
        ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, translationY, 0f).apply {
            duration = 500
            repeatCount = 0
        }
    animator.start()
}

fun fadeOut(view: View, onEnd: () -> Unit) {
    val animator = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f).apply {
        duration = 500
        repeatCount = 0
    }
    animator.apply {
        addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                onEnd()
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }

        })
        start()
    }
}

fun View.slideDownAndFadeOut(height: Int) {
    val tY = PropertyValuesHolder.ofFloat("translationY", translationY, height.toFloat())
    val fadeOut = PropertyValuesHolder.ofFloat("alpha", 1f, 0f)
    val animator = ObjectAnimator.ofPropertyValuesHolder(this, tY, fadeOut).apply {
        duration = 500
        repeatCount = 0
    }
    animator.start()
}

fun View.slideUpAndFadeIn() {
    val tY = PropertyValuesHolder.ofFloat("translationY", translationY, 0f)
    val fadeOut = PropertyValuesHolder.ofFloat("alpha", 0f, 1f)
    val animator = ObjectAnimator.ofPropertyValuesHolder(this, tY, fadeOut).apply {
        duration = 500
        repeatCount = 0
    }
    animator.start()
}