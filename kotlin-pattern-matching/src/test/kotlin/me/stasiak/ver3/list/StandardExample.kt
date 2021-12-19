package me.stasiak.ver3.list

import me.stasiak.ver3.caseSplit
import me.stasiak.ver3.match
import me.stasiak.ver3.otherwise
import me.stasiak.ver3.then

fun listDestructionExample() {
    match(listOf(3, 4, 6)) {
        caseSplit { (a, b, c) ->
            isDivisable(a, 3) && isDivisable(b, 3) && isDivisable(c, 3)
        } then { "Wszystkie podzielne przez 3" }

        caseSplit { (a, b, c) ->
            isDivisable(a, 3) && isDivisable(b, 2) && isDivisable(c, 1)
        } then { "Są podzielne malejąco do 3 do 1" }

        caseSplit { (a, b, c) ->
            isDivisable(a, 1) && isDivisable(b, 2) && isDivisable(c, 3)
        } then { "FizzBuzz" }

        otherwise { "Nic tu się nie zgadza" }
    }.let { println(it) }
}

fun isDivisable(number: Int, by: Int): Boolean {
    return number % by == 0
}