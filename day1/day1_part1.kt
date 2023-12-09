import java.io.File

fun main() {
    var sum = 0
    val filePath = "./day1/input.txt"
    val regex = "\\d".toRegex()

    File(filePath).forEachLine { line ->
        val match = regex.findAll(line).map { it.value.toInt() }.toList()
        sum += if (match.size == 1) {
            match[0]*10 + match[0]
        } else {
            match[0]*10 + match[match.size - 1]
        }
    }
    println("Sum: $sum")
}
