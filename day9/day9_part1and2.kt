import java.io.File

fun main() {
    val filePath = "./day9/input.txt"
    var sumOfPredictionsPart1 = 0
    var sumOfPredictionsPart2 = 0

    val input = File(filePath).readText().trim().split('\n')
    input.forEach { sequence ->
        val nums = sequence.split(' ').map { it.toInt() }.toList()
        val tree = mutableListOf<List<Int>>()
        calculateDiffTree(nums, tree)

        val predictionsPart1 = mutableListOf(0)
        for (i in tree.size-1 downTo 1) {
            predictionsPart1.add(tree[i-1].last() + predictionsPart1.last())
        }

        val predictionsPart2 = mutableListOf(0)
        for (i in tree.size-1 downTo 1) {
            predictionsPart2.add(tree[i-1].first() - predictionsPart2.last())
        }
        sumOfPredictionsPart1 += predictionsPart1.last()
        sumOfPredictionsPart2 += predictionsPart2.last()

    }
    println("Sum of predictions for task 1: $sumOfPredictionsPart1")
    println("Sum of predictions for task 2: $sumOfPredictionsPart2")
}

fun calculateDiffTree(nums: List<Int>, tree: MutableList<List<Int>>): MutableList<List<Int>> {
    val diffs = mutableListOf<Int>()
    tree.add(nums)
    for (i in 0 until nums.size-1) {
        diffs.add(nums[i+1] - nums[i])
    }
    if (!diffs.all { it == 0 }) {
        calculateDiffTree(diffs, tree)
    }
    else {
        tree.add(diffs)
    }
    return tree
}
