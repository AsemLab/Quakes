package com.asemlab.quakes

import com.asemlab.quakes.utils.toSimpleDateFormat
import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val s = toSimpleDateFormat(Date(1679864400000))
        assertEquals("2023-03-27", s)
    }
}