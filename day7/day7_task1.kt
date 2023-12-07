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
    var cards: String,
    var bid: Int,
    var cardsStrength: List<Int>,
    var rank: Int = 0
)

fun main() {
    val filePath = "./day7/input.txt"
    val file = File(filePath)
    val listOfHands = mutableListOf<Hand>()
    var totalWinnings = 0

    file.forEachLine { line ->
        val splitLine = line.split(' ')
        val cards = splitLine[0]
        val bid = splitLine[1].toInt()
        val cardsStrength = convertCardsToStrength(cards)
        listOfHands.add(Hand(cards, bid, cardsStrength))
    }

    val mapOfHandStrengths = mutableMapOf<Int, MutableList<Hand>>()
    for (handStrength in HandStrengths.entries) {
        mapOfHandStrengths[handStrength.strength] = mutableListOf()
    }


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

    val sortedHands = mapOfHandStrengths.mapValues { (_, hands) ->
        hands.sortedWith(
            compareBy(
                { it.cardsStrength[0] },
                { it.cardsStrength[1] },
                { it.cardsStrength[2] },
                { it.cardsStrength[3] },
                { it.cardsStrength[4] })
        )
    }

    var rank = 1
    sortedHands.mapValues { (_, hands) ->
        hands.forEach { hand ->
            hand.rank = rank
            rank++
            totalWinnings += hand.rank * hand.bid
        }
    }

    println("Total winnings: $totalWinnings")
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
