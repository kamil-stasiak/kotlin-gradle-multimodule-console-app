package me.stasiak

import java.io.File
import java.lang.IllegalStateException
import kotlin.test.Test
import kotlin.test.assertEquals

fun calculate(expenses: Collection<Int>): Number {
    expenses.forEach { a ->
        expenses.forEach { b ->
            if (a + b == 2020) {
                return a * b
            }
        }
    }
    throw IllegalStateException("Error!")
}


fun calculate3(expenses: Collection<Int>): Number {
    expenses.forEach { a ->
        expenses.forEach { b ->
            expenses.forEach { c ->
                if (a + b + c == 2020) {
                    return a * b * c
                }

            }
        }
    }
    throw IllegalStateException("Error!")
}

// permutacje:
// The array of integers [3,4,7] has three elements and six permutations:
//n! = 3! = 1 x 2 x 3 = 6
//Permutations: [3,4,7]; [3,7,4]; [4,7,3]; [4,3,7]; [7,3,4]; [7,4,3]

class ExerciseTest {
    @Test
    fun `test on example data`() {
        val expenses = setOf(1721, 979, 366, 299, 675, 1456)

        val result = calculate(expenses)

        assertEquals(514579, result, "Error!")
    }

    @Test
    fun `solve problem`() {
        val expenses = File("src/test/resources/input.txt")
            .readLines()
            .map(String::toInt)

        val result = calculate(expenses)

        println(result)
    }

    @Test
    fun `test 3 on example data`() {
        val expenses = setOf(1721, 979, 366, 299, 675, 1456)

        val result = calculate3(expenses)

        assertEquals(241861950, result, "Error!")
    }

    @Test
    fun `solve part 2`() {
        val expenses = File("src/test/resources/input.txt")
            .readLines()
            .map(String::toInt)

        val result = calculate3(expenses)

        println(result)
    }
}
