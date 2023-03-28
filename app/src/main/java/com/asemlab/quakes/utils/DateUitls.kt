package com.asemlab.quakes.utils

import java.text.SimpleDateFormat
import java.util.*

val SERVER_DATE_FORMAT = "yyyy-MM-dd"

fun toSimpleDateFormat(date: Date = Date(), timeZone: TimeZone = TimeZone.getDefault()): String {
    val format = SimpleDateFormat(SERVER_DATE_FORMAT).also {
        it.timeZone = timeZone
    }
    return format.format(date)
}