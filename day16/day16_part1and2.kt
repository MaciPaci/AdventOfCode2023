import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

enum class BeamDirection(val nexStep: Pair<Int, Int>) {
    NORTH(Pair(-1, 0)),
    WEST(Pair(0, -1)),
    EAST(Pair(0, 1)),
    SOUTH(Pair(1, 0));
}

data class BeamLocation(
    val x: Int,
    val y: Int
)

data class Beam(
    val location: BeamLocation,
    val direction: BeamDirection
)

const val filepathDay16 = "./day16/input.txt"
val lineLengthDay16 = File(filepathDay16).bufferedReader().use { it.readLine() }.length
val lineCountDay16 = Files.lines(Paths.get(filepathDay16)).count().toInt()

fun main() {
    val grid = File(filepathDay16).readLines()

    val beams = mutableListOf(Beam(BeamLocation(0, 0), BeamDirection.EAST))
    val energized = mutableSetOf<BeamLocation>()
    val visited = mutableListOf<Pair<BeamLocation, BeamDirection>>()

    calculateNextBeamStep(beams, grid, energized, visited)
    println("Tiles energized: ${energized.size}")

    /**
     * PART 2
     */

    val beamsControlled = buildStartingBeams()

    var highestPossibleEnergizedTiles = 0
    beamsControlled.forEach { startingBeam ->
        val energizedControlled = mutableSetOf<BeamLocation>()
        val visitedControlled = mutableListOf<Pair<BeamLocation, BeamDirection>>()
        calculateNextBeamStep(startingBeam, grid, energizedControlled, visitedControlled)
        if (energizedControlled.size > highestPossibleEnergizedTiles) highestPossibleEnergizedTiles = energizedControlled.size
    }
    println("Most energized tiles possible: $highestPossibleEnergizedTiles")
}

fun buildStartingBeams(): MutableList<MutableList<Beam>> {
    val beamsControlled: MutableList<MutableList<Beam>> = mutableListOf()

    for (row in 0 until lineCountDay16) {
        for (col in 0 until lineLengthDay16) {
            if (row == 0) {
                beamsControlled.add(mutableListOf(Beam(BeamLocation(row, col), BeamDirection.SOUTH)))
            }
            if (row == lineCountDay16 - 1) {
                beamsControlled.add(mutableListOf(Beam(BeamLocation(row, col), BeamDirection.NORTH)))
            }
            if (col == 0) {
                beamsControlled.add(mutableListOf(Beam(BeamLocation(row, col), BeamDirection.EAST)))
            }
            if (col == lineLengthDay16 - 1) {
                beamsControlled.add(mutableListOf(Beam(BeamLocation(row, col), BeamDirection.WEST)))
            }
        }
    }
    return beamsControlled
}


fun calculateNextBeamStep(
    beams: MutableList<Beam>,
    grid: List<String>,
    energized: MutableSet<BeamLocation>,
    visited: MutableList<Pair<BeamLocation, BeamDirection>>
) {
    var index = 0
    while (index < beams.size) {
        val beam = beams[index]
        val currentLocation = beam.location
        val currentDirection = beam.direction
        if (currentLocation.x >= lineLengthDay16
            || currentLocation.x < 0
            || currentLocation.y >= lineCountDay16
            || currentLocation.y < 0
            || Pair(currentLocation, currentDirection) in visited
        ) {
            index++
            continue
        }
        energized.add(BeamLocation(currentLocation.x, currentLocation.y))
        visited.add(Pair(currentLocation, currentDirection))


        when (grid[currentLocation.x][currentLocation.y]) {
            '.' -> {
                beams[index] = Beam(
                    BeamLocation(
                        currentLocation.x + currentDirection.nexStep.first,
                        currentLocation.y + currentDirection.nexStep.second
                    ), currentDirection
                )
                continue
            }

            '|' -> {
                val newDirection: BeamDirection
                if (currentDirection == BeamDirection.NORTH || currentDirection == BeamDirection.SOUTH) {
                    newDirection = currentDirection
                } else {
                    newDirection = BeamDirection.NORTH
                    beams.add(
                        Beam(
                            BeamLocation(
                                currentLocation.x + BeamDirection.SOUTH.nexStep.first,
                                currentLocation.y + BeamDirection.SOUTH.nexStep.second
                            ), BeamDirection.SOUTH
                        )
                    )
                }


                beams[index] = Beam(
                    BeamLocation(
                        currentLocation.x + newDirection.nexStep.first,
                        currentLocation.y + newDirection.nexStep.second
                    ), newDirection
                )
                continue
            }

            '-' -> {
                val newDirection: BeamDirection
                if (currentDirection == BeamDirection.WEST || currentDirection == BeamDirection.EAST) {
                    newDirection = currentDirection
                } else {
                    newDirection = BeamDirection.WEST
                    beams.add(
                        Beam(
                            BeamLocation(
                                currentLocation.x + BeamDirection.EAST.nexStep.first,
                                currentLocation.y + BeamDirection.EAST.nexStep.second
                            ), BeamDirection.EAST
                        )
                    )
                }

                beams[index] = Beam(
                    BeamLocation(
                        currentLocation.x + newDirection.nexStep.first,
                        currentLocation.y + newDirection.nexStep.second
                    ), newDirection
                )
                continue
            }

            '\\' -> {
                val newDirection: BeamDirection = when (currentDirection) {
                    BeamDirection.NORTH -> BeamDirection.WEST
                    BeamDirection.SOUTH -> BeamDirection.EAST
                    BeamDirection.WEST -> BeamDirection.NORTH
                    BeamDirection.EAST -> BeamDirection.SOUTH
                }
                beams[index] = Beam(
                    BeamLocation(
                        currentLocation.x + newDirection.nexStep.first,
                        currentLocation.y + newDirection.nexStep.second
                    ), newDirection
                )
                continue
            }

            '/' -> {
                val newDirection: BeamDirection = when (currentDirection) {
                    BeamDirection.NORTH -> BeamDirection.EAST
                    BeamDirection.SOUTH -> BeamDirection.WEST
                    BeamDirection.WEST -> BeamDirection.SOUTH
                    BeamDirection.EAST -> BeamDirection.NORTH
                }
                beams[index] = Beam(
                    BeamLocation(
                        currentLocation.x + newDirection.nexStep.first,
                        currentLocation.y + newDirection.nexStep.second
                    ), newDirection
                )
                continue
            }
        }
    }
}
