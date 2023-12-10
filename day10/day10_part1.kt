import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

enum class Direction(private val connectingPipes: List<Char>, private val nexStep: Pair<Int, Int>) {
    NORTH(listOf('|', '7', 'F'), Pair(-1, 0)),
    WEST(listOf('-', 'L', 'F'), Pair(0, -1)),
    EAST(listOf('-', 'J', '7'), Pair(0, 1)),
    SOUTH(listOf('|', 'L', 'J'), Pair(1, 0));

    fun canConnectInDirection(pipe: Char): Boolean {
        return pipe in connectingPipes
    }

    fun getNextStepRow(): Int {
        return nexStep.first
    }

    fun getNextStepColumn(): Int {
        return nexStep.second
    }
}

enum class Pipe(val symbol: Char, private val nextStep: Map<Direction, Step>) {
    VERTICAL(
        '|',
        mapOf(
            Direction.NORTH to Step(Direction.NORTH),
            Direction.SOUTH to Step(Direction.SOUTH)
        )
    ),
    HORIZONTAL(
        '-',
        mapOf(
            Direction.WEST to Step(Direction.WEST),
            Direction.EAST to Step(Direction.EAST)
        )
    ),
    BEND_NE(
        'L',
        mapOf(
            Direction.WEST to Step(Direction.NORTH),
            Direction.SOUTH to Step(Direction.EAST)
        )
    ),
    BEND_NW(
        'J',
        mapOf(
            Direction.EAST to Step(Direction.NORTH),
            Direction.SOUTH to Step(Direction.WEST)
        )
    ),
    BEND_SE(
        '7',
        mapOf(
            Direction.NORTH to Step(Direction.WEST),
            Direction.EAST to Step(Direction.SOUTH)
        )
    ),
    BEND_SW(
        'F',
        mapOf(
            Direction.NORTH to Step(Direction.EAST),
            Direction.WEST to Step(Direction.SOUTH)
        )
    );

    companion object {
        fun getNextStep(pipe: Char, direction: Direction): Step {
            return getPipe(pipe).nextStep.getValue(direction)
        }

        private fun getPipe(symbol: Char): Pipe {
            return entries.first { it.symbol == symbol }
        }
    }
}

val pipe = Pipe

data class Step(
    val dir: Direction,
    val row: Int = dir.getNextStepRow(),
    val column: Int = dir.getNextStepColumn()
)

val steps = listOf(
    Step(Direction.NORTH),
    Step(Direction.WEST),
    Step(Direction.EAST),
    Step(Direction.SOUTH)
)

const val filePath = "./day10/input.txt"
val file = File(filePath)
val lineLength = file.bufferedReader().use { it.readLine() }.length
val lineCount = Files.lines(Paths.get(filePath)).count().toInt()

fun main() {
    val pattern = "S".toRegex()

    val input = file.readText().trim()
    val indexOfS = pattern.find(input.replace("\n", ""))!!.range.first
    val rowOfS = indexOfS / lineLength
    val columnOfS = indexOfS % lineLength
//    println("$rowOfS, $columnOfS")

    val grid = input.split('\n').map { it.toList() }


    val path = mutableListOf<Char>()
    traverse@ for (step in steps) {

//
//        calculatePath(rowOfS, columnOfS, step, grid, path)
////        println(path)
//        if (path.isNotEmpty()) {
//            break
//        }





        var nextRow = rowOfS + step.row
        var nextColumn = columnOfS +step.column
        var nextPipe = grid[rowOfS][columnOfS]
        var finished = false
        var nextStep = step
//
//        if (nextRow < 0
//            || nextRow >= lineCount
//            || nextColumn < 0
//            || nextColumn >= lineLength
//        ) {
//            continue
//        }
//
//        var nextPipe = grid[nextRow][nextColumn]
//        println(nextPipe)
        while (true) {

            if (nextRow < 0
                || nextRow >= lineCount
                || nextColumn < 0
                || nextColumn >= lineLength
            ) {
                break
            }

            nextPipe = grid[nextRow][nextColumn]
//            println(nextPipe)
            if (nextPipe == 'S') {
                break@traverse
            }
            if (canConnectPipes(nextPipe, nextStep.dir)) {
                path.add(nextPipe)
                nextStep = pipe.getNextStep(nextPipe, nextStep.dir)
                println("$nextPipe, $nextStep")
                nextRow += nextStep.row
                nextColumn += nextStep.column
//                if (nextRow < 0
//                    || nextRow >= lineCount
//                    || nextColumn < 0
//                    || nextColumn >= lineLength
//                ) {
//                    continue
//                }
//                nextPipe = grid[nextRow][nextColumn]
            } else {
                path.clear()
                break
            }
        }
//        println("$nextPipe, $step")





    }
    println(path)
    val furthestElementIndex = path.size / 2 + 1
    println("Steps needed: $furthestElementIndex")
}

fun canConnectPipes(nextPipe: Char, direction: Direction): Boolean {
    return direction.canConnectInDirection(nextPipe)
}


// Recursion is way cleaner but sadly overflows buffer hence this dirty loop approach
fun calculatePath(
    currentRow: Int,
    currentColumn: Int,
    step: Step,
    grid: List<List<Char>>,
    path: MutableList<Char>
): List<Char> {
    val nextRow = currentRow + step.row
    val nextColumn = currentColumn + step.column
    if (nextRow < 0
        || nextRow >= lineCount
        || nextColumn < 0
        || nextColumn >= lineLength
    ) {
        return listOf()
    }
    val nextPipe = grid[nextRow][nextColumn]
//    println(nextPipe)
    if (nextPipe != 'S') {
        if (canConnectPipes(nextPipe, step.dir)) {
            path.add(nextPipe)
            val newStep = pipe.getNextStep(nextPipe, step.dir)
//            println(newStep)
            calculatePath(nextRow, nextColumn, newStep, grid, path)
        } else {
            path.clear()
            return listOf()
        }
    }

    return path
}
