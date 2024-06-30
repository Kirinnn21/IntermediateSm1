package com.dutaram.intermediatesm1

import org.junit.Test
import org.junit.Assert.*

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun multiplication_isCorrect() {
        assertEquals(8, 4 * 2)
    }

    @Test
    fun stringConcatenation_isCorrect() {
        val result = "Hello, " + "World!"
        assertEquals("Hello, World!", result)
    }

    @Test
    fun checkStringLength() {
        val str = "Testing"
        assertEquals(7, str.length)
    }
}
