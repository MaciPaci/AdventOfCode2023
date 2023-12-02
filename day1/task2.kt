import java.io.File

fun main() {
    var sum = 0
    val filePath = "input.txt"
    val regexFirst = "(zero|one|two|three|four|five|six|seven|eight|nine|\\d)".toRegex()
    val regexLast = "(zero|one|two|three|four|five|six|seven|eight|nine|\\d)(?!(zero|ne|wo|hree|four|five|six|seven|ight|ine|\\d))".toRegex()
    val numberDict = mapOf<String, Int>("one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5, "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9)

    File(filePath).forEachLine { line ->
        var foundNumbers = mutableListOf<String>()

        val firstMatch = regexFirst.find(line)
        foundNumbers.add(firstMatch?.value.toString())

        val lastMatch = regexLast.findAll(line).map{ it.value.toString() }.toList()
        foundNumbers.add(lastMatch[lastMatch.size - 1])

        val matchedInts = replaceStringsWithInts(foundNumbers)
        sum += matchedInts[0]*10 + matchedInts[1]
    }
    println("Sum: $sum")
}

fun replaceStringsWithInts(inputList: List<String>): List<Int> {
    val numberDict = mapOf<String, Int>("one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5, "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9)
    val result = mutableListOf<Int>()

    for (element in inputList) {
        val intValue = numberDict[element]

        if (intValue != null) {
            result.add(intValue)
        } else {
            result.add(element.toInt())
        }
    }

    return result
}