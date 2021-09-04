package me.stasiak

import org.jgrapht.Graph
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.traverse.DepthFirstIterator
import java.io.File
import kotlin.test.Test
import kotlin.sequences.filter as keep
import kotlin.sequences.filterNot as remove


class ExerciseTest {
    @Test
    fun `1 task on example data`() {
        val graph: Graph<File, DefaultEdge> = DefaultDirectedGraph(DefaultEdge::class.java)


        File("C:\\Users\\kamil\\Dysk Google\\Sync\\Obsidian\\Notes")
            .walkTopDown()
            .remove { it.toString().contains("\\.") }
            .keep { it.toString().endsWith(".md") }
            .forEach { file ->
                val fileName = file.toString().substringAfterLast("\\").substringBeforeLast(".md")
                println("* $fileName")

                file.readLines()
                    .filter { line -> line.contains("[[") && line.contains("]]") }
                    .map { line -> line.substringAfter("[[").substringBefore("]]") }
                    .forEach { println("-> $it") }

//                graph.addVertex(it)
            }


//        val iterator: Iterator<File> = DepthFirstIterator(graph, start)

    }
}
