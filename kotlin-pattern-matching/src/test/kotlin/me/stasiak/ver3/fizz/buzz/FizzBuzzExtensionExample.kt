package me.stasiak.ver3.fizz.buzz

import me.stasiak.ver3.api.extensions.match
import me.stasiak.ver3.api.onthis.case
import me.stasiak.ver3.otherwise
import me.stasiak.ver3.then

fun fizzBuzzExExample() {
    (1..17)
        .asSequence()
        .map { number ->
            FizzBuzz(number).match {
                case { fizz && buzz } then { "FizzBuzz" }
                case { fizz } then { "Fizz" }
                case { buzz } then { "Buzz" }
                otherwise { "$number" }
            }
        }.forEach { println(it) }
}