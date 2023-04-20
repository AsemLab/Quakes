package com.asemlab.quakes.utils

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.databinding.BindingAdapter
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.memory.MemoryCache
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.asemlab.quakes.R
import com.bumptech.glide.Glide
import java.io.FileNotFoundException


@BindingAdapter(value = ["circularSrcRes", "circularSrcUrl"], requireAll = false)
fun ImageView.setCircularSrc(resourceId: Drawable? = null, url: String? = null) {

    url?.let {
        try {
            val inputStream = resources.assets.open("imgs/${it.lowercase()}.png")
            val b = RoundedBitmapDrawableFactory.create(resources, inputStream)
            Glide.with(this.context).load(b).circleCrop().error(R.drawable.ic_globe).into(this)
            inputStream.close()
        } catch (e: FileNotFoundException) {
            Glide.with(this.context).load(R.drawable.ic_globe).circleCrop().into(this)
        }
//        loadSvg(it)
    }

    resourceId?.let {
        Glide.with(this.context).load(it).circleCrop().error(R.drawable.ic_globe).into(this)
    }

    if (resourceId == null && url == null) {
        Glide.with(this.context).load(R.drawable.ic_globe).circleCrop().into(this)
    }

}

fun ImageView.loadSvg(url: String) {
    val imageLoader = ImageLoader.Builder(this.context)
        .components {
            add(SvgDecoder.Factory())
        }
        .memoryCache {
            MemoryCache.Builder(context)
                .maxSizePercent(0.25)
                .build()
        }
        .build()
    val request = ImageRequest.Builder(this.context)
        .data(url)
        .transformations(CircleCropTransformation())
        .size(360, 360)
        .target(this)
        .build()

    imageLoader.enqueue(request)
}

@BindingAdapter("formatMagnitude")
fun TextView.formatMagnitude(mag: Double) {
    val floor = (mag * 100).toInt()
    text = if (floor % 100 == 0)
        "${floor / 100}"
    else
        "${floor / 100.0}"

}

@BindingAdapter("formatTime")
fun TextView.formatTime(time: Long) {
    text = timeAgo(time)

}

@BindingAdapter("colorMagnitude")
fun TextView.colorMagnitude(mag: Double) {
    val tint = when {
        mag < 3.0 -> {
            R.color.green
        }
        mag < 6.0 -> {
            R.color.yellow
        }
        mag < 7.5 -> {
            R.color.red
        }
        else -> {
            R.color.green
        }
    }

    val stateList = ColorStateList(
        arrayOf(
            intArrayOf()
        ),
        intArrayOf(ResourcesCompat.getColor(resources, tint, null))
    )

    backgroundTintList = stateList
}