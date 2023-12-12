import  java.io.File

data class Cogs(
    val row: String,
    val groups: MutableList<Int>,
)

var sumOfVariants = 0

fun main() {
    val filePath = "./day12/input.txt"

    val cogsList = File(filePath).readLines().map { line ->
        val input = line.split(' ')
        val row = input.first()
        val groups = input.last().split(',').map { it.toInt() }.toMutableList()
        Cogs(row, groups)
    }

    for (cogs in cogsList) {
        generateVariants(cogs.row + '.', cogs.groups)
    }

    println("Sum of possible spring variants: $sumOfVariants")
}

fun generateVariants(s: String, groups: List<Int>, hashGroupLength: Int = 0) {
    if (s.length == 1) {
        if (hashGroupLength != 0 && groups.size == 1 && hashGroupLength == groups.first()) {
            sumOfVariants += 1
        } else if (groups.isEmpty()) {
            sumOfVariants += 1
        }
        return
    }

    when (s.first()) {
        '.' -> {
            if (hashGroupLength != 0) {
                if (hashGroupLength == groups.first()) {
                    generateVariants(s.substring(1), groups.subList(1, groups.size).toList(), 0)
                } else {
                    return
                }
            } else {
                generateVariants(s.substring(1), groups, hashGroupLength)
            }
        }

        '?' -> {
            generateVariants('#' + s.substring(1), groups, hashGroupLength)
            generateVariants('.' + s.substring(1), groups, hashGroupLength)
        }

        '#' -> {
            if (groups.isEmpty()) {
                return
            }
            generateVariants(s.substring(1), groups, hashGroupLength + 1)
        }
    }
}
