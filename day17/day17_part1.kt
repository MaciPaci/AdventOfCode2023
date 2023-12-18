import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.LinkedList
import java.util.Queue

data class BlockNode(
    val row: Int,
    val col: Int,
    val direction: MoveDirection,
    val stepsInDirection: Int,
)

enum class MoveDirection(val nextMove: Pair<Int, Int>) {
    DOWN(Pair(1, 0)),
    UP(Pair(-1, 0)),
    LEFT(Pair(0, -1)),
    RIGHT(Pair(0, 1));

    fun reverseDirection(): MoveDirection {
        return when (this) {
            UP -> DOWN
            DOWN -> UP
            RIGHT -> LEFT
            LEFT -> RIGHT
        }
    }
}

val neighbours = listOf(MoveDirection.UP, MoveDirection.DOWN, MoveDirection.RIGHT, MoveDirection.LEFT)

const val filepathDay17 = "./day17/input.txt"
val lineLengthDay17 = File(filepathDay17).bufferedReader().use { it.readLine() }.length
val lineCountDay17 = Files.lines(Paths.get(filepathDay17)).count().toInt()

fun main() {
    val startNode = Pair(0, 0)
    val endNode = Pair(lineCountDay17 - 1, lineLengthDay17 - 1)

    val grid = File(filepathDay17).readText().trim().split('\n').map { row -> row.map { it.code - '0'.code } }.toList()

    val queue: Queue<Pair<BlockNode, Int>> = LinkedList()
    queue.add(Pair(BlockNode(startNode.first, startNode.second, MoveDirection.RIGHT, 0), 0))

    val visited = calculateDistanceToTarget(queue, grid, false)

    var heatLoss = Int.MAX_VALUE
    visited.forEach { node ->
        if (node.key.row == endNode.first && node.key.col == endNode.second && node.value < heatLoss) {
            heatLoss = node.value
        }
    }
    println("Smallest possible heat loss: $heatLoss")

    /**
     * Part 2
     */

    val queueUltraCrucible: Queue<Pair<BlockNode, Int>> = LinkedList()
    queueUltraCrucible.add(Pair(BlockNode(startNode.first, startNode.second, MoveDirection.RIGHT, -1), 0))

    val visitedUltraCrucible = calculateDistanceToTarget(queueUltraCrucible, grid, true)
    var heatLossUltraCrucible = Int.MAX_VALUE
    visitedUltraCrucible.forEach { node ->
        if (node.key.row == endNode.first
            && node.key.col == endNode.second
            && node.value < heatLossUltraCrucible
            && node.key.stepsInDirection >= 4
        ) {
            heatLossUltraCrucible = node.value
        }
    }
    println("Smallest possible heat loss in ultra crucible: $heatLossUltraCrucible")
}

private fun calculateDistanceToTarget(
    queue: Queue<Pair<BlockNode, Int>>,
    grid: List<List<Int>>,
    ultraCrucible: Boolean
): MutableMap<BlockNode, Int> {
    val visited = mutableMapOf<BlockNode, Int>()
    while (queue.isNotEmpty()) {

        val currentNode = queue.remove()
        if (currentNode.first in visited && currentNode.second >= visited[currentNode.first]!!) {
            continue
        }
        visited[currentNode.first] = currentNode.second

        for (n in neighbours) {
            if (n == currentNode.first.direction.reverseDirection()) {
                continue
            }

            val nRow = currentNode.first.row + n.nextMove.first
            val nCol = currentNode.first.col + n.nextMove.second
            if (nRow !in 0 until lineCountDay17 || nCol !in 0 until lineLengthDay17) {
                continue
            }

            val stepsInDirection = if (n != currentNode.first.direction) {
                1
            } else {
                currentNode.first.stepsInDirection + 1
            }
            if (ultraCrucible) {
                if (!((stepsInDirection < 10 || n != currentNode.first.direction) &&
                            (n == currentNode.first.direction
                                    || currentNode.first.stepsInDirection >= 4
                                    || currentNode.first.stepsInDirection == -1))
                ) {
                    continue
                }
            } else {
                if (stepsInDirection > 3) {
                    continue
                }
            }

            val cost = grid[nRow][nCol]
            queue.add(Pair(BlockNode(nRow, nCol, n, stepsInDirection), currentNode.second + cost))
        }
    }
    return visited
}
