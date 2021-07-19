package me.stasiak

import java.util.function.Predicate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class No4VerboseSetter {

    // vetoable
    class Person() {
        // 4 operation
        var name: String = ""
            set(value) {
                val isNotBlank: (String) -> Boolean = { a: String -> a.isNotBlank() }
                val hasProperLength: (String) -> Boolean = { a: String -> a.length < 100 }
                val mapOf: Map<(String) -> Boolean, (String) -> String> = mapOf(
                    isNotBlank to { a: String -> a }, // filter, identity
                    hasProperLength to { a: String -> a }, // filter, identity
                    { _: String -> true } to { a: String -> a.toUpperCase() }, // map
                    { _: String -> true } to { a: String ->
                        println(a)
                        a
                    } // peek
                )


                val all = mapOf.all { (predicate, _) -> predicate(value) }
                if (all) {
                    field = mapOf
                        .values
                        .fold(value) { acc, current ->
                            current(acc)
                        }
                }
            }

        private object Validators {
            fun ageIsPositive(value: Int) {
                if (value < 0) throw IllegalStateException()
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
