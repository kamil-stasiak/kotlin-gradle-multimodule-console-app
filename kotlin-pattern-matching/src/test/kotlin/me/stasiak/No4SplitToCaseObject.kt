package me.stasiak

import kotlin.test.Test
import kotlin.test.assertEquals

class No4SplitToCaseObject {
    //  (doseq [n (range 1 101)]
    //  (println
    //    (match [(mod n 3) (mod n 5)]
    //      [0 0] "FizzBuzz"
    //      [0 _] "Fizz"
    //      [_ 0] "Buzz"
    //      :else n)))

    /*
    1. Dodanie typów
     */


    @Test
    fun `simple solution`() {
        val name = "John Doe"
        val match = when {
            name.length > 10 -> "Very long string"
            name.length < 3 -> "Very short string"
            else -> "Other"
        }

        assertEquals("Other", match)
    }

    @Test
    fun `should match other`() {
        val match = match("John Doe") {
            case { length > 10 } then { "Very long string" }
            case { length < 3 } then { "Very short string" }
            case { true } then { "Other" }
        }

        assertEquals("Other", match)
    }

    @Test
    fun `should match first`() {
        val match = match("John Doe, John Doe, John Doe") {
            case { length > 10 }.then { "Very long string" }
            case { length < 3 }.then { "Very short string" }
            case { true }.then { "Other" }
        }

        assertEquals("Very long string", match)
    }

    @Test
    fun `should match second`() {
        val match = match("Jo") {
            case { length > 100 }.then { "Very long string" }
            case { length < 3 }.then { "Very short string" }
            case { true }.then { "Other" }
        }

        assertEquals("Very short string", match)
    }

    fun match(obj: String, init: Matcher.() -> Unit): String {
        val html = Matcher(obj)
        html.init()
        return html.match()
    }

    class Case(private val matcher: Matcher, private val predicate: String.() -> Boolean) {
        infix fun then(comparator: (String) -> String) {
            matcher.cases.add(predicate to comparator)
        }
    }

    class Matcher(private val obj: String) {
        val cases: MutableList<Pair<String.() -> Boolean, (String) -> String>> = mutableListOf()

        fun case(predicate: String.() -> Boolean, result: (String) -> String) {
            cases.add(predicate to result)
        }

        fun case(predicate: String.() -> Boolean): Case {
            return Case(this, predicate)
        }

        fun match(): String {
            return cases
                .first { (predicate, _) -> predicate(obj) }
                .let { (_, result) -> result(obj) }
        }
    }
}
