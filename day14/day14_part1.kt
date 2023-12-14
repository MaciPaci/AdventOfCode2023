import java.io.File

fun main() {
    val filepath = "./day14/input.txt"

    val input = File(filepath).readText().trim().split("\n")

    val columnPlatform = mutableListOf<String>()
    for (colIndex in input[0].indices) {
        val column = input.joinToString("") { it[colIndex].toString() }
        columnPlatform.add(column)
    }
    println(columnPlatform)

    val tiltedPlatform = mutableListOf<String>()
    columnPlatform.forEach { column ->
        tiltedPlatform.add(tiltThePlatform(column, column.length-1))
    }

    var totalLoad = 0
    tiltedPlatform.forEach { col->
        totalLoad += calculateLoad(col)
    }
}

fun calculateLoad(s: String): Int {
    val maxLoad = s.length
    var totalLoad = 0
    for ((i, elem) in s.withIndex()) {
        if (elem == 'O') {
            totalLoad += maxLoad - i
        }
    }
    return totalLoad
}

fun tiltThePlatform(s: String, i: Int, slidingRocks: Int = 0): String {
    if (i < 0) {
        return s
    }
    when (s[i]) {
        '.' -> {
            if (slidingRocks != 0) {
                val newColumn = slideRocks(s, i, slidingRocks)
                return tiltThePlatform(newColumn, i - 1, slidingRocks)
            } else {
                return tiltThePlatform(s, i - 1)
            }
        }
        '#' -> return tiltThePlatform(s, i - 1, 0)
        'O' -> return tiltThePlatform(s, i - 1, slidingRocks + 1)
    }
    return ""
}

fun slideRocks(s: String, i: Int, length: Int): String {
    val charArray = s.toMutableList().toCharArray()
    for (l in 0 until length) {
        charArray[i+l] = 'O'
    }
    charArray[i+length] = '.'
    return String(charArray)
}
