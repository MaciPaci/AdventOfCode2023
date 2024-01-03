import java.io.File
import java.util.*


internal enum class Dir(val nexStep: Pair<Int, Int>) {
    NORTH(Pair(-1, 0)),
    WEST(Pair(0, -1)),
    EAST(Pair(0, 1)),
    SOUTH(Pair(1, 0));
}

internal data class WalkStep(
    val coords: Pair<Int, Int>,
    val visited: MutableList<Pair<Int, Int>> = mutableListOf(),
    val stepCount: Int = 0
)

internal val walkStepDirection = listOf(Dir.NORTH, Dir.SOUTH, Dir.EAST, Dir.WEST)

internal val isOutOfBounds: (Pair<Int, Int>, Int, Int) -> Boolean = { nextPoint, lineCount, lineLength ->
    listOf(
        nextPoint.first < 0,
        nextPoint.first >= lineCount,
        nextPoint.second < 0,
        nextPoint.second >= lineLength
    ).any { it }
}

fun main() {
    val filepath = "./day23/input.txt"

    val grid = File(filepath).readLines().map { it.toList() }
    val lineCount = grid.size
    val lineLength = grid.first().size
    val (startPoint, endPoint) = findStartAndEndPoints(grid)

    val stack=  Stack<WalkStep>()
    stack.add(WalkStep(startPoint))
    val finishedWalks = traverseMap(stack, grid, lineCount, lineLength, endPoint)
    println("Longest possible hike: ${finishedWalks.max()}")
}

private fun traverseMap(
    stack: Stack<WalkStep>,
    grid: List<List<Char>>,
    lineCount: Int,
    lineLength: Int,
    endPoint: Pair<Int, Int>,
): MutableList<Int> {
    val finishedWalks = mutableListOf<Int>()
    while (stack.isNotEmpty()) {
        val currentStep = stack.pop()
        val currentPoint = currentStep.coords
        val visited = currentStep.visited.map { it.copy() }.toMutableList()
        val stepCount = currentStep.stepCount
        if (currentPoint == endPoint) {
            finishedWalks.add(stepCount)
        }
        visited.add(currentPoint)
        for (dir in walkStepDirection) {
            val nextPoint = Pair(currentPoint.first + dir.nexStep.first, currentPoint.second + dir.nexStep.second)
            if (isOutOfBounds(nextPoint, lineCount, lineLength) || nextPoint in visited) {
                continue
            }
            when (val nextPointSymbol = grid[nextPoint.first][nextPoint.second]) {
                '#' -> continue
                '.' -> stack.push(WalkStep(nextPoint, visited, stepCount + 1))
                else -> {
                    val slopePoint = rideSlope(nextPoint, nextPointSymbol)
                    visited.add(nextPoint)
                    stack.push(WalkStep(slopePoint, visited, stepCount + 2))
                }
            }
        }
    }
    return finishedWalks
}

private fun rideSlope(nextPoint: Pair<Int, Int>, symbol: Char): Pair<Int, Int> {
    return when (symbol) {
        '^' -> {
            Pair(nextPoint.first + Dir.NORTH.nexStep.first, nextPoint.second + Dir.NORTH.nexStep.second)
        }

        '>' -> {
            Pair(nextPoint.first + Dir.EAST.nexStep.first, nextPoint.second + Dir.EAST.nexStep.second)
        }

        '<' -> {
            Pair(nextPoint.first + Dir.WEST.nexStep.first, nextPoint.second + Dir.WEST.nexStep.second)
        }

        'v' -> {
            Pair(nextPoint.first + Dir.SOUTH.nexStep.first, nextPoint.second + Dir.SOUTH.nexStep.second)
        }

        else -> Pair(-1, -1)
    }
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
