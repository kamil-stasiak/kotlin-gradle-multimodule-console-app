package me.stasiak

import com.beust.klaxon.Klaxon
import com.mxgraph.layout.mxCircleLayout
import com.mxgraph.layout.mxIGraphLayout
import com.mxgraph.util.mxCellRenderer
import org.jgrapht.Graph
import org.jgrapht.ext.JGraphXAdapter
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.traverse.DepthFirstIterator
import java.awt.Color
import java.io.File
import javax.imageio.ImageIO
import kotlin.sequences.filter as keep
import kotlin.sequences.filterNot as remove


data class NoteVertex(
    val file: File,
    val fileName: String,
    val relativePath: String,
    val path: String,
    val links: List<String>
) {
    override fun toString(): String {
        return relativePath
    }
}

data class D3(
    val nodes: List<D3Node>,
    val links: List<D3Link>
)

data class D3Link(
    val source: Int,
    val target: Int,
)


data class D3Node(
    val id: Int,
    val name: String,
)

// network graph
// https://www.d3-graph-gallery.com/network.html
fun main() {
    val graph: Graph<NoteVertex, DefaultEdge> = DefaultDirectedGraph(DefaultEdge::class.java)

    val mapa: MutableMap<String, NoteVertex> = LinkedHashMap()

    val notes = File("kotlin-obsidian-graph/src/main/resources/obsidian/Notes")
        .walkTopDown()
        .remove { it.toString().contains("\\.") }
        .keep { it.toString().endsWith(".md") }
        .map {
            NoteVertex(
                file = it,
                path = it.toString(),
                fileName = it.toString()
                    .substringAfterLast("\\")
                    .substringBeforeLast(".md"),
                relativePath = it.toString()
                    .substringAfterLast("\\Notes\\")
                    .substringBeforeLast(".md"),
                links = it.readLines()
                    .filter { line -> line.contains("[[") && line.contains("]]") }
                    .map { line -> line.substringAfter("[[").substringBefore("]]") }
            )
        }

    notes.forEach { graph.addVertex(it) }
    notes.forEach { mapa[it.relativePath] = it }
    notes.forEach { mapa[it.fileName] = it }
    notes.forEach { note ->
        note.links.forEach { link ->
            val noteVertex = mapa[link]
            noteVertex?.let {
                graph.addEdge(note, noteVertex)
            }
        }
    }

    notes.forEach { file ->
        println("${file.relativePath} ${file.links}")
    }

    println("Connections from Parent")
    val startVertex = mapa["""Parent Note"""]
    DepthFirstIterator(graph, startVertex)
        .forEach { println(it.path) }


    val nodes = graph.vertexSet()
        .mapIndexed { index, vertex ->
            D3Node(id = index, name = vertex.relativePath)
        }
    val links = graph.edgeSet()
        .map { edge ->
            D3Link(
                source = nodes
                    .find { node -> node.name == graph.getEdgeSource(edge).relativePath }
                    ?.id!!,
                target = nodes
                    .find { node -> node.name == graph.getEdgeTarget(edge).relativePath }
                    ?.id!!
            )
        }

    println(Klaxon().toJsonString(D3(nodes = nodes, links = links)))

    // writeImageToFile(graph)
}

private fun writeImageToFile(graph: Graph<NoteVertex, DefaultEdge>) {
    val graphAdapter = JGraphXAdapter(graph)
    val layout: mxIGraphLayout = mxCircleLayout(graphAdapter)
    layout.execute(graphAdapter.defaultParent)

    val image = mxCellRenderer.createBufferedImage(graphAdapter, null, 2.0, Color.WHITE, true, null)
    val imgFile = File("kotlin-obsidian-graph/src/main/resources/graph.png")
    imgFile.createNewFile()
    ImageIO.write(image, "PNG", imgFile)
}