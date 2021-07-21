package me.stasiak

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

data class PasswordWithPolicy(
    val password: String,
    val range: IntRange,
    val letter: Char
) {
    companion object {
        fun parse(value: String): PasswordWithPolicy = PasswordWithPolicy(
            password = value.substringAfter(": "),
            range = value.substringBefore(" ").let {
                val (start, end) = it.split("-")
                (start.toInt()..end.toInt())
            },
            letter = value.substringAfter(" ").substringBefore(":").single()
        )

        private val regex = Regex("""(\d+)-(\d+) ([a-z]): ([a-z]+)""")
        fun parseUsingRegex(line: String): PasswordWithPolicy =
            regex.matchEntire(line)!!
                .destructured
                .let { (start, end, letter, password) ->
                    PasswordWithPolicy(password, start.toInt()..end.toInt(), letter.single())
                }
    }
}

class RefactorTest {
    @Test
    fun `1  on example data`() {
        val passwords = """
            1-3 a: abcde
            1-3 b: cdefg
            2-9 c: ccccccccc
        """.trimIndent()
            .split("\n")
            .map(PasswordWithPolicy::parse)

        val result = passwords
            .filter { it.run { password.count { current -> current == letter } in range } }
            .size

        assertEquals(2, result, "Error!")
    }

    @Test
    fun `1 test on file`() {
        val passwords = File("src/test/resources/input.txt")
            .readLines()
            .map(PasswordWithPolicy::parse)

        val result = passwords
            .filter { it.run { password.count { current -> current == letter } in range } }
            .size

        assertEquals(422, result, "Error!")
    }


    @Test
    fun `2  on example data`() {
        val passwords = """
            1-3 a: abcde
            1-3 b: cdefg
            2-9 c: ccccccccc
        """.trimIndent()
            .split("\n")
            .map(PasswordWithPolicy::parse)

        val result = passwords
            .filter(this::isCorrectPasswordBy2Policy)
            .size


        assertEquals(1, result, "Error!")
    }

    private fun isCorrectPasswordBy2Policy(it: PasswordWithPolicy) =
        it.run { (password[range.first - 1] == letter) xor (password[range.last - 1] == letter) }

    @Test
    fun `2 test on file`() {
        val passwords = File("src/test/resources/input.txt")
            .readLines()
            .map(PasswordWithPolicy::parse)

        val result = passwords
            .filter(this::isCorrectPasswordBy2Policy)
            .size

        assertEquals(451, result, "Error!")
    }
}
