package me.stasiak

import java.lang.IllegalStateException
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails


//class ToUpperCaseDelegate() {
//    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
//        println("$value has been assigned to '${property.name}' in $thisRef. ($name)")
//    }
//}

// ReadWriteProperty<Any?, T>


class LoggerDelegate(val name: String) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "$thisRef, thank you for delegating '${property.name}' to me! ($name)"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("$value has been assigned to '${property.name}' in $thisRef. ($name)")
    }
}

class ManyDelegates(val delagates: List<LoggerDelegate>) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "$thisRef, thank you for delegating '${property.name}' to me!"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        delagates.forEach {
            it.setValue(thisRef, property, value)
        }
    }
}


/**
 * # Very important paragraph
 * Some text, but with *cursive*.
 *
 * > Quote: Lorem Ipsum solor
 */
class ManyDelegates2<T>(var initialValue: T, val delagates: List<ReadWriteProperty<Any?, T>>) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return initialValue;
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        // todo use fold function because you need invoke set value with result of previous one
        val fold = delagates.fold(value) { lastValue, current ->
            current.setValue(thisRef, property, lastValue)
            current.getValue(thisRef, property)
        }
    }
}

/**
 * Some text **Elloo**
 */
class Person() {
    // logger for getter and setter
    var name: String by ManyDelegates(listOf(LoggerDelegate("1"), LoggerDelegate("2")))

    // lazy for getter
    val surname: String by lazy {
        println("Lazy!")
        "Stasiak"
    }

    /**
     * # Some big text
     * ## Some big text
     * ### Some big text
     * Paragraph
     */
    var max by Delegates.vetoable(0) { property, oldValue, newValue ->
        newValue > oldValue
    }

    var max2 by ManyDelegates2(
        0,
        listOf(
            // next value should be greater then previous
            Delegates.vetoable(0) { property, oldValue, newValue ->
                newValue > oldValue
            },
            // next value should be greater than previous + 5
            Delegates.vetoable(0) { property, oldValue, newValue ->
                newValue > (oldValue + 5)
            }
        )
    )
}

class DelegatesTest {

    @Test
    fun `logger delegate`() {
        val person = Person()
        person.name = "Kamil"
        person.name = "Kamil 2"


        println("Hello")
    }

    @Test
    fun `vetoable delegate`() {
        val person = Person()

        person.max = 1
        println("After 0 -> 1: " + person.max)

        person.max = 10
        println("After 1 -> 10: " + person.max)

        person.max = 4
        println("After 10 -> 4: " + person.max)

        println("Hello")
    }

    @Test
    fun `many delegate`() {
        val person = Person()

        person.max2 = 10
        println("After 0 -> 10: " + person.max2)

        person.max2 = 11
        println("After 10 -> 11: " + person.max2)

//        person.max2 = 4
//        println("After 10 -> 4: " + person.max2)

        println("Hello")
    }


    class Person2() {
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
            Person2().apply {
                age = -1
            }
        }
    }

    @Test
    fun `setter should set value`() {
        val person = Person2().apply {
            age = 1
        }

        assertEquals(1, person.age, "Age should be equal to 1")
    }
}
