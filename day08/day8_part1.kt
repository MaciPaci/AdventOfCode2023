import java.io.File

data class Node(
    val current: String,
    val left: String,
    val right: String
)

fun main() {
    val filePath = "./day08/input.txt"
    val start = "AAA"
    val end = "ZZZ"

    val input = File(filePath).readText().split("\n\n")
    val commands = input.first()
    val nodeMap = mutableMapOf<String, Node>()
    input[1].split('\n').filter { it.isNotBlank() }.forEach { node ->
        val splitNode =  node.split('=')
        val currentNode = splitNode.first().filter { it.isLetter() }
        val leftNode = splitNode.last().split(',').first().filter { it.isLetter() }
        val rightNode = splitNode.last().split(',').last().filter { it.isLetter() }
        nodeMap[currentNode] = Node(currentNode, leftNode, rightNode)
    }

    var currentNode = start
    var steps = 0
    while (currentNode != end) {
        val direction = commands[steps % commands.length]
        val nextNode = if (direction == 'L') {
            nodeMap.getValue(currentNode).left
        } else {
            nodeMap.getValue(currentNode).right
        }
        currentNode = nextNode
        steps++
    }

    println("Steps to reach the end: $steps")
}
