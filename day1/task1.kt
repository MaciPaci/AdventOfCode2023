import java.io.File

fun main() {
    var sum = 0
    val filePath = "input.txt"
    val regex = "\\d".toRegex()

    File(filePath).forEachLine { line ->
        val match = regex.findAll(line).map { it.value.toInt() }.toList()
        if (match.size == 1) {
            sum += match[0]*10 + match[0]
        } else {
            sum += match[0]*10 + match[match.size - 1]
        }
    }
    println("Sum: $sum")
}