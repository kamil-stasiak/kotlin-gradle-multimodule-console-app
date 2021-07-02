package me.stasiak

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PersonTest {
    @Test
    fun testPersonHasAGreeting() {
        val classUnderTest = Person()
        assertEquals(classUnderTest.sayHello(), "Hello", "Person should have a greeting")
    }
}
