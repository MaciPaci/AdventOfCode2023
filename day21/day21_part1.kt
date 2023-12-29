import java.io.File
import java.util.Queue
import kotlin.math.abs

enum class StepDirection(val nexStep: Pair<Int, Int>) {
    NORTH(Pair(-1, 0)),
    WEST(Pair(0, -1)),
    EAST(Pair(0, 1)),
    SOUTH(Pair(1, 0));
}

private val stepDirection = listOf(StepDirection.NORTH, StepDirection.SOUTH, StepDirection.EAST, StepDirection.WEST)

data class Position(
    val xy: Pair<Int, Int>,
    val step: Int
)

fun main() {
    val filepath = "./day21/input.txt"

    val grid = File(filepath).readLines().map { it.toList() }
    val lineCount = grid.size
    val lineLength = grid.first().size
    val startingPosition = findIndex2D(grid, 'S')!!

//    val positionsQueue: Queue<Position> = LinkedList()
//    positionsQueue.add(Position(startingPosition, 0))

    val positionsQueue: MutableSet<Pair<Pair<Int, Int>, Int>> = mutableSetOf()
    positionsQueue.add(Pair(startingPosition, 0))

    walkThroughInfiniteGarden(64, positionsQueue, grid, lineCount, lineLength)
    println("Possible to reach garden plots: ${positionsQueue.size}")

    /**
     * PART 2 (doesn't work in finite time which is fun)
     */

    val positionsInfiniteQueue: MutableSet<Pair<Pair<Int, Int>, Int>> = mutableSetOf()
    positionsInfiniteQueue.add(Pair(startingPosition, 0))

    val reachedPlots = walkThroughInfiniteGarden(10, positionsInfiniteQueue, grid, lineCount, lineLength)
    println("Possible to reach plots in an infinite garden: $reachedPlots")
}

fun walkThroughInfiniteGarden(
    numberOfSteps: Int,
    positionsQueue: MutableSet<Pair<Pair<Int, Int>, Int>>,
    grid: List<List<Char>>,
    lineLength: Int,
    lineCount: Int
): Int {
    var stepsDone = 0

    val baseGrid = getBaseGrid(lineCount, lineLength, grid)

    while (stepsDone < numberOfSteps) {
        val moveFrom = positionsQueue.first()
        positionsQueue.remove(moveFrom)

        val baseGridCoords = getBaseCoords(moveFrom.first.first, moveFrom.first.second, lineCount, lineLength)
        for (direction in baseGrid[baseGridCoords]!!) {
            positionsQueue.add(
                Pair(
                    Pair(
                        moveFrom.first.first + direction.nexStep.first,
                        moveFrom.first.second + direction.nexStep.second
                    ),
                    moveFrom.second + 1
                )
            )
        }
        stepsDone = positionsQueue.first().second
    }
    return positionsQueue.size
}

private fun getBaseGrid(
    lineCount: Int,
    lineLength: Int,
    grid: List<List<Char>>
): MutableMap<Pair<Int, Int>, MutableList<StepDirection>> {
    val baseGrid: MutableMap<Pair<Int, Int>, MutableList<StepDirection>> = mutableMapOf()
    for (row in 0 until lineCount) {
        for (col in 0 until lineLength) {
            for (direction in stepDirection) {
                var destinationRow = row + direction.nexStep.first
                var destinationCol = col + direction.nexStep.second
                when {
                    destinationRow < 0 -> destinationRow = lineCount - 1
                    destinationRow >= lineCount -> destinationRow = 0
                    destinationCol < 0 -> destinationCol = lineLength - 1
                    destinationCol >= lineLength -> destinationCol = 0
                }
                val destination = grid[destinationRow][destinationCol]
                if (destination == '.' || destination == 'S') {
                    baseGrid.getOrPut(Pair(row, col)) { mutableListOf() }.add(direction)
                }
            }
        }
    }
    return baseGrid
}

private fun getBaseCoords(
    row: Int,
    col: Int,
    lineCount: Int,
    lineLength: Int
): Pair<Int, Int> {
    var baseGridRow = row % lineCount
    var baseGridCol = col % lineLength
    when {
        row < 0 && col < 0 -> {
            baseGridRow = lineCount - 1 - abs((row + 1) % lineCount)
            baseGridCol = lineLength - 1 - abs((col + 1) % lineLength)
        }

        row < 0 -> baseGridRow = lineCount - 1 - abs((row + 1) % lineCount)
        col < 0 -> baseGridCol = lineLength - 1 - abs((col + 1) % lineLength)
    }
    return Pair(baseGridRow, baseGridCol)
}

// Worse performing solution to P1
fun walkThroughGarden(
    numberOfSteps: Int,
    positionsQueue: Queue<Position>,
    grid: List<List<Char>>,
    lineLength: Int,
    lineCount: Int
) {
    val cache: MutableMap<Pair<Int, Int>, MutableList<Pair<Int, Int>>> = mutableMapOf()
    var stepsDone = 0
    while (stepsDone < numberOfSteps) {
        val moveFrom = positionsQueue.remove()
        if (moveFrom.xy in cache) {
            positionsQueue.addAll(cache[moveFrom.xy]
                ?.filterNot { newPosition ->
                    positionsQueue.any { it.xy == newPosition }
                }
                ?.map { newPosition ->
                    Position(newPosition, moveFrom.step + 1)
                } ?: continue
            )
            stepsDone = positionsQueue.minBy { it.step }.step
            continue
        }
        for (direction in stepDirection) {
            val destinationRow = moveFrom.xy.first + direction.nexStep.first
            val destinationCol = moveFrom.xy.second + direction.nexStep.second
            if (destinationRow < 0 || destinationRow >= lineCount || destinationCol < 0 || destinationCol >= lineLength) {
                continue
            }
            val destination = grid[destinationRow][destinationCol]
            if (destination == '.' || destination == 'S') {
                val nextPosition = Position(Pair(destinationRow, destinationCol), moveFrom.step + 1)
                cache.getOrPut(moveFrom.xy) { mutableListOf() }.add(nextPosition.xy)
                if (nextPosition !in positionsQueue) {
                    positionsQueue.add(nextPosition)
                }
            }
        }
        stepsDone = positionsQueue.minBy { it.step }.step
    }
}

fun <T> findIndex2D(list: List<List<T>>, element: T): Pair<Int, Int>? {
    for ((i, row) in list.withIndex()) {
        val columnIndex = row.indexOf(element)
        if (columnIndex != -1) {
            return Pair(i, columnIndex)
        }
    }
    return null
}
