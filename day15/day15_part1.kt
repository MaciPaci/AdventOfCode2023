import java.io.File

fun main() {
    val filepath = "./day15/input.txt"

    val input = File(filepath).readText().trim().split(',')

    var resultOfSequence = 0
    input.forEach { command ->
        resultOfSequence += calculateHash(command)
    }
    println("Result of the initialization sequence: $resultOfSequence")
}

fun calculateHash(command: String): Int {
    var result = 0
    command.forEach { char ->
        result += char.code
        result *= 17
        result %= 256
    }
    return result
}
