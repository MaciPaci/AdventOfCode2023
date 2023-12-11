import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

data class NumberWithStars (
    val value: Int,
    val row: Int,
    val column: Int,
    val length: Int,
    var starsIndexes: MutableList<Int> = mutableListOf()
)

data class Star (
    val index: Int,
    val adjacentNumbers: MutableList<NumberWithStars> = mutableListOf()
)

fun main() {
    val filePath = "./day03/input.txt"
    val patternNumbers = "\\d+".toRegex()
    val patternStars = "[*]".toRegex()
    val numbersList = mutableListOf<NumberWithStars>()
    val starsMap = mutableMapOf<Int, Star>()
    val file = File(filePath)
    val lineLength = file.bufferedReader().use { it.readLine() }.length
    val lineCount = Files.lines(Paths.get(filePath)).count().toInt()
    val inputStream: InputStream = file.inputStream()
    val inputString = inputStream.bufferedReader().use { it.readText() }
    val inputStringNoNewline = inputString.replace("\n", "")
    var sumOfNumbers = 0

    patternNumbers.findAll(inputString).forEach { result ->
        val number = result.value.toInt()
        val range = result.range

        val newNumber = NumberWithStars(
            number,
            range.first / (lineLength + 1),
            range.first % (lineLength + 1),
            range.count(),
        )
        numbersList.add(newNumber)
    }

    patternStars.findAll(inputStringNoNewline).forEach { star ->
        starsMap[star.range.first] = Star(star.range.first)
    }

    for (number in numbersList) {
        number.starsIndexes = findStarsNearNumber(inputStringNoNewline, number, lineLength, lineCount)
        number.starsIndexes.forEach { starIndex ->
            starsMap[starIndex]?.adjacentNumbers?.add(number)
        }
    }
    starsMap.forEach { star ->
        if (star.value.adjacentNumbers.size == 2) {
            sumOfNumbers += star.value.adjacentNumbers[0].value * star.value.adjacentNumbers[1].value
        }
    }

    println("Sum of numbers with symbol adjacent: $sumOfNumbers")
}

fun findStarsNearNumber(inputString: String, number: NumberWithStars, lineLength: Int, lineCount: Int): MutableList<Int> {
    val starsIndexes = mutableListOf<Int>()
    for (i in number.row-1..number.row+1) {
        if (i < 0 || i >= lineCount) {
            continue
        }
        for (j in number.column - 1..number.column + number.length) {
            if (j < 0 || j >= lineLength) {
                continue
            }
            if (inputString[i * lineLength + j] == '*') {
                starsIndexes.add(i * lineLength + j)
            }
        }
    }
    return starsIndexes
}
