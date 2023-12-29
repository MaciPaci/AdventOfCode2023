import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.util.LinkedList
import java.util.Queue

class GardenWalkTest {
    val filepath = "./day21/input_test.txt"

    private val grid = File(filepath).readLines().map { it.toList() }
    private val lineCount = grid.size
    private val lineLength = grid.first().size
    val start = findIndex2D(grid, 'S')!!

    @Test
    fun testWalkThroughGarden() {
        val startingPosition = Position(start, 0)
        val positionsQueue: Queue<Position> = LinkedList()
        positionsQueue.add(startingPosition)

        walkThroughGarden(6, positionsQueue, grid, lineCount, lineLength)
        assertEquals(16, positionsQueue.size, "In 6 steps, he should reach 16 garden plots.")
    }

    @Test
    fun testWalkThroughInfiniteGarden() {
        val positionsInfiniteQueue: MutableSet<Pair<Pair<Int, Int>, Int>> = mutableSetOf()
        positionsInfiniteQueue.add(Pair(start, 0))

        walkThroughInfiniteGarden(6, positionsInfiniteQueue, grid, lineCount, lineLength)
        assertEquals(16, positionsInfiniteQueue.size, "In 6 steps, he should reach 16 garden plots.")

        positionsInfiniteQueue.clear()
        positionsInfiniteQueue.add(Pair(start, 0))
        walkThroughInfiniteGarden(10, positionsInfiniteQueue, grid, lineCount, lineLength)
        assertEquals(50, positionsInfiniteQueue.size, "In 10 steps, he should reach 50 garden plots.")

        positionsInfiniteQueue.clear()
        positionsInfiniteQueue.add(Pair(start, 0))
        walkThroughInfiniteGarden(50, positionsInfiniteQueue, grid, lineCount, lineLength)
        assertEquals(1594, positionsInfiniteQueue.size, "In 50 steps, he should reach 1594 garden plots.")

        positionsInfiniteQueue.clear()
        positionsInfiniteQueue.add(Pair(start, 0))
        walkThroughInfiniteGarden(100, positionsInfiniteQueue, grid, lineCount, lineLength)
        assertEquals(6536, positionsInfiniteQueue.size, "In 100 steps, he should reach 6536 garden plots.")

        positionsInfiniteQueue.clear()
        positionsInfiniteQueue.add(Pair(start, 0))
        walkThroughInfiniteGarden(500, positionsInfiniteQueue, grid, lineCount, lineLength)
        assertEquals(167004, positionsInfiniteQueue.size, "In 500 steps, he should reach 167004 garden plots.")

        positionsInfiniteQueue.clear()
        positionsInfiniteQueue.add(Pair(start, 0))
        walkThroughInfiniteGarden(1000, positionsInfiniteQueue, grid, lineCount, lineLength)
        assertEquals(668697, positionsInfiniteQueue.size, "In 1000 steps, he should reach 668697 garden plots.")

        positionsInfiniteQueue.clear()
        positionsInfiniteQueue.add(Pair(start, 0))
        walkThroughInfiniteGarden(5000, positionsInfiniteQueue, grid, lineCount, lineLength)
        assertEquals(16733044, positionsInfiniteQueue.size, "In 5000 steps, he should reach 16733044 garden plots.")
    }
}
