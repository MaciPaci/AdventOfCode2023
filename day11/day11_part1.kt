import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.abs

fun main() {
    val filePath = "./day11/input.txt"
    val file = File(filePath)
    val lineLength = file.bufferedReader().use { it.readLine() }.length
    val lineCount = Files.lines(Paths.get(filePath)).count().toInt()

    val input = file.readText().trim().split('\n').map { it.toMutableList() }.toMutableList()

    val emptyRows = (0 until lineCount).associateWith { true }.toMutableMap()
    val emptyColumns = (0 until lineLength).associateWith { true }.toMutableMap()

    input.forEachIndexed { rowIndex, row ->
        row.forEachIndexed { columnIndex, point ->
            if (point == '#') {
                emptyRows[rowIndex] = false
                emptyColumns[columnIndex] = false
            }
        }
    }

    val emptyRowsList = mutableListOf<Int>()
    val emptyColumnsList = mutableListOf<Int>()
    emptyRowsList.addAll(emptyRows.filter { it.value }.keys)
    emptyColumnsList.addAll(emptyColumns.filter { it.value }.keys)

    emptyColumnsList.reversed().forEach { colIndex ->
        input.forEach { row ->
            row.add(colIndex, '.')
        }
    }

    emptyRowsList.reversed().forEach { rowIndex ->
        input.add(rowIndex, MutableList(lineLength + emptyColumnsList.size) { '.' })
    }

    val galaxies = mutableListOf<Pair<Int, Int>>()
    input.forEachIndexed { rowIndex, row ->
        row.forEachIndexed { colIndex, point ->
            if (point == '#') {
                galaxies.add(Pair(rowIndex, colIndex))
            }
        }
    }

    var sumOfDistance = 0
    galaxies.forEachIndexed { index, galaxy ->
        for (i in index+1 until galaxies.size) {
            val distance = abs(galaxies[i].first - galaxy.first) + abs(galaxies[i].second - galaxy.second)
            sumOfDistance += distance
        }
    }
    println("Sum of distances between all pairs of galaxies: $sumOfDistance")
}
