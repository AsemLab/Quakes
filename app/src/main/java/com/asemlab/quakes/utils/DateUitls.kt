package com.asemlab.quakes.utils

import java.text.SimpleDateFormat
import java.util.*

val SERVER_DATE_FORMAT = "yyyy-MM-dd"

fun Date.toSimpleDateFormat(timeZone: TimeZone = TimeZone.getDefault()): String {
    val format = SimpleDateFormat(SERVER_DATE_FORMAT).also {
        it.timeZone = timeZone
    }
    return format.format(this)
}

fun tomorrowDate(): Date {
    return Calendar.getInstance().apply {
        add(Calendar.DAY_OF_MONTH, 1)
    }.time
}