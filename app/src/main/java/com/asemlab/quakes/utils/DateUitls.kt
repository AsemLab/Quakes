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