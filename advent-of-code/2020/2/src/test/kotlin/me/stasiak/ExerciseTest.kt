package me.stasiak

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals



class ExerciseTest {
    @Test
    fun `1  on example data`() {
        val inputText = """
            1-3 a: abcde
            1-3 b: cdefg
            2-9 c: ccccccccc
        """.trimIndent()
            .split("\n")


        val result = howManyValidPasswords(inputText, this::isValidPassword)

        assertEquals(2, result, "Error!")
    }

    @Test
    fun `1 test on file`() {
        val inputText = File("src/test/resources/input.txt")
            .readLines()

        val result = howManyValidPasswords(inputText, this::isValidPassword)

        assertEquals(422, result, "Error!")
    }

    private fun howManyValidPasswords(inputText: List<String>, predicate: (Int, Int, Char, String) -> Boolean): Int {
        return inputText.map { it.split(" ") }
            .filter {
                val howMany = it[0].split("-")
                val min = howMany[0].toInt()
                val max = howMany[1].toInt()
                val letter = it[1].substringBefore(":").toCharArray().first()
                return@filter predicate(min, max, letter, it[2])
            }.size
    }

    private fun isValidPassword(min: Int, max: Int, letter: Char, password: String): Boolean =
        password.count { it == letter } in min..max

    @Test
    fun `2  on example data`() {
        val inputText = """
            1-3 a: abcde
            1-3 b: cdefg
            2-9 c: ccccccccc
        """.trimIndent()
            .split("\n")


        val result = howManyValidPasswords(inputText, this::isValidPassword2)

        assertEquals(1, result, "Error!")
    }

    @Test
    fun `2 test on file`() {
        val inputText = File("src/test/resources/input.txt")
            .readLines()

        val result = howManyValidPasswords(inputText, this::isValidPassword2)

        assertEquals(451, result, "Error!")
    }

    private fun isValidPassword2(min: Int, max: Int, letter: Char, password: String): Boolean =
        (password[min - 1] == letter) xor (password[max - 1] == letter)

}
