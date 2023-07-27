package com.asemlab.quakes.utils

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View

fun View.slideDown(height: Int, delay: Long = 0) {
    val animator =
        ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, translationY, height.toFloat()).apply {
            duration = 500
            repeatCount = 0
            startDelay = delay
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

fun View.slideDownAndFadeOut(height: Int, delay: Long = 0) {
    val tY = PropertyValuesHolder.ofFloat("translationY", translationY, height.toFloat())
    val fadeOut = PropertyValuesHolder.ofFloat("alpha", 1f, 0f)
    val animator = ObjectAnimator.ofPropertyValuesHolder(this, tY, fadeOut).apply {
        duration = 500
        repeatCount = 0
        startDelay = delay
    }
    animator.start()
}

fun View.slideUpAndFadeIn(delay: Long = 0) {
    val tY = PropertyValuesHolder.ofFloat("translationY", translationY, 0f)
    val fadeOut = PropertyValuesHolder.ofFloat("alpha", 0f, 1f)
    val animator = ObjectAnimator.ofPropertyValuesHolder(this, tY, fadeOut).apply {
        duration = 500
        repeatCount = 0
        startDelay = delay
    }
    animator.start()
}

fun View.animateHeight(delay: Long = 0) {
    val tY = ObjectAnimator.ofInt(measuredHeight, measuredHeight + 500).apply {
        duration = 500
        repeatCount = 0
        startDelay = delay
        addUpdateListener {
            val l = it.animatedValue as Int
            val layoutParams = layoutParams
            layoutParams.height = l

            this@animateHeight.layoutParams = layoutParams
        }
    }
    tY.start()
}