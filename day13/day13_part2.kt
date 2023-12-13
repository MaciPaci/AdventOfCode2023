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

    val previousResults = mutableMapOf<Int, Pair<String, Int>>()
    for (i in 0 until rowPatterns.size) {
        val rowReflection = findFlippedReflection(rowPatterns[i], Pair("", 0), "row")
        if (rowReflection.first) {
            previousResults[i] = Pair("row", rowReflection.second)
        } else {
            val colReflection = findFlippedReflection(columnPatterns[i], Pair("", 0), "col")
            if (colReflection.first) {
                previousResults[i] = Pair("col", colReflection.second)
            }
        }
    }

    var reflectionsAboveRow = 0
    var reflectionsLeftColumn = 0
    for (i in 0 until rowPatterns.size) {
        var found = false
        var swapIndex = 0
        while (!found) {
            val newRowPattern = swapCharacterInPattern(rowPatterns[i], swapIndex)
            val rowReflection = findFlippedReflection(newRowPattern, previousResults.getValue(i), "row")
            if (rowReflection.first) {
                found = true
                reflectionsAboveRow += rowReflection.second
            } else {
                val newColPattern = swapCharacterInPattern(columnPatterns[i], swapIndex)
                val colReflection = findFlippedReflection(newColPattern, previousResults.getValue(i), "col")
                if (colReflection.first) {
                    found = true
                    reflectionsLeftColumn += colReflection.second
                }
            }
            swapIndex++
        }
    }

    val summary = reflectionsLeftColumn + 100 * reflectionsAboveRow
    println("Notes summary: $summary")
}

fun swapCharacterInPattern(list: List<String>, swapIndex: Int): List<String> {
    val rowIndex = swapIndex / list.first().length
    val colIndex = swapIndex % list.first().length
    val newList = list.toMutableList()
    val charArray = newList[rowIndex].toCharArray()
    charArray[colIndex] = if (charArray[colIndex] == '.') '#' else '.'
    newList[rowIndex] = String(charArray)
    return newList
}

fun findFlippedReflection(list: List<String>, cache: Pair<String, Int>, key: String): Pair<Boolean, Int> {
    val firstElement = list.first()
    val lastElement = list.last()

    for (i in 1 until list.size - 1) {
        if (firstElement == list[i]) {
            val elementsBetween = i - 1
            if (elementsBetween % 2 == 0) {
                if (checkElementsBetweenFlipped(list, 1, i - 1, elementsBetween) &&
                    cache != Pair(key, elementsBetween / 2 + 1)) {
                    return Pair(true, elementsBetween / 2 + 1)
                }
            }
        }
        if (lastElement == list[i]) {
            val elementsBetween = list.indices.last - i - 1
            if (elementsBetween % 2 == 0) {
                if (checkElementsBetweenFlipped(list, i + 1, list.indices.last - 1, elementsBetween) &&
                    cache != Pair(key,  list.indices.last - elementsBetween / 2)) {
                    return Pair(true, list.indices.last - elementsBetween / 2)
                }
            }
        }
    }

    return Pair(false, 0)
}

fun checkElementsBetweenFlipped(list: List<String>, left: Int, right: Int, numsBetween: Int): Boolean {
    for (i in 0 until numsBetween / 2) {
        if (list[left + i] != list[right - i]) {
            return false
        }
    }
    return true
}
