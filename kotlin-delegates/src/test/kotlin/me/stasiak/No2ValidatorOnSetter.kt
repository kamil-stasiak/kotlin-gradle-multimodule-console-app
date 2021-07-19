package me.stasiak

import java.lang.IllegalStateException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class No2ValidatorOnSetter {
    class Person() {
        var age: Int = 0
            set(value) {
                if (value < 0) {
                    throw IllegalStateException()
                }
                field = value
            }
    }

    @Test
    fun `setter should throw exception`() {
        assertFails {
            Person().apply {
                age = -1
            }
        }
    }

    @Test
    fun `setter should set value`() {
        val person = Person().apply { age = 1 }
        assertEquals(1, person.age, "Age should be equal to 1")
    }
}
