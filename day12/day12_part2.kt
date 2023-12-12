import  java.io.File

data class CogsUnfolded(
    val row: String,
    val groups: MutableList<Int>,
)

val cache = mutableMapOf<Pair<String, List<Int>>, Long>()

fun main() {
    val filePath = "./day12/input.txt"

    val unfoldedCogsList = File(filePath).readLines().map { line ->
        val input = line.split(' ')
        val row = input.first().repeat(5).chunked(input.first().length).joinToString("?")
        val repeatedGroups = input.last().repeat(5).chunked(input.last().length).joinToString(",")
        val groups = repeatedGroups.split(',').map { it.toInt() }.toMutableList()
        CogsUnfolded(row, groups)
    }

    var sumOfUnfoldedVariants = 0L
    for (cogs in unfoldedCogsList) {
        sumOfUnfoldedVariants += solve(cogs.row + '.', cogs.groups)
    }

    println("Sum of possible unfolded spring variants: $sumOfUnfoldedVariants")
}

fun solve(s: String, groups: List<Int>): Long {
    if (s.length <= 1) {
        if (groups.isEmpty()) {
            return 1L
        } else {
            return 0L
        }
    }

    var result = 0L

    when (s.first()) {
        '.' -> result += solve(
            s.substring(1),
            groups
        )

        '?' -> result += solve(
            s.substring(1),
            groups,
        ) + solveHash(
            s,
            groups,
        ).also { cache[s to groups] = it }

        '#' -> result += solveHash(
            s,
            groups,
        ).also { cache[s to groups] = it }

    }
    return result
}

fun solveHash(s: String, groups: List<Int>): Long {
    cache[s to groups]?.let { return it }

    if (groups.isEmpty()) {
        return 0
    }

    val group = groups.first()
    if (s.length < group) return 0
    for (i in 0 until group) {
        if (s[i] == '.') return 0
    }
    if (s.length == group) {
        if (groups.size == 1) return 1
        return 0
    }
    if (s[group] == '#') return 0
    return solve(s.substring(group + 1), groups.subList(1, groups.size).toList())
}
