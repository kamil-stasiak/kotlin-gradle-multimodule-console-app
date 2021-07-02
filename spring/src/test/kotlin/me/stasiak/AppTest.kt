package me.stasiak

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AppTest {

    @Test
    fun testAppHasAGreeting() {
        val classUnderTest = App()
        assertNotNull(classUnderTest.greeting, "app should have a greeting")
    }

    @Test
    fun testPersonHasAGreeting() {
        val classUnderTest = Person()
        assertEquals(classUnderTest.sayHello(), "Hello", "Person should have a greeting")
    }
}
