package me.stasiak.ver2

import me.stasiak.ver2.TestMatcher.Vars.*
import me.stasiak.ver2.TestMatcher.Vars2.*
import kotlin.test.Test
import kotlin.test.assertEquals

class TestMatcher {

    @Test
    fun `should match on string`() {
        val match = match("John Doe") {
            case({ it.length > 100 }) {
                "> 100"
            }
            case(and({ it.length > 1 }, { it.length > 1 })) {
                "> 1"
            }

        }

        assertEquals("> 1", match)
    }


    @Test
    fun `should match on list`() {
        val match = match(listOf(1, 2, 3)) {
            case({ it.size > 100 }) {
                "> 100"
            }
            case(and({ it.size > 10 }, { it.size < 50 })) {
                "> 1"
            }
            case(listLike(AA, BB, CC).where { it[AA]!! > it[CC]!! }) {
                "OK1"
            }
            case(listLike(AA, BB, CC) where { it[AA]!! > it[CC]!! }) {
                "OK2"
            }
            case(listLike(AA, BB, CC).where({ it[AA]!! > it[CC]!! })) {
                "OK3"
            }
            case(listLike(AA, BB, CC).where(
                { it[AA]!! < it[BB]!! }, { it[BB]!! < it[CC]!! })
            ) { "OK4" }
        }

        assertEquals("OK4", match)
    }

    @Test
    fun `should match on list on exact length`() {
        val match = match(listOf(1, 2, 3)) {
            case(listLike(AA, BB).where { it[AA]!! > it[BB]!! }) {
                "BAD1"
            }
            case(listLike(AA, BB, CC).where { it[AA]!! < it[BB]!! }) {
                "OK"
            }
        }

        assertEquals("OK", match)
    }

    @Test
    fun `should match pattern with same numbers`() {
        val match = match(listOf(1, 2, 1)) {
            case(listLike(AA, BB, CC).where { it[AA]!! < it[CC]!! }) {
                "BAD"
            }
            case(listLike(AA, BB, AA).where { it[AA]!! < it[BB]!! }) {
                "OK"
            }
        }

        assertEquals("OK", match)
    }

    private fun valuesShouldBeDifferent(): (Map<Vars2, Int>) -> Boolean =
        { it.values.distinct().size == it.values.size }

    private fun valuesCouldBeTheSame(): (Map<Vars2, Int>) -> Boolean =
        { true }

    @Test
    fun `should match otherwise because patterns are wrong`() {
        val match = match(listOf(1, 2, 1)) {
            case(listLike(AA, BB, CC).where { it[AA]!! < it[CC]!! }) {
                "BAD1"
            }
            case(listLike(BB, BB, AA).where { it[AA]!! < it[BB]!! }) {
                "BAD2"
            }
            case(listLike(AA, BB, BB).where { it[AA]!! < it[BB]!! }) {
                "BAD2"
            }
            otherwise {
                "OK"
            }
        }

        assertEquals("OK", match)
    }

    @Test
    fun `should match different values`() {
        val match = match(listOf(1, 2, 3)) {
            case(listLike(AA, BB, BB).where(valuesShouldBeDifferent())) {
                "BAD1"
            }
            case(listLike(BB, BB, AA).where(valuesShouldBeDifferent())) {
                "BAD2"
            }
            case(listLike(AA, BB, CC).where(valuesShouldBeDifferent())) {
                "OK"
            }
            otherwise {
                "OTHERWISE"
            }
        }

        assertEquals("OK", match)
    }
    @Test
    fun `should match first same values`() {
        val match = match(listOf(1, 1, 1)) {
            case(listLike(AA, BB, BB).where(valuesCouldBeTheSame())) {
                "OK"
            }
            otherwise {
                "OTHERWISE"
            }
        }

        assertEquals("OK", match)
    }

    @Test
    fun `should match first same values 2`() {
        val match = match(listOf(1, 1, 1)) {
            case(listLike(AA, BB, BB).where(valuesShouldBeDifferent())) {
                "BAD1"
            }
            case(listLike(AA, BB, BB).where(valuesCouldBeTheSame())) {
                "OK"
            }
            otherwise {
                "OK2"
            }
        }

        assertEquals("OK", match)
    }

    private fun <T> and(left: ((T) -> Boolean), right: ((T) -> Boolean)): ((T) -> Boolean) {
        return { left(it) && right(it) }
    }

    sealed class Vars<T> {
        class A<T> : Vars<T>()
        class B<T> : Vars<T>()
        class C<T> : Vars<T>()
        class D<T> : Vars<T>()
        class REST<T> : Vars<T>()
    }

    sealed class Vars2 {
        object AA : Vars2()
        object BB : Vars2()
        object CC : Vars2()
        object DD : Vars2()
        object RESTT : Vars2()
    }


    sealed class ListFunctionParams<T> {
        class PatternDoesNotMatch<T> : ListFunctionParams<T>()
        class PatternIsOk<T>(val params: Map<Vars2, T>) : ListFunctionParams<T>()
    }

    //    class Pattern<T>(val pattern: Array<out Vars<T>>) {
    class Pattern<T>(
        val matcherContext: Matcher<List<T>>,
        val pattern: Array<out Vars2>
    ) {

        private fun createParamsMap(
            obj: List<T>
        ): ListFunctionParams<T> {

            val mapa: MutableMap<Vars2, T> = mutableMapOf()
            pattern.forEachIndexed { index, letter ->
                val contains = mapa.contains(letter)
                if (contains) {
                    val valueFromMap = mapa[letter]!!
                    val currentValue = obj[index]
                    // todo if not equal then stop (return false)
                    if (valueFromMap != currentValue) {
                        return ListFunctionParams.PatternDoesNotMatch()
                    }
                }
                mapa[letter] = obj[index]
            }
            return ListFunctionParams.PatternIsOk(mapa)
        }
        // this
//        public fun where(function: Map<Vars<String>, Int>.() -> Unit): (T) -> Boolean {
//            return { true }
//        }

//        fun where(function: (Map<Vars<String>, Int>) -> Unit): (T) -> Boolean {
//            return { true }
//        }

        infix fun where(function: (Map<Vars2, T>) -> Boolean): (List<T>) -> Boolean {
            return when (val createParamsMap = createParamsMap(matcherContext.obj)) {
                is ListFunctionParams.PatternIsOk -> {
                    { function(createParamsMap.params) }
                }
                is ListFunctionParams.PatternDoesNotMatch -> {
                    { false }
                }
            }
        }

        fun where(vararg functions: (Map<Vars2, T>) -> Boolean): (List<T>) -> Boolean {
            return when (val createParamsMap = createParamsMap(matcherContext.obj)) {
                is ListFunctionParams.PatternIsOk -> {
                    { functions.map { it(createParamsMap.params) }.all { it } }
                }
                is ListFunctionParams.PatternDoesNotMatch -> {
                    { false }
                }
            }
        }
    }


    fun <T> match(obj: T, init: Matcher<T>.() -> Unit): String {
        val matcherContext = Matcher(obj)
        matcherContext.init()
        return matcherContext.match()
    }

    class Case<T>(
        private val matcher: Matcher<T>,
        private val predicate: (T) -> Boolean,
        private val thenBlock: (T) -> String
    ) {
        fun test(objUnderTest: T): Boolean = predicate(objUnderTest)
        fun then(objUnderTest: T): String = thenBlock(objUnderTest)
    }


    class Matcher<T>(val obj: T) {
        val cases: MutableList<Case<T>> = mutableListOf()

        fun case(predicate: (T) -> Boolean, thenBlock: (T) -> String) {
            cases.add(Case(this, predicate, thenBlock))
        }

        fun otherwise(thenBlock: (T) -> String) {
            cases.add(Case(this, { true }, thenBlock))
        }

        fun match(): String = cases.first { it.test(obj) }.then(obj)

//        fun listLike(vararg pattern: Vars<T>): Pattern<T> {
//            return Pattern(pattern)
//        }


        val A = A<T>()
        val B = B<T>()
    }

    fun <T> Matcher<List<T>>.listLike(vararg pattern: Vars2): Pattern<T> {
        return Pattern(this, pattern)
    }

    val <T> Matcher<T>.C: C<T>
        get() = C()

}
