package me.stasiak

import kotlin.test.Test
import kotlin.test.assertEquals

class No2FirstPrototype {
    //  (doseq [n (range 1 101)]
    //  (println
    //    (match [(mod n 3) (mod n 5)]
    //      [0 0] "FizzBuzz"
    //      [0 _] "Fizz"
    //      [_ 0] "Buzz"
    //      :else n)))
    @Test
    fun `should match other`() {
        val match = match("John Doe") {
            case({ it.length > 100 }, { "Very long string" })
            case({ it.length < 3 }, { "Very short string" })
            case({ true }, { "Other" })
        }

        assertEquals("Other", match)
    }

    @Test
    fun `should match first`() {
        val match = match("John Doe, John Doe, John Doe") {
            case({ it.length > 10 }, { "Very long string" })
            case({ it.length < 3 }, { "Very short string" })
            case({ true }, { "Other" })
        }

        assertEquals("Very long string", match)
    }

    @Test
    fun `should match second`() {
        val match = match("Jo") {
            case({ it.length > 10 }, { "Very long string" })
            case({ it.length < 3 }, { "Very short string" })
            case({ true }, { "Other" })
        }

        assertEquals("Very short string", match)
    }

    fun match(obj: String, init: Matcher.() -> Unit): String {
        val html = Matcher(obj)
        html.init()
        return html.match()
    }

    class Matcher(private val obj: String) {
        val cases: MutableList<Pair<(String) -> Boolean, (String) -> String>> = mutableListOf()

        fun case(predicate: (String) -> Boolean, result: (String) -> String) {
            cases.add(predicate to result)
        }

        fun match(): String {
            return cases
                .first { (predicate, _) -> predicate(obj) }
                .let { (_, result) -> result(obj) }
        }
    }
}
