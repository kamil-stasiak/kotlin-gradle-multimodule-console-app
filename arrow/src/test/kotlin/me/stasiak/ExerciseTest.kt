package me.stasiak

import java.io.File
import java.lang.IllegalStateException
import kotlin.test.Test
import kotlin.test.assertEquals

class ExerciseTest {
    @Test
    fun `test on example data`() {
        val data = setOf(1721, 979, 366, 299, 675, 1456)
    }

    @Test
    fun `solve problem`() {
        val data = File("src/test/resources/input.txt")
            .readLines()
            .map(String::toString)
    }
}
