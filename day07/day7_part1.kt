import java.io.File

enum class HandStrengths(val strength: Int) {
    HIGH_CARD(1),
    ONE_PAIR(2),
    TWO_PAIRS(3),
    THREE_OF_A_KIND(4),
    FULL_HOUSE(5),
    FOUR_OF_A_KIND(6),
    FIVE_OF_A_KIND(7),
}

enum class CardStrength(val symbol: Char, val strength: Int) {
    T('T', 10),
    J('J', 11),
    Q('Q', 12),
    K('K', 13),
    A('A', 14);

    companion object {
        fun getStrengthForCard(card: Char): Int {
            return entries.first { it.symbol == card }.strength
        }
    }
}

data class Hand(
    val cards: String,
    val bid: Int,
    val cardsStrength: List<Int>,
    var rank: Int = 0
)

fun main() {
    val filePath = "./day07/input.txt"

    val listOfHands = parseInputFile(filePath)

    val mapOfHandStrengths = HandStrengths.entries.associate { it.strength to mutableListOf<Hand>() }.toMutableMap()

    calculateHandStrength(listOfHands, mapOfHandStrengths)

    val sortedHands = sortHandsByCardStrength(mapOfHandStrengths)

    val totalWinnings = calculateTotalWinnings(sortedHands)

    println("Total winnings: $totalWinnings")
}

private fun calculateTotalWinnings(
    sortedHands: Map<Int, List<Hand>>,
): Int {
    var totalWinnings = 0
    var rank = 1
    sortedHands.mapValues { (_, hands) ->
        hands.forEach { hand ->
            hand.rank = rank
            rank++
            totalWinnings += hand.rank * hand.bid
        }
    }
    return totalWinnings
}

private fun sortHandsByCardStrength(mapOfHandStrengths: MutableMap<Int, MutableList<Hand>>) =
    mapOfHandStrengths.mapValues { (_, hands) ->
        hands.sortedWith(
            compareBy(
                { it.cardsStrength[0] },
                { it.cardsStrength[1] },
                { it.cardsStrength[2] },
                { it.cardsStrength[3] },
                { it.cardsStrength[4] })
        )
    }

private fun calculateHandStrength(
    listOfHands: List<Hand>,
    mapOfHandStrengths: MutableMap<Int, MutableList<Hand>>
) {
    listOfHands.forEach { hand ->
        val countedChars = hand.cards.groupingBy { it }.eachCount()
        when (countedChars.size) {
            5 -> mapOfHandStrengths.getValue(HandStrengths.HIGH_CARD.strength).add(hand)
            4 -> mapOfHandStrengths.getValue(HandStrengths.ONE_PAIR.strength).add(hand)
            3 -> {
                if (countedChars.entries.maxBy { it.value }.value == 3) {
                    mapOfHandStrengths.getValue(HandStrengths.THREE_OF_A_KIND.strength).add(hand)
                } else {
                    mapOfHandStrengths.getValue(HandStrengths.TWO_PAIRS.strength).add(hand)
                }
            }

            2 -> {
                if (countedChars.entries.maxBy { it.value }.value == 4) {
                    mapOfHandStrengths.getValue(HandStrengths.FOUR_OF_A_KIND.strength).add(hand)
                } else {
                    mapOfHandStrengths.getValue(HandStrengths.FULL_HOUSE.strength).add(hand)
                }
            }

            1 -> mapOfHandStrengths.getValue(HandStrengths.FIVE_OF_A_KIND.strength).add(hand)
        }
    }
}

private fun parseInputFile(filePath: String) = File(filePath)
    .readLines()
    .map { line ->
        val splitLine = line.split(' ')
        val cards = splitLine[0]
        val bid = splitLine[1].toInt()
        val cardsStrength = convertCardsToStrength(cards)
        Hand(cards, bid, cardsStrength)
    }

fun convertCardsToStrength(cards: String): List<Int> {
    val cardsStrength = cards.map { card ->
        if (card.isDigit()) {
            card.code - '0'.code
        } else {
            CardStrength.getStrengthForCard(card)
        }
    }
    return cardsStrength
}
