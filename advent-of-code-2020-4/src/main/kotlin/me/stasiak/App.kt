package me.stasiak

import java.io.File

fun main() {

    val numbers = File("advent-of-code-2020-2/src/main/resources/input.txt")
        .readLines()
        .map(String::toInt)

    println("End")
}
