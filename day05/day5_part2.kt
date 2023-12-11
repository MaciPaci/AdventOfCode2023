import java.io.File
import java.io.InputStream
import kotlin.Long.Companion.MAX_VALUE

data class SeedWithRange(
    val start: Long,
    val end: Long
)

data class Range(
    val destinationRangeStart: Long,
    val sourceRangeStart: Long,
    val rangeLength: Long
) {
    fun hasBetween(index: Long): Boolean {
        return index >= sourceRangeStart && index < sourceRangeStart + rangeLength
    }
}

var smallestLocation = MAX_VALUE

val almanac = mutableMapOf<String, MutableList<Range>>(
    MapNames.SEED_TO_SOIL.mapName to mutableListOf(),
    MapNames.SOIL_TO_FERT.mapName to mutableListOf(),
    MapNames.FERT_TO_WATER.mapName to mutableListOf(),
    MapNames.WATER_TO_LIGHT.mapName to mutableListOf(),
    MapNames.LIGHT_TO_TEMP.mapName to mutableListOf(),
    MapNames.TEMP_TO_HUMID.mapName to mutableListOf(),
    MapNames.HUMID_TO_LOC.mapName to mutableListOf(),
)

fun main() {
    val filePath = "./day05/input.txt"

    val seedRanges = mutableListOf<Long>()
    val listOfSeedRanges = mutableListOf<SeedWithRange>()

    val file = File(filePath)
    val inputStream: InputStream = file.inputStream()
    val inputString = inputStream.bufferedReader().use { it.readText() }

    val preprocessedData = inputString.split(':', '\n').filter { it.isNotBlank() }
    var currentMap = ""
    preprocessedData.forEach {
        when (it) {
            "seeds" -> currentMap = MapNames.SEEDS.mapName
            "seed-to-soil map" -> currentMap = MapNames.SEED_TO_SOIL.mapName
            "soil-to-fertilizer map" -> currentMap = MapNames.SOIL_TO_FERT.mapName
            "fertilizer-to-water map" -> currentMap = MapNames.FERT_TO_WATER.mapName
            "water-to-light map" -> currentMap = MapNames.WATER_TO_LIGHT.mapName
            "light-to-temperature map" -> currentMap = MapNames.LIGHT_TO_TEMP.mapName
            "temperature-to-humidity map" -> currentMap = MapNames.TEMP_TO_HUMID.mapName
            "humidity-to-location map" -> currentMap = MapNames.HUMID_TO_LOC.mapName
            else -> {
                val listOfNumbers = it.split(' ').filter { record -> record.toLongOrNull() != null }.map { it.toLong() }
                if (currentMap == MapNames.SEEDS.mapName) {
                    seedRanges.addAll(listOfNumbers)
                } else {
                    almanac.getValue(currentMap).add(Range(listOfNumbers[0], listOfNumbers[1], listOfNumbers[2]))
                }
            }
        }
    }

    for (i in 0..<seedRanges.size step 2) {
        val start = seedRanges[i]
        val range = seedRanges[i + 1]
        listOfSeedRanges.add(SeedWithRange(start, start + range))
    }

    listOfSeedRanges.forEach { seedRange ->
        for (seed in seedRange.start..<seedRange.end) {
            calculateForSeed(seed, 1)
        }
    }
    println("Smallest location: $smallestLocation")
}

private fun calculateForSeed(
    seed: Long,
    index: Int,
) {
    var nextSeed = seed
    for (customRange in almanac.getValue(MapNames.indexToMapName(index))) {
        if (customRange.hasBetween(seed)) {
            nextSeed = seed + customRange.destinationRangeStart - customRange.sourceRangeStart
        }
    }
    if (index < 7) {
        calculateForSeed(nextSeed, index + 1)
    } else {
        if (nextSeed < smallestLocation) {
            smallestLocation = nextSeed
        }
    }
}


