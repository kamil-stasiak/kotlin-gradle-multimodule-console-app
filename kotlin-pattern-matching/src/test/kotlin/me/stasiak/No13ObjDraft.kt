package me.stasiak

import java.util.function.Predicate
import kotlin.reflect.KClass

class No13ObjDraft {


    data class FizzBuzz(val number: Int) {
        val fizz: Boolean
            get() = number % 3 == 0
        val buzz: Boolean
            get() = number % 5 == 0
    }

    fun fizzBuzz1(number: Int): String = match(
        FizzBuzz(number)
    ) {
        prop { it.fizz && it.buzz } then { "FizzBuzz" }
        prop { it.fizz } then { "Fizz" }
        prop { it.buzz } then { "Buzz" }
        otherwise { "$number" }
    }

    fun obj(number: Int): String = match(
        FizzBuzz(number)
    ) {
//        isA<FizzBuzz>()
        prop { it.fizz && it.buzz } then { "FizzBuzz" }
        prop { it.fizz } then { "Fizz" }
        prop { it.buzz } then { "Buzz" }
        otherwise { "$number" }
    }

    fun fizzBuzz2(number: Int): String = match(
        FizzBuzz(number)
    ) {
        prop { it.fizz && it.buzz }
            .then { "FizzBuzz" }

        prop { it.fizz }
            .then { "Fizz" }

        prop { it.buzz }
            .then { "Buzz" }

        otherwise { "$number" }
    }


//    private fun <T> match(obj: T, init: Matcher<T>.() -> Unit): String {
//        val matcher = Matcher(obj)
//        matcher.init()
//        return matcher.match()
//    }

    private fun <T> match(obj: T, init: Matcher<T>.() -> Unit): String {
        val matcher = Matcher(obj)
        matcher.init()
        return matcher.match()
    }

    interface Case<T> {
        fun test(subjects: T): Boolean
        val matcher: Matcher<T>
    }

    class TwoCases<T>(override val matcher: Matcher<T>, val left: Case<T>, val right: Case<T>) : Case<T> {
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

    class CaseIsAClass<T>(
        override val matcher: Matcher<Any>
    ) : Case<Any> {
        private val abc: T? = null
        override fun test(subjects: Any): Boolean {
//            return subjects is T //???
            return true;
        }

    }
//
//    class CaseStart<T>(
//        override val matcher: Matcher<T>,
//        private val predicates: T
//    ) : Case<T> {
//        override fun test(subjects: T): Boolean {
//            return predicates.zip(subjects).all { (predicate, subject) -> predicate == subject }
//        }
//    }
//
//    class CaseEnd<T>(
//        override val matcher: Matcher<T>,
//        private val predicates: T
//    ) : Case<T> {
//        override fun test(subjects: T): Boolean {
//            val reversedFirst = predicates.reversed()
//            val reversedOther = subjects.reversed()
//            return reversedFirst
//                .zip(reversedOther)
//                .all { (predicate, subject) -> predicate == subject }
//        }
//    }
//
//    class CaseExact<T>(
//        override val matcher: Matcher<T>,
//        private val predicates: T
//    ) : Case<T> {
//        override fun test(subjects: T): Boolean {
//            return predicates.size == subjects.size
//                    && predicates.zip(subjects).all { (predicate, subject) -> predicate == subject }
//        }
//    }

    class Matcher<T>(val subject: T) {
        val cases: MutableList<Pair<Case<T>, (T) -> String>> = mutableListOf()

        fun match(): String {
            return cases
                .first { it.first.test(subject) }
                .let { it.second(subject) }
        }
    }

//    fun <T> Matcher<T>.start(vararg items: T): CaseStart<T> {
//        return CaseStart(this, items.toList())
//    }
//
//    fun <T> Matcher<T>.end(vararg items: T): CaseEnd<T> {
//        return CaseEnd(this, items.toList())
//    }

    fun <T> Matcher<T>.otherwise(result: (T) -> String) {
        CaseOtherwise<T>(this).then(result)
    }


    fun <T> Matcher<T>.prop(predicate: Predicate<T>): CasePredicate<T> {
        return CasePredicate(this, predicate)
    }
//
//    fun <T> Matcher<Any>.isA(): CaseIsAClass {
//        return CaseIsAClass<T>(this)
//    }

    infix fun <T> Case<T>.and(case: Case<T>): Case<T> {
        return TwoCases(case.matcher, this, case)
    }


    // then powinien się zarejestrować i przehcować wynik
    infix fun <T> Case<T>.then(result: (T) -> String) {
        this.matcher.cases.add(this to result)
    }

//    interface MatcherT {
//        val matcher: Matcher<T>
//    }

//    class MatcherStart<T>(override val matcher: Matcher<T>) : MatcherT {
//        operator fun get(vararg elements: T): CaseStart<T> = CaseStart(matcher, elements.toList())
//    }
//
//    class MatcherEnd<T>(override val matcher: Matcher<T>) : MatcherT {
//        operator fun get(vararg elements: T): CaseEnd<T> = CaseEnd(matcher, elements.toList())
//    }
//
//    class MatcherExact<T>(override val matcher: Matcher<T>) : MatcherT {
//        operator fun get(vararg elements: T): CaseExact<T> = CaseExact(matcher, elements.toList())
//    }
//
//    val <T> Matcher<T>.start: MatcherStart<T>
//        get() = MatcherStart(this)
//
//    val <T> Matcher<T>.end: MatcherEnd<T>
//        get() = MatcherEnd(this)
//
//    val <T> Matcher<T>.exact: MatcherExact<T>
//        get() = MatcherExact(this)
//

}
