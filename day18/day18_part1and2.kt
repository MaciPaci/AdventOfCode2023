import java.io.File
import kotlin.math.abs

enum class DigDirection(val code: String, val nextStep: Pair<Int, Int>) {
    UP("U", Pair(-1, 0)),
    DOWN("D", Pair(1, 0)),
    RIGHT("R", Pair(0, 1)),
    LEFT("L", Pair(0, -1));

    companion object {
        fun getStepForDirection(code: String): Pair<Int, Int> {
            return entries.first { it.code == code }.nextStep
        }
    }
}

fun main() {
    val filepath = "./day18/input.txt"

    val digPlan = File(filepath).readLines().map { it.split(' ') }
    val vertices = mutableListOf<Pair<Long, Long>>()
    var perimeter = 0L
    var newRow = 0
    var newCol = 0
    digPlan.forEach { row ->
        val direction = DigDirection.getStepForDirection(row.first())
        val steps = row[1].toInt()
        newRow += direction.first * steps
        newCol += direction.second * steps
        vertices.add(Pair(newRow.toLong(),newCol.toLong()))
        perimeter += steps
    }

    val area = solve(vertices, perimeter)
    println("Area of the excavation: $area")

    /**
     * PART 2
     */

    val vertices2 = mutableListOf<Pair<Long, Long>>()
    var perimeter2 = 0L
    var newRow2 = 0L
    var newCol2 = 0L
    digPlan.forEach { row ->
        val (dir, distance) = hexToDistanceDirection(row[2].trim('(',')','#'))
        val direction = DigDirection.getStepForDirection(dir)
        newRow2 += direction.first * distance
        newCol2 += direction.second * distance
        vertices2.add(Pair(newRow2.toLong(),newCol2.toLong()))
        perimeter2 += distance
    }

    val area2 = solve(vertices2, perimeter2)
    println("Area of the excavation for part 2: $area2")

}

fun hexToDistanceDirection(hexCode: String): Pair<String, Int> {
    val distanceHex = hexCode.substring(0, hexCode.length - 1)
    val directionHex = hexCode.last()

    val distance = distanceHex.toInt(16)
    val directionMapping = mapOf('0' to "R", '1' to "D", '2' to "L", '3' to "U")
    val direction = directionMapping[directionHex]!!

    return Pair(direction, distance)
}

fun solve(vertices: MutableList<Pair<Long, Long>>, perimeter: Long): Long {
    val area = shoelace(vertices)
    return area - perimeter / 2L + 1L + perimeter
}

fun shoelace(vertices: List<Pair<Long, Long>>): Long {
    var area = 0L
    for ((v1, v2) in vertices.zipWithNext()) {
        val (x1, y1) = v1
        val (x2, y2) = v2
        area += x1 * y2 - x2 * y1
    }
    return (abs(area) / 2L)
}
