import java.io.File
import java.io.InputStream
import kotlin.Long.Companion.MAX_VALUE

enum class MapNames(val mapName: String, val index: Int) {
    SEEDS("seeds", 0),
    SEED_TO_SOIL("seed_to_soil", 1),
    SOIL_TO_FERT("soil_to_fert", 2),
    FERT_TO_WATER("fert_to_water", 3),
    WATER_TO_LIGHT("water_to_light", 4),
    LIGHT_TO_TEMP("light_to_temp", 5),
    TEMP_TO_HUMID("temp_to_humid", 6),
    HUMID_TO_LOC("humid_to_loc", 7);

    companion object{
        fun indexToMapName(index: Int): String {
            return entries.first { it.index == index }.mapName
        }
    }
}

data class Seed(
    var id: Long,
    var soil: Long = 0,
    var fertilizer: Long = 0,
    var water: Long = 0,
    var light: Long = 0,
    var temperature: Long = 0,
    var humidity: Long = 0,
    var location: Long = 0,
)

fun updateSeedProperty(
    listOfSeeds: List<Seed>,
    propertyGetter: (Seed) -> Long,
    nextPropertyGetter: (Seed) -> Long,
    propertySetter: (Seed, Long) -> Unit,
    source: Long,
    range: Long,
    destination: Long
) {
    listOfSeeds.forEach { seed ->
        val propertyValue = propertyGetter(seed)
        val nextPropertyValue = nextPropertyGetter(seed)
        if (nextPropertyValue == 0L || nextPropertyValue == propertyValue) {
            if (propertyValue >= source && propertyValue < source + range) {
                propertySetter(seed, propertyValue - source + destination)
            } else {
                propertySetter(seed, propertyValue)
            }
        }
    }
}

fun main() {
    val filePath = "./day05/input.txt"
    val almanac = mutableMapOf<String, MutableList<Long>>(
        MapNames.SEEDS.mapName to mutableListOf(),
        MapNames.SEED_TO_SOIL.mapName to mutableListOf(),
        MapNames.SOIL_TO_FERT.mapName to mutableListOf(),
        MapNames.FERT_TO_WATER.mapName to mutableListOf(),
        MapNames.WATER_TO_LIGHT.mapName to mutableListOf(),
        MapNames.LIGHT_TO_TEMP.mapName to mutableListOf(),
        MapNames.TEMP_TO_HUMID.mapName to mutableListOf(),
        MapNames.HUMID_TO_LOC.mapName to mutableListOf(),
    )
    val listOfSeeds = mutableListOf<Seed>()

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
                almanac.getValue(currentMap).addAll(listOfNumbers)
            }
        }
    }

    almanac.getValue(MapNames.SEEDS.mapName).forEach { id ->
        listOfSeeds.add(Seed(id))
    }

    almanac.forEach { (name, map) ->
        when (name) {
            MapNames.SEED_TO_SOIL.mapName, MapNames.SOIL_TO_FERT.mapName, MapNames.FERT_TO_WATER.mapName, MapNames.WATER_TO_LIGHT.mapName, MapNames.LIGHT_TO_TEMP.mapName, MapNames.TEMP_TO_HUMID.mapName, MapNames.HUMID_TO_LOC.mapName -> {
                for (i in 0 until map.size step 3) {
                    val destination = map[i]
                    val source = map[i + 1]
                    val range = map[i + 2]

                    when (name) {
                        MapNames.SEED_TO_SOIL.mapName -> updateSeedProperty(listOfSeeds, Seed::id, Seed::soil, { seed, value -> seed.soil = value }, source, range, destination)
                        MapNames.SOIL_TO_FERT.mapName -> updateSeedProperty(listOfSeeds, Seed::soil, Seed::fertilizer, { seed, value -> seed.fertilizer = value }, source, range, destination)
                        MapNames.FERT_TO_WATER.mapName -> updateSeedProperty(listOfSeeds, Seed::fertilizer, Seed::water, { seed, value -> seed.water = value }, source, range, destination)
                        MapNames.WATER_TO_LIGHT.mapName -> updateSeedProperty(listOfSeeds, Seed::water, Seed::light, { seed, value -> seed.light = value }, source, range, destination)
                        MapNames.LIGHT_TO_TEMP.mapName -> updateSeedProperty(listOfSeeds, Seed::light, Seed::temperature, { seed, value -> seed.temperature = value }, source, range, destination)
                        MapNames.TEMP_TO_HUMID.mapName -> updateSeedProperty(listOfSeeds, Seed::temperature, Seed::humidity, { seed, value -> seed.humidity = value }, source, range, destination)
                        MapNames.HUMID_TO_LOC.mapName -> updateSeedProperty(listOfSeeds, Seed::humidity, Seed::location, { seed, value -> seed.location = value }, source, range, destination)
                    }
                }
            }
        }
    }

    var smallestLocation = MAX_VALUE
    listOfSeeds.forEach {seed ->
        if (seed.location < smallestLocation) {
            smallestLocation = seed.location
        }
    }
    println("Smallest location: $smallestLocation")
}
