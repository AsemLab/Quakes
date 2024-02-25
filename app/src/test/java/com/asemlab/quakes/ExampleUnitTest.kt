package com.asemlab.quakes

import com.asemlab.quakes.utils.toServerDateFormat
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
        val s = Date(1679864400000).toServerDateFormat()
        assertEquals("2023-03-27", s)
    }
}