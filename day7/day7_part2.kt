import java.io.File

enum class CardStrengthWithJokers(val symbol: Char, val strength: Int) {
    J('J', 0),
    T('T', 10),
    Q('Q', 12),
    K('K', 13),
    A('A', 14);

    companion object {
        fun getStrengthForCardWithJokers(card: Char): Int {
            return entries.first { it.symbol == card }.strength
        }
    }
}

fun main() {
    val filePath = "./day7/input.txt"

    val listOfHands = parseInputFileWithJokers(filePath)

    val mapOfHandStrengths = HandStrengths.entries.associate { it.strength to mutableListOf<Hand>() }.toMutableMap()

    calculateHandStrengthWithJokers(listOfHands, mapOfHandStrengths)

    val sortedHands = sortHandsByCardStrength(mapOfHandStrengths)

    val totalWinnings = calculateTotalWinnings(sortedHands)

    println("Total winnings: $totalWinnings")
}

private fun calculateTotalWinnings(
    sortedHands: Map<Int, List<Hand>>,
): Int {
    var rank = 1
    var totalWinnings = 0
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

private fun calculateHandStrengthWithJokers(
    listOfHands: List<Hand>,
    mapOfHandStrengths: MutableMap<Int, MutableList<Hand>>
) {
    listOfHands.forEach { hand ->
        val countedChars = hand.cards.groupingBy { it }.eachCount().toMutableMap()
        if (countedChars.containsKey(CardStrengthWithJokers.J.symbol)) {
            if (countedChars.size != 1) {
                val sortedEntries = countedChars.toList().sortedByDescending {
                    convertSingleCardsToStrengthWithJokers(it.first)
                }

                val maxEntry = sortedEntries.filter { it.first != CardStrengthWithJokers.J.symbol }.maxBy { it.second }
                countedChars[maxEntry.first] = maxEntry.second + countedChars.getValue(CardStrengthWithJokers.J.symbol)
            } else {
                countedChars[CardStrengthWithJokers.A.symbol] = 5
            }
            countedChars.remove(CardStrengthWithJokers.J.symbol)
        }

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

private fun parseInputFileWithJokers(filePath: String) = File(filePath)
    .readLines()
    .map { line ->
        val splitLine = line.split(' ')
        val cards = splitLine[0]
        val bid = splitLine[1].toInt()
        val cardsStrength = convertCardsToStrengthWithJokers(cards)
        Hand(cards, bid, cardsStrength)
    }

fun convertCardsToStrengthWithJokers(cards: String): List<Int> {
    val cardsStrength = cards.map { card ->
        convertSingleCardsToStrengthWithJokers(card)
    }
    return cardsStrength
}

fun convertSingleCardsToStrengthWithJokers(card: Char): Int {
    val cardStrength: Int = if (card.isDigit()) {
        card.code - '0'.code
    } else {
        CardStrengthWithJokers.getStrengthForCardWithJokers(card)
    }
    return cardStrength
}
