package me.stasiak.ver3.list

import me.stasiak.ver3.api.extensions.match
import me.stasiak.ver3.api.onit.case
import me.stasiak.ver3.otherwise
import me.stasiak.ver3.then

fun listDestructionExExample() {
    listOf(3, 4, 6).match {
        case { (a, b, c) ->
            isDivisable(a, 3) && isDivisable(b, 3) && isDivisable(c, 3)
        } then { "Wszystkie podzielne przez 3" }

        case { (a, b, c) ->
            isDivisable(a, 3) && isDivisable(b, 2) && isDivisable(c, 1)
        } then { "Są podzielne malejąco do 3 do 1" }

        case { (a, b, c) ->
            isDivisable(a, 1) && isDivisable(b, 2) && isDivisable(c, 3)
        } then { "FizzBuzz" }

        otherwise { "Nic tu się nie zgadza" }
    }.let { println(it) }
}

fun listDestructionExExample2() {
    listOf(3, 4, 6).match {
        val (a, b, c) = this.subject

        case { isDivisable(a, 3) && isDivisable(b, 3) && isDivisable(c, 3) } then { "Wszystkie podzielne przez 3" }
        case { isDivisable(a, 3) && isDivisable(b, 2) && isDivisable(c, 1) } then { "Są podzielne malejąco do 3 do 1" }
        case { isDivisable(a, 1) && isDivisable(b, 2) && isDivisable(c, 3) } then { "FizzBuzz" }

        otherwise { "Nic tu się nie zgadza" }
    }.let { println(it) }
}

fun withoutPatternMatching() {
    listOf(3, 4, 6).let { (a, b, c) ->
        if (isDivisable(a, 3) && isDivisable(b, 3) && isDivisable(c, 3)) {
            "Wszystkie podzielne przez 3"
        } else if (isDivisable(a, 3) && isDivisable(b, 2) && isDivisable(c, 1)) {
            "Są podzielne malejąco do 3 do 1"
        } else if (isDivisable(a, 1) && isDivisable(b, 2) && isDivisable(c, 3)) {
            "Są podzielne malejąco do 3 do 1"
        } else {
            "Nic tu się nie zgadza"
        }
    }.let { println(it) }
}


fun withoutPatternMatching2() {
    listOf(3, 4, 6).let { (a, b, c) ->
        when {
            isDivisable(a, 3) && isDivisable(b, 3) && isDivisable(c, 3) -> "Wszystkie podzielne przez 3"
            isDivisable(a, 3) && isDivisable(b, 2) && isDivisable(c, 1) -> "Są podzielne malejąco do 3 do 1"
            isDivisable(a, 1) && isDivisable(b, 2) && isDivisable(c, 3) -> "Są podzielne malejąco do 3 do 1"
            else -> "Nic tu się nie zgadza"
        }
    }.let { println(it) }
}
