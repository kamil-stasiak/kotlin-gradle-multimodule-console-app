package me.stasiak

import kotlin.reflect.KProperty
import kotlin.test.Test
import kotlin.test.assertEquals

internal class SimplePropertyDelegate<T>(
    private var value: T,
    private val operations: (Iterable<T>) -> Iterable<T>
) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        operations(listOf(value))
            .firstOrNull()
            ?.also { this.value = it }
    }
}

class No6Delegate {

    class Person() {
        var name: String by SimplePropertyDelegate("") {
            it.filter(String::isNotBlank)
                .filter { a: String -> a.length < 100 }
                .map(String::toUpperCase)
                .tap(::println)
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
