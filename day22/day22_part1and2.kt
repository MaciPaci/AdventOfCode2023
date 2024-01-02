import java.io.File
import kotlin.math.max

fun main() {
    val filepath = "./day22/input.txt"

    val input = File(filepath).readLines()
    val coordsList: MutableList<List<List<Int>>> = mutableListOf()
    input.forEach { line ->
        val coordinates = line.split('~').map { it.split(',').map { it.toInt() } }
        coordsList.add(coordinates)
    }
    val sortedCoordinatesList = coordsList.sortedBy { it[0][2] }
    val (newTower, _) = dropBricks(sortedCoordinatesList)

    var part1Falls = 0
    var part2Falls = 0
    for (brick in newTower) {
        val newTowerCopy = newTower.toMutableList()
        newTowerCopy.remove(brick)
        val (_, falls) = dropBricks(newTowerCopy)
        if (falls == 0) {
            part1Falls += 1
        } else {
            part2Falls += falls
        }
    }
    println("Bricks that can be safely disintegrated: $part1Falls")
    println("Bricks that would fall: $part2Falls")
}

private fun dropBricks(bricks: List<List<List<Int>>>): Pair<MutableList<List<List<Int>>>, Int> {
    val highest = mutableMapOf<Pair<Int, Int>, Int>()
    val newTower = mutableListOf<List<List<Int>>>()
    var falls = 0
    for (brick in bricks) {
        val newBrick = droppedBrick(brick, highest)
        if (newBrick[0][2] != brick[0][2]) {
            falls += 1
        }
        newTower.add(newBrick)
        for (x in brick[0][0]..brick[1][0]) {
            for (y in brick[0][1]..brick[1][1]) {
                highest[Pair(x, y)] = newBrick[1][2]
            }
        }
    }
    return Pair(newTower, falls)
}

private fun droppedBrick(
    brick: List<List<Int>>,
    highest: Map<Pair<Int, Int>, Int>,
): List<List<Int>> {
    var maxHeight = 0
    var newBrick = listOf(listOf<Int>())
    for (x in brick[0][0]..brick[1][0]) {
        for (y in brick[0][1]..brick[1][1]) {
            val currentCoords = Pair(x, y)
            if (highest.getOrDefault(currentCoords, -1) + 1 > maxHeight) {
                maxHeight = highest[currentCoords]!!
            }
            val diffInZ = max(brick[0][2] - maxHeight - 1, 0)
            newBrick = listOf(
                listOf(brick[0][0], brick[0][1], brick[0][2] - diffInZ),
                listOf(brick[1][0], brick[1][1], brick[1][2] - diffInZ)
            )
        }
    }
    return newBrick
}
