package com.asemlab.quakes.utils

import java.text.SimpleDateFormat
import java.util.*

const val SERVER_DATE_FORMAT = "yyyy-MM-dd"
const val RANGE_DATE_FORMAT = "dd/MM/yyyy"
const val DATE_TIME_FORMAT = "dd MMM,  yy \t hh:mm aa"
const val TIME_FORMAT = "hh:mm aa"

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

fun timeAgo(time: Long): String {
    val now = Date()
    val then = Date(time)

    val duration = now.time - then.time
    return if (duration < 86_400_000L) {
        if (duration < 3_600_000L) {
            "${duration / 1000 / 60} minutes ago"
        } else {
            "${duration / 3_600_000L} hour${if (duration / 3_600_000L > 1) "s" else ""} ago"
        }
    } else {
        then.toSimpleDateFormat()
    }
}

fun Date.toDetailedDateFormat(timeZone: TimeZone = TimeZone.getDefault()): String {
    val format = SimpleDateFormat(DATE_TIME_FORMAT).also {
        it.timeZone = timeZone
    }
    return format.format(this)
}

fun Date.toTimeString(timeZone: TimeZone = TimeZone.getDefault()): String {
    val format = SimpleDateFormat(TIME_FORMAT).also {
        it.timeZone = timeZone
    }
    return format.format(this)
}