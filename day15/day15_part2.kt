import java.io.File

fun main() {
    val filepath = "./day15/input.txt"

    val input = File(filepath).readText().trim().split(',')

    val boxes = (0 until 256).associateWith { mutableMapOf<String, Int>() }.toMap()
    input.forEach { command ->
        calculateLenses(command, boxes)
    }

    val focusingPower = calculateFocusingPower(boxes)
    println("Result of the initialization sequence: $focusingPower")
}

fun calculateLenses(command: String, boxes: Map<Int, MutableMap<String, Int>>) {
    if (command.contains('=')) {
        insertLens(command, boxes)
    } else {
        removeLens(command, boxes)
    }
}

fun calculateFocusingPower(boxes: Map<Int, MutableMap<String, Int>>): Int {
    var focusingPower = 0
    boxes.forEach { box ->
        var slot = 1
        box.value.forEach { lens ->
            focusingPower += (box.key + 1) * slot * lens.value
            slot++
        }
    }
    return focusingPower
}

fun insertLens(command: String, boxes: Map<Int, MutableMap<String, Int>>) {
    val label = command.split('=').first()
    val boxNumber = calculateNewHash(label)
    val focalLength = command.split('=').last().toInt()
    boxes[boxNumber]!![label] = focalLength
}

fun removeLens(command: String, boxes: Map<Int, MutableMap<String, Int>>) {
    val label = command.removeSuffix("-")
    val boxNumber = calculateNewHash(label)
    if (label in boxes[boxNumber]!!.keys) {
        boxes[boxNumber]!!.remove(label)
    }
}

fun calculateNewHash(s: String): Int {
    var result = 0
    s.forEach { char ->
        result += char.code
        result *= 17
        result %= 256
    }
    return result
}
