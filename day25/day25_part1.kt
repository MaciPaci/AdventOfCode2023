import java.io.File
import java.util.LinkedList
import java.util.Queue

fun main() {
    val filepath = "./day25/input.txt"

    val input = File(filepath).readLines().map { it.replace(":", "").split(' ') }

    val graph: MutableMap<String, MutableSet<String>> = buildGraph(input)
    val nodePairs: MutableList<Pair<String, String>> = getNodePairs(graph)
    val randomPairs = nodePairs.shuffled().take(300)

    val mostVisitedEdges: MutableMap<Pair<String, String>, Int> = getMostVisitedEdges(randomPairs, graph)
    val sortedMostVisitedEdges = mostVisitedEdges.entries.sortedByDescending { it.value }.map { it.key }.take(6)
    val edgesToCut: MutableList<Pair<String, String>> = getEdgesToCut(sortedMostVisitedEdges)
    for (edge in edgesToCut) {
        removeConnection(graph, edge.first, edge.second)
    }

    val group1 = countReachableNodes(graph, edgesToCut[0].first)
    val group2 = countReachableNodes(graph, edgesToCut[0].second)
    println("Sizes of the groups multiplied: ${group1*group2}")
}

private fun countReachableNodes(graph: Map<String, MutableSet<String>>, node: String): Int {
    val queue: Queue<String> = LinkedList()
    queue.add(node)
    val visited: MutableSet<String> = mutableSetOf()
    while (queue.isNotEmpty()) {
        val currentNode = queue.remove()
        visited.add(currentNode)
        val connections = graph[currentNode]!!
        for (conn in connections) {
            if (conn in visited) {
                continue
            }
            queue.add(conn)
        }
    }
    return visited.size
}

private fun removeConnection(graph: Map<String, MutableSet<String>>, edge1: String, edge2: String) {
        graph[edge1]!!.remove(edge2)
        graph[edge2]!!.remove(edge1)
}

private fun getEdgesToCut(sortedMostVisitedEdges: List<Pair<String, String>>): MutableList<Pair<String, String>> {
    val edgesToCut: MutableList<Pair<String, String>> = mutableListOf()
    for ((firstNode, secondNode) in sortedMostVisitedEdges) {
        if (edgesToCut.none { pair -> pair.first == firstNode || pair.second == firstNode }) {
            edgesToCut.add(Pair(firstNode, secondNode))
        }
    }
    return edgesToCut
}

private fun getMostVisitedEdges(
    randomPairs: List<Pair<String, String>>,
    graph: Map<String, MutableSet<String>>
): MutableMap<Pair<String, String>, Int> {
    val visitedEdges: MutableMap<Pair<String, String>, Int> = mutableMapOf()
    for (pair in randomPairs) {
        val (start, destination) = pair
        val queue: Queue<Pair<String, MutableList<String>>> = LinkedList()
        queue.add(Pair(start, mutableListOf()))
        while (queue.isNotEmpty()) {
            val (currentNode, visited) = queue.remove()
            val visitedCopy = visited.toMutableList()
            visitedCopy.add(currentNode)

            if (currentNode == destination) {
                visited.add(destination)
                for (index in 0 until visited.size - 1) {
                    visitedEdges[Pair(visited[index], visited[index + 1])] =
                        visitedEdges.getOrDefault(Pair(visited[index], visited[index + 1]), 0) + 1
                }
                break
            }
            val connections = graph[currentNode]!!
            for (conn in connections) {
                if (conn in visited) {
                    continue
                }
                queue.add(Pair(conn, visitedCopy))
            }
        }
    }
    return visitedEdges
}

private fun getNodePairs(graph: MutableMap<String, MutableSet<String>>): MutableList<Pair<String, String>> {
    val nodePairs: MutableList<Pair<String, String>> = mutableListOf()
    for (node in graph.keys) {
        val restOfNodes = graph.filter { it.key != node }.keys
        for (n in restOfNodes) {
            nodePairs.add(Pair(node, n))
        }
    }
    return nodePairs
}

private fun buildGraph(input: List<List<String>>): MutableMap<String, MutableSet<String>> {
    val graph: MutableMap<String, MutableSet<String>> = mutableMapOf()
    for (line in input) {
        val startNode = line[0]
        val connections = line.filter { it != startNode }
        graph.getOrPut(startNode) { mutableSetOf() }.addAll(connections)
        for (node in connections) {
            graph.getOrPut(node) { mutableSetOf() }.add(startNode)
        }
    }
    return graph
}
