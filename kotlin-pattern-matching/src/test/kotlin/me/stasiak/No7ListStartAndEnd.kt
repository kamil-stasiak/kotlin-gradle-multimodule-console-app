package me.stasiak

import kotlin.test.Test
import kotlin.test.assertEquals

class No7ListStartAndEnd {
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
            start(5, 5, 5, 5).then { "Tiger" }
            start(5, 2, 0, 5).then { "Horse" }
            start(5, 2, 1).then { "Dog" }
            start().then { "Other" }
        }

        assertEquals("Dog", match)
    }


    @Test
    fun `should match last, empty (Other)`() {
        val match = match(secretCombination) {
            start(5, 5, 5, 5).then { "Tiger" }
            start(5, 2, 0, 5).then { "Horse" }
            start(5, 2, 2).then { "Dog" }
            start().then { "Other" }
        }

        assertEquals("Other", match)
    }


    @Test
    fun `should match first, long (Tiger)`() {
        val match = match(secretCombination) {
            start(5, 2, 1, 3).then { "Tiger" }
            start(5, 2, 0, 5).then { "Horse" }
            start(5, 2, 2).then { "Dog" }
            start().then { "Other" }
        }

        assertEquals("Tiger", match)
    }


    @Test
    fun `should third (Dog)`() {
        val match = match(secretCombination) {
            end(2, 2).then { "Tiger" }
            end(2, 0, 5).then { "Horse" }
            end(2, 1, 3).then { "Dog" }
            end().then { "Other" }
        }

        assertEquals("Dog", match)
    }

    @Test
    fun `should match first (Dog)`() {
        val match = match(secretCombination) {
            end(3).then { "Tiger" }
            end(2, 0, 5).then { "Horse" }
            end(2, 1, 3).then { "Dog" }
            end().then { "Other" }
        }

        assertEquals("Tiger", match)
    }

    private fun match(obj: List<Int>, init: Matcher.() -> Unit): String {
        val html = Matcher(obj)
        html.init()
        return html.match()
    }

    interface Case {
        fun test(): Boolean
        fun result(): String
    }


    class CaseStart(
        private val matcher: Matcher,
        private val subjects: List<Int>,
        private val predicates: List<Int>
    ) : Case {
        lateinit var result: (List<Int>) -> String

        infix fun then(result: (List<Int>) -> String) {
            this.result = result
            matcher.cases.add(this)
        }

        override fun test(): Boolean {
            return predicates.zip(subjects).all { (predicate, subject) -> predicate == subject }
        }

        override fun result(): String {
            return result(subjects)
        }
    }

    class CaseEnd(
        private val matcher: Matcher,
        private val subjects: List<Int>,
        private val predicates: List<Int>
    ) : Case {

        lateinit var result: (List<Int>) -> String
        infix fun then(result: (List<Int>) -> String) {
            this.result = result
            matcher.cases.add(this)
        }

        override fun test(): Boolean {
            return predicates.reversed().zip(subjects.reversed()).all { (predicate, subject) -> predicate == subject }
        }

        override fun result(): String {
            return result(subjects)
        }
    }

    class Matcher(val obj: List<Int>) {
        val cases: MutableList<Case> = mutableListOf()

        fun match(): String {
            return cases
                .first { it.test() }.result()
        }
    }

    fun Matcher.start(vararg predicate: Int): CaseStart {
        return CaseStart(this, this.obj, predicate.toList())
    }

    fun Matcher.end(vararg predicate: Int): CaseEnd {
        return CaseEnd(this, this.obj, predicate.toList())
    }
}
