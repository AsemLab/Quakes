package com.asemlab.quakes.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.widget.Toast

fun makeToast(context: Context, m: String) {
    Toast.makeText(context, m, Toast.LENGTH_SHORT).show()
}

fun isNightModeOn(): Boolean {
    val nightModeFlags = Resources.getSystem().configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK
    return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
}