import java.io.File
import java.io.InputStream
import kotlin.math.pow
import kotlin.math.sqrt

data class OneRace(
    var time: Long,
    var record: Long,
)

fun main() {
    val filePath = "./day06/input.txt"
    val file = File(filePath)
    val inputStream: InputStream = file.inputStream()
    val inputString = inputStream.bufferedReader().use { it.readText() }

    val raceData = inputString.trim().split('\n').map { it.filter { it.isDigit() }.toLong()}

    val race = OneRace(raceData[0], raceData[1])

    val delta = race.time.toDouble().pow(2.0) - 4*race.record
    val t1 = (-race.time - sqrt(delta))/-2
    val t2 = (-race.time + sqrt(delta))/-2

    val racesWon = t1.toInt()-t2.toInt()

    println("Races won: $racesWon")
}
