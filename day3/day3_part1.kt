import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

data class Number (
    val value: Int,
    val row: Int,
    val column: Int,
    val length: Int,
    var nextToSymbol: Boolean = false
)

fun main() {
    val filePath = "./day3/input.txt"
    val patternNumbers = "\\d+".toRegex()
    val numbersList = mutableListOf<Number>()
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

        val newNumber = Number(
            number,
            range.first / (lineLength+1),
            range.first % (lineLength+1),
            range.count(),
        )
        numbersList.add(newNumber)
    }

    for (number in numbersList) {
        if (isSymbolNearNumber(inputStringNoNewline, number, lineLength, lineCount)) {
            number.nextToSymbol = true
            sumOfNumbers += number.value
        }
    }
    println("Sum of numbers with symbol adjacent: $sumOfNumbers")
}

fun isSymbolNearNumber(inputString: String, number: Number, lineLength: Int, lineCount: Int): Boolean {
    val patternSymbols = "[^.\\d\\n]+".toRegex()
    var windowString = ""
    for (i in number.row-1..number.row+1) {
        if (i < 0 || i >= lineCount) {
            continue
        }
        for (j in number.column-1..number.column+number.length) {
            if (j < 0 || j >= lineLength) {
                continue
            }
            windowString += inputString[i*lineLength+j]
        }
    }
    return patternSymbols.containsMatchIn(windowString)
}
