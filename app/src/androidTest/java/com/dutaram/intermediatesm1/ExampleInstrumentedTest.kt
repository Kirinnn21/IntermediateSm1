package com.dutaram.intermediatesm1

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

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
        assertEquals("com.dutaram.intermediatesm1", appContext.packageName)
    }

    @Test
    fun testAppResources() {
        // Accessing resources example
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val res = appContext.resources
        assertNotNull(res)
        assertNotNull(res.getString(R.string.app_name))
        assertNotNull(res.getDrawable(R.mipmap.ic_launcher))
    }

    @Test
    fun testIntent() {
        // Example of testing intents
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = appContext.packageManager.getLaunchIntentForPackage(appContext.packageName)
        assertNotNull(intent)
    }
}
