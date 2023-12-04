import java.io.File
import kotlin.math.pow

data class Scratchcard (
    var id: Int,
    var winningNumbers: List<Int>,
    var cardNumbers: List<Int>,
    var points: Int = 0
) {
    fun calculatePoints(): Int {
        val cardWon = cardNumbers.filter { cardNumber -> winningNumbers.any { it == cardNumber } }
        points = if (cardWon.isEmpty()) {
            0
        } else {
            (2.0).pow(cardWon.size-1).toInt()
        }
        return points
    }
}

fun main() {
    val filePath = "./day4/input.txt"
    val scratchcardList = mutableListOf<Scratchcard>()
    var sumOfPoints = 0

    File(filePath).forEachLine { line ->
        val gameData = line.split(':', '|')
        val gameID = gameData[0].filter { it.isDigit() }.toInt()
        val winningNumbers = gameData[1].trim().replace("  ", " ").split(' ').map { it.toInt() }.toList()
        val ownedNumbers = gameData[2].trim().replace("  ", " ").split(' ').map { it.toInt() }.toList()

        scratchcardList.add(Scratchcard(gameID, winningNumbers, ownedNumbers))
    }
    scratchcardList.forEach { scratchcard -> sumOfPoints += scratchcard.calculatePoints() }
    println("Sum of points: $sumOfPoints")
}
