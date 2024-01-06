import java.io.File
import kotlin.math.abs

private data class Hailstone(
    val x: Long,
    val y: Long,
    val z: Long,
    val vx: Long,
    val vy: Long,
    val vz: Long,
//    y = ax + b
    var a: Double = 0.0,
    var b: Double = 0.0
)

fun main() {
    val filepath = "./day24/input.txt"
    val minArea = 200000000000000
    val maxArea = 400000000000000

    val input = File(filepath).readLines()
    val hailstones = parseHailstones(input)
    getLineCoefficients(hailstones)

    val intersectionsInsideArea = checkForIntersectionInsideArea(hailstones, minArea, maxArea)
    println("Intersections within the test area: $intersectionsInsideArea")

    /**
     * Part 2
     */

    val potentialVelocities = findPotentialRockVelocities(hailstones)
    val throwingPosition = findRockThrowingPosition(potentialVelocities, hailstones[0], hailstones[1])
    println("Rock throwing position sum: ${throwingPosition.first + throwingPosition.second + throwingPosition.third}")
}

private fun findRockThrowingPosition(
    potentialVelocities: Triple<Long, Long, Long>,
    hailstone1: Hailstone,
    hailstone2: Hailstone
): Triple<Long, Long, Long> {
    val (potentialVX, potentialVY, potentialVZ) = potentialVelocities
    val m1 = (hailstone1.vy - potentialVY).toDouble() / (hailstone1.vx - potentialVX).toDouble()
    val m2 = (hailstone2.vy - potentialVY).toDouble() / (hailstone2.vx - potentialVX).toDouble()
    val c1 = hailstone1.y - (m1 * hailstone1.x)
    val c2 = hailstone2.y - (m2 * hailstone2.x)
    val xPos = ((c2 - c1) / (m1 - m2)).toLong()
    val yPos = (m1 * xPos + c1).toLong()
    val time = (xPos - hailstone1.x) / (hailstone1.vx - potentialVX)
    val zPos = hailstone1.z + (hailstone1.vz - potentialVZ) * time
    return Triple(xPos, yPos, zPos)
}

private fun findPotentialRockVelocities(
    hailstones: List<Hailstone>,
): Triple<Long, Long, Long> {
    var potentialXVelocities = mutableSetOf<Long>()
    var potentialYVelocities = mutableSetOf<Long>()
    var potentialZVelocities = mutableSetOf<Long>()
    for ((index, hailstone) in hailstones.withIndex()) {
        for (i in index + 1 until hailstones.size) {
            if (hailstone.vx == hailstones[i].vx && abs(hailstone.vx) > 100) {
                potentialXVelocities =
                    calculatePotentialSet(hailstone.x, hailstone.vx, hailstones[i].x, potentialXVelocities)
            }
            if (hailstone.vy == hailstones[i].vy && abs(hailstone.vy) > 100) {
                potentialYVelocities =
                    calculatePotentialSet(hailstone.y, hailstone.vy, hailstones[i].y, potentialYVelocities)
            }
            if (hailstone.vz == hailstones[i].vz && abs(hailstone.vz) > 100) {
                potentialZVelocities =
                    calculatePotentialSet(hailstone.z, hailstone.vz, hailstones[i].z, potentialZVelocities)
            }
        }
    }
    return Triple(potentialXVelocities.first(), potentialYVelocities.first(), potentialZVelocities.first())
}

private fun calculatePotentialSet(
    hailstone1Pos: Long,
    hailstone1V: Long,
    hailstone2Pos: Long,
    potentialSet: Set<Long>
): MutableSet<Long> {
    var potentialSetCopy = potentialSet
    val newSetX = mutableSetOf<Long>()
    val difference = hailstone2Pos - hailstone1Pos

    for (v in -1000L until 1000L) {
        if (v == hailstone1V) {
            continue
        }
        if (difference % (v - hailstone1V) == 0L) {
            newSetX.add(v)
        }
    }
    potentialSetCopy = if (potentialSetCopy.isNotEmpty()) {
        (potentialSetCopy intersect newSetX) as MutableSet<Long>
    } else {
        newSetX.toMutableSet()
    }
    return potentialSetCopy
}

private fun checkForIntersectionInsideArea(
    hailstones: List<Hailstone>,
    minArea: Long,
    maxArea: Long,
): Int {
    var intersections = 0
    for ((index, hailstone) in hailstones.withIndex()) {
        for (i in index + 1 until hailstones.size) {
            val (W, Wx, Wy) = calculateMatrixCoefficients(hailstone, hailstones[i])
            if (W != 0.0) {
                val pointOfIntersection = Pair(Wx / W, Wy / W)
                if (intersectionInsideArea(pointOfIntersection, minArea, maxArea)
                    && intersectionInTheFuture(pointOfIntersection, hailstone)
                    && intersectionInTheFuture(pointOfIntersection, hailstones[i])
                ) {
                    intersections++
                }
            }
        }
    }
    return intersections
}

private fun calculateMatrixCoefficients(
    firstHailstone: Hailstone,
    secondHailstone: Hailstone
): Triple<Double, Double, Double> {
    val W = secondHailstone.a - firstHailstone.a
    val Wx = firstHailstone.b - secondHailstone.b
    val Wy = firstHailstone.b * secondHailstone.a - secondHailstone.b * firstHailstone.a
    return Triple(W, Wx, Wy)
}

private fun getLineCoefficients(hailstones: List<Hailstone>) {
    for (hailstone in hailstones) {
        val secondPoint = Pair(hailstone.x + hailstone.vx, hailstone.y + hailstone.vy)
        hailstone.a = (hailstone.y - secondPoint.second).toDouble() / (hailstone.x - secondPoint.first).toDouble()
        hailstone.b = hailstone.y - hailstone.a * hailstone.x
    }
}

private fun parseHailstones(input: List<String>): MutableList<Hailstone> {
    val hailstones: MutableList<Hailstone> = mutableListOf()
    input.forEach { line ->
        val parsedInput = line.split('@').map { it.split(',').map { it.trim().toLong() } }
        hailstones.add(
            Hailstone(
                parsedInput[0][0],
                parsedInput[0][1],
                parsedInput[0][2],
                parsedInput[1][0],
                parsedInput[1][1],
                parsedInput[1][2]
            )
        )
    }
    return hailstones
}

private val intersectionInsideArea: (Pair<Double, Double>, Long, Long) -> Boolean =
    { pointOfIntersection, minArea, maxArea ->
        pointOfIntersection.first in minArea.toDouble()..maxArea.toDouble() &&
                pointOfIntersection.second in minArea.toDouble()..maxArea.toDouble()
    }

private val intersectionInTheFuture: (Pair<Double, Double>, Hailstone) -> Boolean = { pointOfIntersection, hailstone ->
    val firstCondition: Boolean = if (hailstone.vx >= 0) {
        pointOfIntersection.first >= hailstone.x
    } else {
        pointOfIntersection.first <= hailstone.x
    }
    val secondCondition: Boolean = if (hailstone.vy >= 0) {
        pointOfIntersection.second >= hailstone.y
    } else {
        pointOfIntersection.second <= hailstone.y
    }
    firstCondition && secondCondition
}
