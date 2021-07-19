package me.stasiak

import kotlin.test.Test
import kotlin.test.assertEquals

class No10FizzBuzz {
    private val `1 to 9` = (1..9).toList() // listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)

    fun fizzBuzz0(number: Int): String {
        val isFizz = if (number % 3 == 0) "Fizz" else ""
        val isBuzz = if (number % 5 == 0) "Buzz" else ""

        if (isFizz.isEmpty() && isBuzz.isEmpty()) {
            return "$number"
        }
        return isFizz + isBuzz;
    }

    fun fizzBuzz1(number: Int): String = match(listOf(number % 3, number % 5)) {
        exact[0, 0] then { "FizzBuzz" }
        start[0] then { "Fizz" }
        end[0] then { "Buzz" }
        otherwise { "$number" }
    }

    fun fizzBuzz15(number: Int): String = match(
        listOf(number % 3, number % 5)
    ) {
        start[0] and end[0] then { "FizzBuzz" }
        start[0] then { "Fizz" }
        end[0] then { "Buzz" }
        otherwise { "$number" }
    }

    fun fizzBuzz2(number: Int): String = match(listOf(number % 3, number % 5)) {
        like[0, 0] then { "FizzBuzz" }
        like[0, null] then { "Fizz" }
        like[null, 0] then { "Buzz" }
        otherwise { "$number" }
    }

    val expected = listOf(
        "1",
        "2",
        "Fizz",
        "4",
        "Buzz",
        "Fizz",
        "7",
        "8",
        "Fizz",
        "Buzz",
        "11",
        "Fizz",
        "13",
        "14",
        "FizzBuzz",
        "16"
    );

    @Test
    fun `fizz buzz 0`() {
        val result = (1..16).map { fizzBuzz0(it) }

        assertEquals(expected, result)
    }

    @Test
    fun `fizz buzz 1`() {
        val result = (1..16).map { fizzBuzz1(it) }

        assertEquals(expected, result)
    }

    @Test
    fun `fizz buzz 15`() {
        val result = (1..16).map { fizzBuzz15(it) }

        assertEquals(expected, result)
    }

    @Test
    fun `fizz buzz 2`() {
        val result = (1..16).map { fizzBuzz2(it) }

        assertEquals(expected, result)
    }

    @Test
    fun `should match end`() {
        val result = match(`1 to 9`) {
            start[0, 1, 2, 3]
                .then { "Start with: 0, 1, 2, 3" }

            end[7, 8, 9]
                .then { "end with: 7, 8, 9" }

            exact[1, 2, 3, 4, 5, 6, 7, 8, 9]
                .then { "exact: 1, 2, 3, 4, 5, 6, 7, 8, 9" }
        }

        assertEquals("end with: 7, 8, 9", result)
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

    private fun match(obj: List<Int>, init: Matcher.() -> Unit): String {
        val matcher = Matcher(obj)
        matcher.init()
        return matcher.match()
    }

    interface Case {
        fun test(subjects: List<Int>): Boolean
        val matcher: Matcher
    }

    class TwoCases(override val matcher: Matcher, val left: Case, val right: Case) : Case {
        override fun test(subjects: List<Int>): Boolean {
            return left.test(subjects) && right.test(subjects)
        }
    }

    class CaseOtherwise(override val matcher: Matcher) : Case {
        override fun test(subjects: List<Int>): Boolean = true

    }

    class CaseStart(
        override val matcher: Matcher,
        private val predicates: List<Int>
    ) : Case {
        override fun test(subjects: List<Int>): Boolean {
            return predicates.zip(subjects).all { (predicate, subject) -> predicate == subject }
        }
    }

    class CaseLike(
        override val matcher: Matcher,
        private val predicates: List<Int?>
    ) : Case {
        override fun test(subjects: List<Int>): Boolean {
            if (predicates.size != subjects.size) return false

            return predicates
                .zip(subjects)
                .all { (predicate, subject) -> predicate == null || predicate == subject }

        }
    }

    class CaseEnd(
        override val matcher: Matcher,
        private val predicates: List<Int>
    ) : Case {
        override fun test(subjects: List<Int>): Boolean {
            val reversedFirst = predicates.reversed()
            val reversedOther = subjects.reversed()
            return reversedFirst
                .zip(reversedOther)
                .all { (predicate, subject) -> predicate == subject }
        }
    }

    class CaseExact(
        override val matcher: Matcher,
        private val predicates: List<Int>
    ) : Case {
        override fun test(subjects: List<Int>): Boolean {
            return predicates.size == subjects.size
                    && predicates.zip(subjects).all { (predicate, subject) -> predicate == subject }
        }
    }

    class Matcher(val subject: List<Int>) {
        val cases: MutableList<Pair<Case, (List<Int>) -> String>> = mutableListOf()

        fun match(): String {
            return cases
                .first { it.first.test(subject) }
                .let { it.second(subject) }
        }
    }

    fun Matcher.start(vararg items: Int): CaseStart {
        return CaseStart(this, items.toList())
    }

    fun Matcher.end(vararg items: Int): CaseEnd {
        return CaseEnd(this, items.toList())
    }

    fun Matcher.otherwise(result: (List<Int>) -> String) {
        CaseOtherwise(this).then(result)
    }

    infix fun Case.and(case: Case): Case {
        return TwoCases(case.matcher, this, case)
    }


    // then powinien się zarejestrować i przehcować wynik
    infix fun Case.then(result: (List<Int>) -> String) {
        this.matcher.cases.add(this to result)
    }

    interface MatcherList {
        val matcher: Matcher
    }

    class MatcherStart(override val matcher: Matcher) : MatcherList {
        operator fun get(vararg elements: Int): CaseStart = CaseStart(matcher, elements.toList())
    }

    class MatcherLike(override val matcher: Matcher) : MatcherList {
        operator fun get(vararg elements: Int?): CaseLike = CaseLike(matcher, elements.toList())
    }

    class MatcherEnd(override val matcher: Matcher) : MatcherList {
        operator fun get(vararg elements: Int): CaseEnd = CaseEnd(matcher, elements.toList())
    }

    class MatcherExact(override val matcher: Matcher) : MatcherList {
        operator fun get(vararg elements: Int): CaseExact = CaseExact(matcher, elements.toList())
    }

    val Matcher.start: MatcherStart
        get() = MatcherStart(this)

    val Matcher.end: MatcherEnd
        get() = MatcherEnd(this)

    val Matcher.exact: MatcherExact
        get() = MatcherExact(this)


    ///
    val Matcher.like: MatcherLike
        get() = MatcherLike(this)
}
