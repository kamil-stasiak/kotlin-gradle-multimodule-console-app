package me.stasiak

import kotlin.test.Test
import kotlin.test.assertEquals

class No10Generics {
    private val `1 to 9` = (1..9).toList() // listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)


    @Test
    fun `should match List of Strings`() {

        val result = match(listOf("a", "b", "c")) {
            end["z"]
                .then { "end with: z" }

            exact["a", "b", "c", "d"]
                .then { "exact: a, b, c, d" }

            start["a", "b", "c"]
                .then { "Start with: a, b, c" }

            otherwise { "Default" }
        }

        assertEquals("Start with: a, b, c", result)
    }


    @Test
    fun `should match start`() {

        val result = match(`1 to 9`) {
            end[7, 8, 9, 10]
                .then { "end with: 7, 8, 9, 10" }

            exact[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
                .then { "exact: 1, 2, 3, 4, 5, 6, 7, 8, 9" }

            start[1, 2, 3]
                .then { "Start with: 1, 2, 3" }

        }

        assertEquals("Start with: 1, 2, 3", result)
    }


    @Test
    fun `should match start and end`() {
        val result = match(`1 to 9`) {
            exact[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
                .then { "exact: 1, 2, 3, 4, 5, 6, 7, 8, 9" }

            (start[1, 2, 3] and end[7, 8, 9])
                .then { "Start with: 1, 2, 3; End with: 7, 8, 9" }

            otherwise { "Default..." }
        }

        assertEquals("Start with: 1, 2, 3; End with: 7, 8, 9", result)
    }


    @Test
    fun `should match otherwise`() {
        val result = match(`1 to 9`) {
            end[7, 8, 9, 10]
                .then { "end with: 7, 8, 9, 10" }

            exact[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
                .then { "exact: 1, 2, 3, 4, 5, 6, 7, 8, 9" }

            start[0, 1, 2, 3]
                .then { "Start with: 1, 2, 3" }

            otherwise { "Default..." }
        }

        assertEquals("Default...", result)
    }

    private fun <T> match(obj: List<T>, init: Matcher<T>.() -> Unit): String {
        val matcher = Matcher(obj)
        matcher.init()
        return matcher.match()
    }

    interface Case<T> {
        fun test(subjects: List<T>): Boolean
        val matcher: Matcher<T>
    }

    class TwoCases<T>(override val matcher: Matcher<T>, val left: Case<T>, val right: Case<T>) : Case<T> {
        override fun test(subjects: List<T>): Boolean {
            return left.test(subjects) && right.test(subjects)
        }
    }

    class CaseOtherwise<T>(override val matcher: Matcher<T>) : Case<T> {
        override fun test(subjects: List<T>): Boolean = true

    }

    class CaseStart<T>(
        override val matcher: Matcher<T>,
        private val predicates: List<T>
    ) : Case<T> {
        override fun test(subjects: List<T>): Boolean {
            return predicates.zip(subjects).all { (predicate, subject) -> predicate == subject }
        }
    }

    class CaseEnd<T>(
        override val matcher: Matcher<T>,
        private val predicates: List<T>
    ) : Case<T> {
        override fun test(subjects: List<T>): Boolean {
            val reversedFirst = predicates.reversed()
            val reversedOther = subjects.reversed()
            return reversedFirst
                .zip(reversedOther)
                .all { (predicate, subject) -> predicate == subject }
        }
    }

    class CaseExact<T>(
        override val matcher: Matcher<T>,
        private val predicates: List<T>
    ) : Case<T> {
        override fun test(subjects: List<T>): Boolean {
            return predicates.size == subjects.size
                    && predicates.zip(subjects).all { (predicate, subject) -> predicate == subject }
        }
    }

    class Matcher<T>(val subject: List<T>) {
        val cases: MutableList<Pair<Case<T>, (List<T>) -> String>> = mutableListOf()

        fun match(): String {
            return cases
                .first { it.first.test(subject) }
                .let { it.second(subject) }
        }
    }

    fun <T> Matcher<T>.start(vararg items: T): CaseStart<T> {
        return CaseStart(this, items.toList())
    }

    fun <T> Matcher<T>.end(vararg items: T): CaseEnd<T> {
        return CaseEnd(this, items.toList())
    }

    fun <T> Matcher<T>.otherwise(result: (List<T>) -> String) {
        CaseOtherwise<T>(this).then(result)
    }

    infix fun <T> Case<T>.and(case: Case<T>): Case<T> {
        return TwoCases(case.matcher, this, case)
    }


    // then powinien się zarejestrować i przehcować wynik
    infix fun <T> Case<T>.then(result: (List<T>) -> String) {
        this.matcher.cases.add(this to result)
    }

    interface MatcherList<T> {
        val matcher: Matcher<T>
    }

    class MatcherStart<T>(override val matcher: Matcher<T>) : MatcherList<T> {
        operator fun get(vararg elements: T): CaseStart<T> = CaseStart(matcher, elements.toList())
    }

    class MatcherEnd<T>(override val matcher: Matcher<T>) : MatcherList<T> {
        operator fun get(vararg elements: T): CaseEnd<T> = CaseEnd(matcher, elements.toList())
    }

    class MatcherExact<T>(override val matcher: Matcher<T>) : MatcherList<T> {
        operator fun get(vararg elements: T): CaseExact<T> = CaseExact(matcher, elements.toList())
    }

    val <T> Matcher<T>.start: MatcherStart<T>
        get() = MatcherStart(this)

    val <T> Matcher<T>.end: MatcherEnd<T>
        get() = MatcherEnd(this)

    val <T> Matcher<T>.exact: MatcherExact<T>
        get() = MatcherExact(this)


}
