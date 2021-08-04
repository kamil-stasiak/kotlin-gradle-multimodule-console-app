package me.stasiak

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class Board(val raw: List<String>) {

    fun countTrees(): Int {
        var currentX = 0;
        var currentY = 0;
        var treesCount = 0;


        return 0

    }
}

class ExerciseTest {
    @Test
    fun `1  on example data`() {
        val inputText = """
            ..##.......
            #...#...#..
            .#....#..#.
            ..#.#...#.#
            .#...##..#.
            ..#.##.....
            .#.#.#....#
            .#........#
            #.##...#...
            #...##....#
            .#..#...#.#
            """.trimIndent()
            .split("\n")


        val result = Board(inputText).countTrees()

        assertEquals(2, result, "Error!")
    }
}
