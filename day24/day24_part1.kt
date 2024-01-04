import java.io.File

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
    println("Intersection within the test area: $intersectionsInsideArea")
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

private fun getLineCoefficients(hailstones: List<Hailstone>){
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
        hailstones.add(Hailstone(parsedInput[0][0], parsedInput[0][1], parsedInput[0][2], parsedInput[1][0], parsedInput[1][1], parsedInput[1][2]))
    }
    return hailstones
}

private val intersectionInsideArea: (Pair<Double, Double>, Long, Long) -> Boolean = {
    pointOfIntersection, minArea, maxArea ->
    pointOfIntersection.first in minArea.toDouble()..maxArea.toDouble() &&
            pointOfIntersection.second in minArea.toDouble()..maxArea.toDouble()
}

private val intersectionInTheFuture: (Pair<Double,Double>, Hailstone) -> Boolean = {
    pointOfIntersection, hailstone ->
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
