package com.asemlab.quakes

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.asemlab.quakes.database.models.UsaStateData
import com.asemlab.quakes.database.typeconverters.UsaStateConverter
import org.json.JSONArray

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.asemlab.quakes", appContext.packageName)
    }

    @Test
    fun insertUsaStates() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val fileInString: String = appContext.assets.open("usa_states.json").bufferedReader().readText()
        val jsonArray = JSONArray(fileInString)
        val usaStateConverter = UsaStateConverter()
        val l = mutableListOf<UsaStateData?>()
        for (i in 0 until jsonArray.length()) {
            l.add(usaStateConverter.toUsaState(jsonArray[i].toString()))
        }

        println(l)
        assertEquals(50, l.size)
    }
}