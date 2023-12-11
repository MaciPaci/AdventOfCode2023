import java.io.File
import java.io.InputStream

data class Race(
    var time: Int,
    var record: Int,
    var racesWon: Int = 0
)

fun main() {
    val filePath = "./day06/input.txt"
    val pattern = "(?<=\\:).*".toRegex()
    val file = File(filePath)
    val inputStream: InputStream = file.inputStream()
    val inputString = inputStream.bufferedReader().use { it.readText() }

    val match = pattern.findAll(inputString).map { it.value }.map { it.split(' ') }.map { it.filter { it.isNotBlank() }}.toList()
    val timeTable = match[0].map { it.toInt() }
    val distanceTable = match[1].map { it.toInt() }

    val raceTable = mutableListOf<Race>()
    for (i in timeTable.indices) {
        raceTable.add(Race(timeTable[i], distanceTable[i]))
    }

    var velocity: Int
    var distance: Int

    raceTable.forEach { race ->
        for (t in 1..<race.time) {
            velocity = t
            distance = velocity*(race.time-t)
            if (distance > race.record) {
                race.racesWon += 1
            }
        }
    }
    println("Races won: ${raceTable.fold(1){ acc: Int, race: Race -> acc * race.racesWon }}")
}
