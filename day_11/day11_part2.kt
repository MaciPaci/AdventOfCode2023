import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.abs

fun main() {
    val filePath = "./day_11/input.txt"
    val file = File(filePath)
    val lineLength = file.bufferedReader().use { it.readLine() }.length
    val lineCount = Files.lines(Paths.get(filePath)).count().toInt()

    val input = file.readText().trim().split('\n').map { it.toMutableList() }.toMutableList()

    val emptyRows = (0 until lineCount).associateWith { true }.toMutableMap()
    val emptyColumns = (0 until lineLength).associateWith { true }.toMutableMap()

    val galaxies = mutableListOf<Pair<Int, Int>>()
    input.forEachIndexed { rowIndex, row ->
        row.forEachIndexed { columnIndex, point ->
            if (point == '#') {
                emptyRows[rowIndex] = false
                emptyColumns[columnIndex] = false
                galaxies.add(Pair(rowIndex, columnIndex))
            }
        }
    }

    val emptyRowsList = mutableListOf<Int>()
    val emptyColumnsList = mutableListOf<Int>()
    emptyRowsList.addAll(emptyRows.filter { it.value }.keys)
    emptyColumnsList.addAll(emptyColumns.filter { it.value }.keys)

    var sumOfDistance = 0L
    val expansionRate = 1000000
    galaxies.forEachIndexed { index, galaxy ->
        for (i in index + 1 until galaxies.size) {
            val distance = abs(galaxies[i].first - galaxy.first) + abs(galaxies[i].second - galaxy.second)
            val newRowsBetween =
                emptyRowsList.filter { it in galaxy.first..galaxies[i].first || it in galaxies[i].first..galaxy.first }.size
            val newColsBetween =
                emptyColumnsList.filter { it in galaxy.second..galaxies[i].second || it in galaxies[i].second..galaxy.second }.size
            sumOfDistance += distance + (newRowsBetween + newColsBetween) * (expansionRate - 1)
        }
    }

    println("Sum of distances between all pairs of galaxies: $sumOfDistance")
}
