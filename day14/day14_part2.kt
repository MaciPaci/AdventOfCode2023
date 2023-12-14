import java.io.File

enum class TiltDirection(val step: Int) {
    NORTH(-1),
    WEST(-1),
    SOUTH(1),
    EAST(1)
}

const val totalCycles = 1000000000

val cacheRocks = mutableMapOf<Pair<TiltDirection, List<String>>, List<String>>()

fun main() {
    val filepath = "./day14/input.txt"

    val input = File(filepath).readText().trim().split("\n")

    val rotatedPlatform = rotateThePlatform(input)

    val totalLoad = transposeAndCalculateLoad(rotatedPlatform)

    println("Total load: $totalLoad")
}

fun transposeAndCalculateLoad(s: List<String>): Int {
    var totalLoad = 0
    val maxLoad = s.first().length
    val transposedPlatform = transposePlatform(s)
    transposedPlatform.forEach { col ->
        for ((i, elem) in col.withIndex()) {
            if (elem == 'O') {
                totalLoad += maxLoad - i
            }
        }
    }
    return totalLoad
}

fun rotateThePlatform(s: List<String>): List<String> {
    var platform = s
    val seen = mutableMapOf<List<String>, Int>()
    var cycleLength = 0
    var currentCycle = 0
    while (currentCycle < totalCycles) {
        if (platform in seen) {
            cycleLength = currentCycle - seen[platform]!!
            break
        }
        seen[platform] = currentCycle

        platform = tiltPlatform(platform, TiltDirection.NORTH)
        platform = tiltPlatform(platform, TiltDirection.WEST)
        platform = tiltPlatform(platform, TiltDirection.SOUTH)
        platform = tiltPlatform(platform, TiltDirection.EAST)

        currentCycle++
    }

    if (cycleLength > 0) {
        val remainingCycles = (totalCycles - currentCycle) % cycleLength
        for (c in 0 until remainingCycles) {
            platform = tiltPlatform(platform, TiltDirection.NORTH)
            platform = tiltPlatform(platform, TiltDirection.WEST)
            platform = tiltPlatform(platform, TiltDirection.SOUTH)
            platform = tiltPlatform(platform, TiltDirection.EAST)
        }
    }

    return platform
}

fun tiltPlatform(s: List<String>, direction: TiltDirection): List<String> {
    cacheRocks[direction to s]?.let { return it }

    val transposedPlatform = transposePlatform(s)

    val tiltedPlatform = mutableListOf<String>()
    when (direction) {
        TiltDirection.NORTH, TiltDirection.WEST -> {
            transposedPlatform.forEach { column ->
                tiltedPlatform.add(tiltRocks(column, column.length - 1, direction))
            }
        }

        TiltDirection.SOUTH, TiltDirection.EAST -> {
            transposedPlatform.forEach { column ->
                tiltedPlatform.add(tiltRocks(column, 0, direction))
            }
        }
    }
    cacheRocks[direction to s] = tiltedPlatform
    return tiltedPlatform
}

fun tiltRocks(s: String, i: Int, direction: TiltDirection, slidingRocks: Int = 0): String {
    val step = direction.step
    if (i < 0 || i >= s.length) {
        return s
    }
    when (s[i]) {
        '.' -> {
            if (slidingRocks != 0) {
                val newColumn: String = when (direction) {
                    TiltDirection.NORTH, TiltDirection.WEST -> slideTiltedRocksUp(s, i, slidingRocks)
                    TiltDirection.SOUTH, TiltDirection.EAST -> slideTiltedRocksDown(s, i, slidingRocks)
                }
                return tiltRocks(newColumn, i + step, direction, slidingRocks)
            } else {
                return tiltRocks(s, i + step, direction)
            }
        }

        '#' -> return tiltRocks(s, i + step, direction, 0)
        'O' -> return tiltRocks(s, i + step, direction, slidingRocks + 1)
    }
    return ""
}

fun slideTiltedRocksUp(s: String, i: Int, length: Int): String {
    val charArray = s.toMutableList().toCharArray()
    for (l in 0 until length) {
        charArray[i + l] = 'O'
    }
    charArray[i + length] = '.'
    return String(charArray)
}

fun slideTiltedRocksDown(s: String, i: Int, length: Int): String {
    val charArray = s.toMutableList().toCharArray()
    for (l in 0..length) {
        charArray[i - length + l] = 'O'
    }
    charArray[i - length] = '.'
    return String(charArray)
}

fun transposePlatform(rows: List<String>): List<String> {
    val transposedPlatform = mutableListOf<String>()
    for (colIndex in rows[0].indices) {
        val column = rows.joinToString("") { it[colIndex].toString() }
        transposedPlatform.add(column)
    }
    return transposedPlatform
}
