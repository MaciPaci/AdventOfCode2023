import java.io.File

class Game (
    var ID: Int,
    var maxRed: Int,
    var maxGreen: Int,
    var maxBlue: Int
)

fun main() {
    val filePath = "input.txt"
    val maxRedCubes = 12
    val maxGreenCubes = 13
    val maxBlueCubes = 14
    var listOfGames = mutableListOf<Game>()
    var sumOfIDs = 0
    var powerOfGameSets = 0

    File(filePath).forEachLine{ line ->
        val games = line.split(":", ";")
        val gameID = games[0].filter { it.isDigit() }.toInt()
        val game = Game(gameID, 0, 0, 0)

        for (i in (1..games.size-1)) {
            val pull = games[i].split(",")
            pull.forEach { cubes ->
                when {
                    cubes.contains("red") -> {
                        var redCubes = cubes.filter { it.isDigit() }.toInt() 
                        if (redCubes > game.maxRed) {
                            game.maxRed = redCubes 
                        }
                    }
                    cubes.contains("green") -> {
                        var greenCubes = cubes.filter { it.isDigit() }.toInt() 
                        if (greenCubes > game.maxGreen) {
                            game.maxGreen = greenCubes 
                        }
                    }
                    cubes.contains("blue") ->  {
                        var blueCubes = cubes.filter { it.isDigit() }.toInt() 
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
            sumOfIDs += game.ID
        }
        powerOfGameSets += game.maxRed * game.maxGreen * game.maxBlue 
    }
    println("Sum of game IDs: $sumOfIDs")
    println("Power of game sets: $powerOfGameSets")
}