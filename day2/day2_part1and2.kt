import java.io.File

class Game (
    var id: Int,
    var maxRed: Int,
    var maxGreen: Int,
    var maxBlue: Int
)

fun main() {
    val filePath = "./day2/input.txt"
    val maxRedCubes = 12
    val maxGreenCubes = 13
    val maxBlueCubes = 14
    val listOfGames = mutableListOf<Game>()
    var sumOfIDs = 0
    var powerOfGameSets = 0

    File(filePath).forEachLine{ line ->
        val games = line.split(":", ";")
        val gameID = games[0].filter { it.isDigit() }.toInt()
        val game = Game(gameID, 0, 0, 0)

        for (i in (1..<games.size)) {
            val pull = games[i].split(",")
            pull.forEach { cubes ->
                when {
                    cubes.contains("red") -> {
                        val redCubes = cubes.filter { it.isDigit() }.toInt()
                        if (redCubes > game.maxRed) {
                            game.maxRed = redCubes 
                        }
                    }
                    cubes.contains("green") -> {
                        val greenCubes = cubes.filter { it.isDigit() }.toInt()
                        if (greenCubes > game.maxGreen) {
                            game.maxGreen = greenCubes 
                        }
                    }
                    cubes.contains("blue") ->  {
                        val blueCubes = cubes.filter { it.isDigit() }.toInt()
                        if (blueCubes > game.maxBlue) {
                            game.maxBlue = blueCubes 
                        }
                    }
                }
            }
        }
        listOfGames.add(game)
    }
    listOfGames.forEach { game ->
        if (game.maxRed <= maxRedCubes && game.maxGreen <= maxGreenCubes && game.maxBlue <= maxBlueCubes) {
            sumOfIDs += game.id
        }
        powerOfGameSets += game.maxRed * game.maxGreen * game.maxBlue 
    }
    println("Sum of game IDs: $sumOfIDs")
    println("Power of game sets: $powerOfGameSets")
}
