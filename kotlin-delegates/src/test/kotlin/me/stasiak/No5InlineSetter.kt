package me.stasiak

import kotlin.test.Test
import kotlin.test.assertEquals

class No5InlineSetter {

    // vetoable
    class Person() {
        // 4 operation
        var name: String = ""
            set(value) {
                listOf(value)
                    .filter(String::isNotBlank)
                    .filter { a: String -> a.length < 100 }
                    .map(String::toUpperCase)
                    .tap { println(it) }
                    .firstOrNull()
                    ?.also {
                        field = it
                    }
            }
    }

    @Test
    fun `setter should ignore blank name`() {
        val person = Person().apply {
            name = "JOHN"
            name = ""
        }

        assertEquals("JOHN", person.name)
    }

    @Test
    fun `setter should ignore long name`() {
        val person = Person().apply {
            name = longName()
        }

        assertEquals("", person.name)
    }

    @Test
    fun `setter should uppercase name`() {
        val person = Person().apply {
            name = "john"
        }

        assertEquals("JOHN", person.name)
    }

    private fun longName() = "a".repeat(110)
}
