import java.io.File

data class ScratchcardMulti (
    var id: Int,
    var winningNumbers: List<Int>,
    var cardNumbers: List<Int>,
    var cardsWon: Int = 0,
    var quantity: Int = 1
) {
    fun calculateWinnings(mapOfCards: MutableMap<Int, ScratchcardMulti>, index: Int) {
        cardsWon = cardNumbers.filter { cardNumber -> winningNumbers.any { it == cardNumber } }.size
        for (i in 1..cardsWon) {
            mapOfCards.getValue(index+i).quantity += quantity
        }
    }
}

fun main() {
    val filePath = "./day4/input.txt"
    val scratchcardMap = mutableMapOf<Int, ScratchcardMulti>()
    var sumOfCards = 0

    File(filePath).forEachLine { line ->
        val gameData = line.split(':', '|')
        val gameID = gameData[0].filter { it.isDigit() }.toInt()
        val winningNumbers = gameData[1].trim().replace("  ", " ").split(' ').map { it.toInt() }.toList()
        val ownedNumbers = gameData[2].trim().replace("  ", " ").split(' ').map { it.toInt() }.toList()

        scratchcardMap[gameID] = ScratchcardMulti(gameID, winningNumbers, ownedNumbers)
    }
    scratchcardMap.forEach { (index, scratchcard) ->
        scratchcard.calculateWinnings(scratchcardMap, index)
        sumOfCards += scratchcard.quantity
    }
    println("Sum of cards won: $sumOfCards")
}
