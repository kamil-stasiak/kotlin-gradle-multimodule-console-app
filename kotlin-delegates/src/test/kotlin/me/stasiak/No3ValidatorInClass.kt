package me.stasiak

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class No3ValidatorInClass {

    class Person() {
        var age: Int = 0
            set(value) {
                Validators.ageIsPositive(value)
                field = value
            }

        private object Validators {
            fun ageIsPositive(value: Int) {
                if (value < 0) throw IllegalStateException()
            }
        }
    }

    @Test
    fun `setter should throw exception (Person3)`() {
        assertFails { Person().apply { age = -1 } }
    }

    @Test
    fun `setter should set value (Person3)`() {
        val person = Person().apply {
            age = 1
        }

        assertEquals(1, person.age, "Age should be equal to 1")
    }
}
