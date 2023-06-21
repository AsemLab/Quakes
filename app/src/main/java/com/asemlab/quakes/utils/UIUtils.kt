package com.asemlab.quakes.utils

import android.content.Context
import android.widget.Toast

fun makeToast(context: Context, m: String) {
    Toast.makeText(context, m, Toast.LENGTH_SHORT).show()
}