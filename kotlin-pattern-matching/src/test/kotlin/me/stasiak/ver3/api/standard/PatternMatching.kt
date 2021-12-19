package me.stasiak.ver3

import java.util.function.Predicate

fun <T> match(obj: T, init: Matcher<T>.() -> Unit): String =
    Matcher(obj).apply(init).match()


interface Case<T> {
    fun test(subjects: T): Boolean
    val matcher: Matcher<T>
}

class TwoCases<T>(
    override val matcher: Matcher<T>,
    private val left: Case<T>,
    private val right: Case<T>
) : Case<T> {
    override fun test(subjects: T): Boolean {
        return left.test(subjects) && right.test(subjects)
    }
}

class CaseOtherwise<T>(override val matcher: Matcher<T>) : Case<T> {
    override fun test(subjects: T): Boolean = true

}

class CasePredicate<T>(
    override val matcher: Matcher<T>,
    private val predicate: Predicate<T>
) : Case<T> {
    override fun test(subjects: T): Boolean {
        return predicate.test(subjects)
    }

}

class Matcher<T>(val subject: T) {
    val cases: MutableList<Pair<Case<T>, (T) -> String>> = mutableListOf()

    fun match(): String {
        return cases
            .first { it.first.test(subject) }
            .let { it.second(subject) }
    }
}

fun <T> Matcher<T>.otherwise(result: T.() -> String) {
    CaseOtherwise<T>(this).then(result)
}
infix fun <T> Case<T>.and(case: Case<T>): Case<T> {
    return TwoCases(case.matcher, this, case)
}

infix fun <T> Case<T>.then(result: (T) -> String) {
    this.matcher.cases.add(this to result)
}
