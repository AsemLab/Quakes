package com.asemlab.quakes

import com.asemlab.quakes.utils.timeAgo
import org.junit.Assert
import org.junit.Test

class DateUtilsTest {

    @Test
    fun testMinutes() {

        val t = timeAgo(1681724100000)
        println(t)
        Assert.assertFalse(t.startsWith("-"))
    }

    @Test
    fun testHours() {

        val t = timeAgo(1681717140000)
        println(t)
        Assert.assertFalse(t.startsWith("-"))
    }

    @Test
    fun testMoreDay() {

        val t = timeAgo(1681641480000)
        println(t)
        Assert.assertFalse(t.startsWith("-"))
    }
}