import java.io.File

private data class NodeForGhosts(
    val current: String,
    val left: String,
    val right: String
)

fun main() {
    val filePath = "./day08/input.txt"

    val input = File(filePath).readText().split("\n\n")
    val commands = input.first()
    val startNodes = mutableListOf<String>()
    val endNodes = mutableListOf<String>()
    val nodeMap = mutableMapOf<String, NodeForGhosts>()

    input[1].split('\n').filter { it.isNotBlank() }.forEach { node ->
        parseInputParamsToNodes(node, startNodes, endNodes, nodeMap)
    }

    val pathLengths = mutableListOf<Int>()
    startNodes.forEach { startNode ->
        pathLengths.add(calculatePathLength(startNode, commands, nodeMap))
    }
    val lcm = findLCMOfListOfNumbers(pathLengths)

    println("Steps to reach the end: $lcm")
}

private fun parseInputParamsToNodes(
    node: String,
    startNodes: MutableList<String>,
    endNodes: MutableList<String>,
    nodeMap: MutableMap<String, NodeForGhosts>
) {
    val splitNode = node.split('=')
    val currentNode = splitNode.first().filter { it.isLetterOrDigit() }
    if (currentNode.endsWith('A')) {
        startNodes.add(currentNode)
    } else if (currentNode.endsWith('Z')) {
        endNodes.add(currentNode)
    }
    val leftNode = splitNode.last().split(',').first().filter { it.isLetterOrDigit() }
    val rightNode = splitNode.last().split(',').last().filter { it.isLetterOrDigit() }
    nodeMap[currentNode] = NodeForGhosts(currentNode, leftNode, rightNode)
}

private fun calculatePathLength(
    startNode: String,
    commands: String,
    nodeMap: MutableMap<String, NodeForGhosts>,
):Int {
    var steps = 0
    var nextNode = startNode
    while (true) {
        val direction = commands[steps % commands.length]
        val currentNode = nodeMap.getValue(nextNode)
        nextNode = if (direction == 'L') {
            currentNode.left
        } else {
            currentNode.right
        }
        steps++
        if (nextNode.endsWith('Z')) {
            return steps
        }
    }
}

private fun findLCM(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % a == 0L && lcm % b == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}

private fun findLCMOfListOfNumbers(numbers: List<Int>): Long {
    var result = numbers[0].toLong()
    for (i in 1 until numbers.size) {
        result = findLCM(result, numbers[i].toLong())
    }
    return result
}
