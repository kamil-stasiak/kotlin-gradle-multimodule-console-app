package me.stasiak.ver3.fizz.buzz

import me.stasiak.ver3.*
import me.stasiak.ver3.api.onthis.case

data class FizzBuzz(val number: Int) {
    val fizz: Boolean by lazy { number % 3 == 0 }
    val buzz: Boolean by lazy { number % 5 == 0 }
}

fun fizzBuzzExample() {
    (1..17)
        .asSequence()
        .map { number ->
            match(FizzBuzz(number)) {
                case { fizz && buzz } then { "FizzBuzz" }
                case { fizz } then { "Fizz" }
                case { buzz } then { "Buzz" }
                otherwise { "$number" }
            }
        }.forEach { println(it) }
}