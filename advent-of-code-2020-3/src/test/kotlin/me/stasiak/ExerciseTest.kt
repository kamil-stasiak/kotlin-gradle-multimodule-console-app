package me.stasiak

import java.io.File
import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals

typealias Row = List<Ground>
typealias WorldMap = List<Row>

sealed class Ground {
    object Tree : Ground()
    object OpenSquare : Ground()
}

data class Input(val slope: Pair<Int, Int>, val result: Int)

class Board(raw: List<String>) {

    private val worldMap: WorldMap
    private val xSize: Int

    init {
        worldMap = raw.map { parseRows(it) }
        xSize = raw[0].length
    }

    private fun parseRows(rows: String) =
        rows.map { parseRow(it) }

    private fun parseRow(char: Char) =
        when (char) {
            '#' -> Ground.Tree
            '.' -> Ground.OpenSquare
            else -> error("Illegal state")
        }

    /*
    *  (0, 0)----> x
    *  |
    * \/
    * y
    */
    private fun WorldMap.getGroundValue(x: Int, y: Int): Ground {
        if (x >= xSize) {
            return this[y][x % xSize]
        }
        return this[y][x]
    }


    fun countTrees(stepX: Int, stepY: Int): BigInteger {
        var currentX = 0
        var currentY = 0
        var trees = 0

        while (currentY < worldMap.size) {
            if (worldMap.getGroundValue(currentX, currentY) == Ground.Tree) {
                ++trees;
            }
            currentX += stepX
            currentY += stepY
        }
        return trees.toBigInteger()
    }
}

class ExerciseTest {
    private val exampleInput = """
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

    private val exampleBoard = Board(exampleInput)

    private val secondTaskSlopesWithResult = listOf(
        Input(1 to 1, 2),
        Input(3 to 1, 7),
        Input(5 to 1, 3),
        Input(7 to 1, 4),
        Input(1 to 2, 2),
    )

    private val inputTxt = File("src/test/resources/input.txt")
        .readLines()

    private val board = Board(inputTxt)

    @Test
    fun `1 task on example data`() {
        // 0, 0
        // 3, 1
        // 6, 2
        // 9, 3
        // 12, 4 = 12 % 11 = 1 r 1
        val result = exampleBoard.countTrees(3, 1)

        assertEquals(7.toBigInteger(), result, "Error!")
    }

    @Test
    fun `1 task on real data`() {
        val result = board.countTrees(3, 1)

        assertEquals(278.toBigInteger(), result, "Error!")
    }


    @Test
    fun `2 task on example data`() {
        secondTaskSlopesWithResult.map { (slope, result) ->
            val partialResult = exampleBoard.countTrees(slope.first, slope.second)

            assertEquals(result.toBigInteger(), partialResult, "Error!")

            partialResult
        }
            .reduce(::multiply)
            .let { assertEquals(336.toBigInteger(), it, "Task error") }

    }

    @Test
    fun `2 task on input data`() {
        listOf(1 to 1, 3 to 1, 5 to 1, 7 to 1, 1 to 2)
            .map { (right, down) -> board.countTrees(right, down) }
            .reduce(::multiply)
            .let { assertEquals(9709761600.toBigInteger(), it, "Task error") }
    }

    private fun multiply(acc: BigInteger, unit: BigInteger) = acc * unit
}
