package me.stasiak

import kotlin.test.Test
import kotlin.test.assertEquals

class No8StartAndEndComposition {
    private val secretCombination = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)


    @Test
    fun `should match start(1, 2, 3) end(8, 9) second`() {
        val result = match(secretCombination) {
            start(1, 2, 3) and end(8, 9, 10) then { "up to 10" }
            start(1, 2, 3) and end(8, 9) then { "up to 9" }
        }

        assertEquals("up to 9", result)
    }

    @Test
    fun `should match start(1, 2, 3) end(8, 9) first`() {
        val result = match(secretCombination) {
            start(1, 2, 3) and end(8, 9) then { "up to 9" }
            start(1, 2, 3) then { "up to 9" }
        }

        assertEquals("up to 9", result)
    }

    @Test
    fun `should match start(1, 2, 3) second`() {
        val result = match(secretCombination) {
            start(1, 2, 4) and end(8, 9) then { "up to 9" }
            start(1, 2, 3) then { "only 3" }
        }

        assertEquals("only 3", result)
    }


    private fun match(obj: List<Int>, init: Matcher.() -> Unit): String {
        val html = Matcher(obj)
        html.init()
        return html.match()
    }

    interface Case {
        fun test(): Boolean
        fun result(): String
        val matcher: Matcher
    }

    class TwoCases(override val matcher: Matcher, val left: Case, val right: Case) : Case {
        override fun test(): Boolean {
            return left.test() && right.test()
        }

        override fun result(): String {

            TODO("Not yet implemented")
        }

    }

    class CaseStart(
        override val matcher: Matcher,
        private val subjects: List<Int>,
        private val predicates: List<Int>
    ) : Case {
        lateinit var result: (List<Int>) -> String

        override fun test(): Boolean {
            return predicates.zip(subjects).all { (predicate, subject) -> predicate == subject }
        }

        override fun result(): String {
            return result(subjects)
        }
    }

    class CaseEnd(
        override val matcher: Matcher,
        private val subjects: List<Int>,
        private val predicates: List<Int>
    ) : Case {

        lateinit var result: (List<Int>) -> String

        override fun test(): Boolean {
            return predicates.reversed().zip(subjects.reversed()).all { (predicate, subject) -> predicate == subject }
        }

        override fun result(): String {
            return result(subjects)
        }
    }

    class Matcher(val obj: List<Int>) {
        val cases: MutableList<Pair<Case, (List<Int>) -> String>> = mutableListOf()

        fun match(): String {
            return cases
                .first { it.first.test() }
                .let { it.second(obj) }
        }
    }

    fun Matcher.start(vararg predicate: Int): CaseStart {
        return CaseStart(this, this.obj, predicate.toList())
    }

    fun Matcher.end(vararg predicate: Int): CaseEnd {
        return CaseEnd(this, this.obj, predicate.toList())
    }

    infix fun Case.and(case: Case): Case {
        return TwoCases(case.matcher, this, case)
    }


    // then powinien się zarejestrować i przehcować wynik
    infix fun Case.then(result: (List<Int>) -> String) {
        this.matcher.cases.add(this to result)
    }

//    operator fun Matcher.get(a1: Int, a2: Int, a3: Int) {
//
//    }

    operator fun Matcher.get(vararg a1: Int) {

    }

    val Matcher.coll: List<Int>
        get() {
            TODO()
        }

    operator fun List<Int>.get(vararg a: Int) {

    }
}
