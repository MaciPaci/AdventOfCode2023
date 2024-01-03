import java.io.File
import java.util.*

private data class WalkNode(
    val coords: Pair<Int, Int>,
    val connectedHubs: MutableMap<Pair<Int, Int>, Int>
)

fun main() {
    val filepath = "./day23/input.txt"

    val grid = File(filepath).readLines().map { it.toList() }
    val lineCount = grid.size
    val lineLength = grid.first().size
    val (startPoint, endPoint) = findStartAndEndPoints(grid)

    val hubs = findHubs(grid, startPoint, endPoint, lineCount, lineLength)
    val graph = generateGraph(grid, hubs, lineCount, lineLength)

    val startNode = graph.first { it.coords == startPoint }
    val finishedPaths: MutableList<Pair<List<Pair<Int, Int>>, Int>> = mutableListOf()
    calculatePaths(graph, mutableListOf(startNode.coords), 0, endPoint, finishedPaths)
    println("Longest possible dry hike: ${finishedPaths.maxBy { it.second }.second}")
}

private fun calculatePaths(
    graph: MutableList<WalkNode>,
    path: List<Pair<Int, Int>>,
    pathLength: Int,
    endPoint: Pair<Int, Int>,
    finishedPaths: MutableList<Pair<List<Pair<Int, Int>>, Int>>,
) {
    if (path.last() == endPoint) {
        finishedPaths.add(Pair(path, pathLength))
    } else {
        val nextHub = graph.first { it.coords == path.last() }
        for ((hubCoord, lengthToHub) in nextHub.connectedHubs) {
            if (hubCoord in path) {
                continue
            }
            val newPath = path.toMutableList()
            newPath.add(hubCoord)
            calculatePaths(graph, newPath, pathLength + lengthToHub, endPoint, finishedPaths)
        }
    }
}

private fun generateGraph(
    grid: List<List<Char>>,
    hubs: MutableList<Pair<Int, Int>>,
    lineCount: Int,
    lineLength: Int,
): MutableList<WalkNode> {
    val graph = mutableListOf<WalkNode>()
    val walkSteps: List<WalkStep> = hubs.map { pair ->
        WalkStep(coords = pair)
    }
    val stack = ArrayDeque<WalkStep>()
    for (walkStep in walkSteps) {
        stack.push(walkStep)
    }
    while (stack.isNotEmpty()) {
        val node = stack.pop()
        val connectedHubs = traverseDryMap(node, grid, lineCount, lineLength, hubs)
        graph.add(WalkNode(node.coords, connectedHubs))
    }
    return graph
}

private fun findHubs(
    grid: List<List<Char>>,
    startPoint: Pair<Int, Int>,
    endPoint: Pair<Int, Int>,
    lineCount: Int,
    lineLength: Int
): MutableList<Pair<Int, Int>> {
    val hubs = mutableListOf<Pair<Int, Int>>()
    for ((rowIdx, row) in grid.withIndex()) {
        for ((colIdx, col) in row.withIndex()) {
            if (col == '#') {
                continue
            }
            if (Pair(rowIdx, colIdx) == startPoint || Pair(rowIdx, colIdx) == endPoint ||
                getNeighbours(grid, Pair(rowIdx, colIdx), lineCount, lineLength) > 2
            ) {
                hubs.add(Pair(rowIdx, colIdx))
            }
        }
    }
    return hubs
}

private fun getNeighbours(grid: List<List<Char>>, currentPoint: Pair<Int, Int>, lineCount: Int, lineLength: Int): Int {
    var neighbours = 0
    for (dir in walkStepDirection) {
        val nextPoint = Pair(currentPoint.first + dir.nexStep.first, currentPoint.second + dir.nexStep.second)
        if (isOutOfBounds(nextPoint, lineCount, lineLength) || grid[nextPoint.first][nextPoint.second] == '#') {
            continue
        } else {
            neighbours++
        }
    }
    return neighbours
}

private fun traverseDryMap(
    node: WalkStep,
    grid: List<List<Char>>,
    lineCount: Int,
    lineLength: Int,
    hubs: MutableList<Pair<Int, Int>>
): MutableMap<Pair<Int, Int>, Int> {
    val stack = ArrayDeque<WalkStep>()
    stack.push(node)
    val connectedHubs: MutableMap<Pair<Int, Int>, Int> = mutableMapOf()
    while (stack.isNotEmpty()) {
        val currentStep = stack.pop()
        val currentPoint = currentStep.coords
        val visited = currentStep.visited.map { it.copy() }.toMutableList()
        val stepCount = currentStep.stepCount
        if (currentPoint in hubs && currentPoint != node.coords) {
            connectedHubs[currentPoint] = stepCount
            continue
        }
        visited.add(currentPoint)
        for (dir in walkStepDirection) {
            val nextPoint = Pair(currentPoint.first + dir.nexStep.first, currentPoint.second + dir.nexStep.second)
            if (isOutOfBounds(nextPoint, lineCount, lineLength) || nextPoint in visited) {
                continue
            }
            when (grid[nextPoint.first][nextPoint.second]) {
                '#' -> continue
                else -> stack.push(WalkStep(nextPoint, visited, stepCount + 1))
            }
        }
    }
    return connectedHubs
}

private fun findStartAndEndPoints(grid: List<List<Char>>): Pair<Pair<Int, Int>, Pair<Int, Int>> {
    val numRows = grid.size
    val numCols = grid[0].size
    var startIndex = -1
    var endIndex = -1
    for (col in 0..<numCols) {
        if (grid[0][col] == '.') {
            startIndex = col
            break
        }
    }
    for (col in 0..<numCols) {
        if (grid[numRows - 1][col] == '.') {
            endIndex = col
            break
        }
    }
    return Pair(Pair(0, startIndex), Pair(numRows - 1, endIndex))
}
