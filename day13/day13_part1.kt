import java.io.File

fun main() {
    val filePath = "./day13/input.txt"

    val input = File(filePath).readText().trim()

    val patterns = input.split("\n\n")

    val rowPatterns = mutableListOf<List<String>>()
    val columnPatterns = mutableListOf<List<String>>()
    patterns.forEach { pattern ->
        val rows = pattern.split('\n')
        rowPatterns.add(rows)
        val columns = mutableListOf<String>()
        for (colIndex in rows[0].indices) {
            val column = rows.joinToString("") { it[colIndex].toString() }
            columns.add(column)
        }
        columnPatterns.add(columns)
    }

    var reflectionsAboveRow = 0
    var reflectionsLeftColumn = 0

    for (i in 0 until rowPatterns.size) {
        reflectionsAboveRow += findReflection(rowPatterns[i])
        reflectionsLeftColumn += findReflection(columnPatterns[i])
    }

    val summary = reflectionsLeftColumn + 100 * reflectionsAboveRow
    println("Notes summary: $summary")
}

fun findReflection(list: List<String>): Int {
    val firstElement = list.first()
    val lastElement = list.last()

    for (i in 1 until list.size - 1) {
        if (firstElement == list[i]) {
            val elementsBetween = i - 1
            if (elementsBetween % 2 == 0) {
                if (checkElementsBetween(list, 1, i - 1, elementsBetween)) {
                    return elementsBetween / 2 + 1
                }
            }
        }
        if (lastElement == list[i]) {
            val elementsBetween = list.indices.last-i - 1
            if (elementsBetween % 2 == 0) {
                if (checkElementsBetween(list, i + 1, list.indices.last - 1, elementsBetween)) {
                    return list.indices.last - elementsBetween / 2
                }
            }
        }
    }

    return 0
}

fun checkElementsBetween(list: List<String>, left: Int, right: Int, numsBetween: Int): Boolean {
    for (i in 0 until numsBetween / 2) {
        if (list[left + i] != list[right - i]) {
            return false
        }
    }
    return true
}
