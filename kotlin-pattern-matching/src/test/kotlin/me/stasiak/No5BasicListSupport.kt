package me.stasiak

import kotlin.test.Test
import kotlin.test.assertEquals

class No5BasicListSupport {
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

    private val secretCombination = listOf(5, 2, 1, 3)

    @Test
    fun `simple solution`() {
        val num = secretCombination
        val match = when {
            num[0] == 5 && num[1] == 5 && num[2] == 5 && num[3] == 5 -> "Tiger"
            num[0] == 5 && num[1] == 2 && num[2] == 0 && num[3] == 5 -> "Horse"
            num[0] == 5 && num[1] == 2 && num[2] == 1 -> "Dog"
            else -> "Other"
        }

        assertEquals("Dog", match)
    }

    @Test
    fun `should match third, shorter (Dog)`() {
        val match = match(secretCombination) {
            case(listOf(5, 5, 5, 5)).then { "Tiger" }
            case(listOf(5, 2, 0, 5)).then { "Horse" }
            case(listOf(5, 2, 1)).then { "Dog" }
            case(listOf()).then { "Other" }
        }

        assertEquals("Dog", match)
    }


    @Test
    fun `should match last, empty (Other)`() {
        val match = match(secretCombination) {
            case(listOf(5, 5, 5, 5)).then { "Tiger" }
            case(listOf(5, 2, 0, 5)).then { "Horse" }
            case(listOf(5, 2, 2)).then { "Dog" }
            case(listOf()).then { "Other" }
        }

        assertEquals("Other", match)
    }


    @Test
    fun `should match first, long (Tiger)`() {
        val match = match(secretCombination) {
            case(listOf(5, 5, 1, 3)).then { "Tiger" }
            case(listOf(5, 2, 0, 5)).then { "Horse" }
            case(listOf(5, 2, 2)).then { "Dog" }
            case(listOf()).then { "Other" }
        }

        assertEquals("Other", match)
    }

    private fun match(obj: List<Int>, init: Matcher.() -> Unit): String {
        val html = Matcher(obj)
        html.init()
        return html.match()
    }

    class Case(
        private val matcher: Matcher,
        private val predicate: List<Int>
    ) {
        infix fun then(comparator: (List<Int>) -> String) {
            matcher.cases.add(predicate to comparator)
        }
    }

    class Matcher(private val obj: List<Int>) {
        val cases: MutableList<Pair<List<Int>, (List<Int>) -> String>> = mutableListOf()

        fun case(predicate: List<Int>): Case {
            return Case(this, predicate)
        }

//        fun case(predicate: String.() -> Boolean): Case {
//            return Case(this, predicate)
//        }

//        fun case(predicate: List.() -> Boolean): Case {
//            return Case(this, predicate)
//        }

        fun match(): String {
            return cases
                .first { (predicate, _) ->
                    predicate.zip(obj)
                        .all { (pred, obj) -> pred == obj }
                }
                .let { (_, result) -> result(obj) }
        }
    }
}
