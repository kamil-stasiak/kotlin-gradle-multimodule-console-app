package me.stasiak.ver1

import kotlin.test.Test

class PatternMatchingTest {
    //  (doseq [n (range 1 101)]
    //  (println
    //    (match [(mod n 3) (mod n 5)]
    //      [0 0] "FizzBuzz"
    //      [0 _] "Fizz"
    //      [_ 0] "Buzz"
    //      :else n)))
    @Test
    fun `setter should ignore blank name`() {
        val name = "John Joe"
        match(
            name,
            Pair(
                { it.length > 100 },
                { a: String -> "Very long string" }), // filter, identity
            { a: String -> a.length < 2 } to { a: String -> "Very short string" }, // filter, identity
            // else
            { _: String -> true } to { a: String -> a.toUpperCase() }, // map
        )

        // val mapOf: Map<(String) -> Boolean, (String) -> String> = mapOf(
        //                    isNotBlank to { a: String -> a }, // filter, identity
        //                    hasProperLength to { a: String -> a }, // filter, identity
        //                    { _: String -> true } to { a: String -> a.toUpperCase() }, // map
        //                    { _: String -> true } to { a: String ->
        //                        println(a)
        //                        a
        //                    } // peek
        //                )
//        assertEquals("JOHN", person.name)
    }

    private fun match(name: String, vararg pairs: Pair<(String) -> Boolean, (String) -> String>) {
        TODO("Not yet implemented")
    }
}
