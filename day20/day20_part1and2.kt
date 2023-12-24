import java.io.File
import java.util.LinkedList
import java.util.Queue

interface Module {
    fun receiveSignal(source: String, signal: Int): Int
    fun sendSignal(): Int
    fun getState(): String
    val name: String
    val next: List<String>
}

class FlipFlop(override val name: String, override val next: List<String>) : Module {
    private var isOn = false

    override fun receiveSignal(source: String, signal: Int): Int {
        if (signal == 0) {
            isOn = !isOn
            return sendSignal()
        }
        return -1
    }

    override fun sendSignal(): Int {
        return if (isOn) {
            1
        } else {
            0
        }
    }

    override fun getState(): String {
        return "%s:%s:%s".format(name, "isOn", isOn)
    }
}

class Conjunction(override val name: String, override val next: List<String>) : Module {
    private var connectedInputs: MutableMap<String, Int> = mutableMapOf()

    override fun receiveSignal(source: String, signal: Int): Int {
        connectedInputs[source] = signal
        return sendSignal()
    }

    override fun sendSignal(): Int {
        return if (connectedInputs.all { it.value == 1 }) {
            0
        } else {
            1
        }
    }

    override fun getState(): String {
        return "%s:%s:%s".format(name, "connectedInputs", connectedInputs)
    }

    fun initializeConnectedInputs(inputs: List<String>) {
        connectedInputs = inputs.associateWith { 0 }.toMutableMap()
    }
}

class Broadcaster(override val name: String, override val next: List<String>) : Module {
    private var receivedSignal: Int = 0

    override fun receiveSignal(source: String, signal: Int): Int {
        receivedSignal = signal
        return sendSignal()
    }

    override fun sendSignal(): Int {
        return receivedSignal
    }

    override fun getState(): String {
        return "%s:%s:%s".format(name, "receivedSignal", receivedSignal)
    }
}

data class Signal(
    val source: String,
    val destination: String,
    val value: Int
)

fun main() {
    val filepath = "./day20/input.txt"

    val input = File(filepath).readLines()
    val conjunctions = getConjunctionWithInputs(input)
    val moduleMap = parseInputToModules(input, conjunctions)

    val (highSignals, lowSignals, cycles) = countSignals(moduleMap)

    val result: Long = highSignals * (1000 / cycles) * lowSignals * (1000 / cycles)
    println("High signals: $highSignals, low signals: $lowSignals, cycles $cycles")
    println("Result: $result")

    /**
     * Part 2
     */

    val resetModuleMap = parseInputToModules(input, conjunctions)
    val cycleIntervals = calculateCyclesIntervals(resetModuleMap)
    val buttonPresses = findLCMOfListOfNumbers(cycleIntervals)
    println("Button presses required: $buttonPresses")
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

private fun findLCMOfListOfNumbers(numbers: List<Long>): Long {
    var result = numbers[0]
    for (i in 1 until numbers.size) {
        result = findLCM(result, numbers[i])
    }
    return result
}

private fun calculateCyclesIntervals(moduleMap: MutableMap<String, Module>): List<Long> {
    val exitModule = "sq"
    val cyclesStartList = listOf("gd", "kg", "gt", "lf")
    val cycleIntervals = mutableListOf<Long>()
    for (start in cyclesStartList) {
        val signalQueue: Queue<Signal> = LinkedList()
        for (cycle in 1..4096) {
            val moduleListAsKey = mutableListOf<String>()
            moduleMap.forEach { module -> moduleListAsKey.add(module.value.getState()) }
            signalQueue.add(Signal("broadcaster", start, 0))
            while (signalQueue.isNotEmpty()) {
                val signal = signalQueue.remove()
                val destinationModule = moduleMap[signal.destination] ?: continue
                val nextModules = destinationModule.next
                val nextSignal = destinationModule.receiveSignal(signal.source, signal.value)
                if (nextSignal == -1) continue
                signalQueue.addAll(createNextSignals(destinationModule.name, nextModules, nextSignal))
                if (exitModule in nextModules && nextSignal == 1) {
                    cycleIntervals.add(cycle.toLong())
                }
            }
        }
    }
    return cycleIntervals
}

private fun getConjunctionWithInputs(input: List<String>): MutableMap<String, MutableList<String>> {
    val conjunctions = mutableMapOf<String, MutableList<String>>()
    input.forEach { line ->
        if (line.contains('&')) {
            conjunctions[(line.split("->").first().trim('&', ' '))] = mutableListOf()
        }
    }
    input.forEach { line ->
        val configuration = line.split("->")
        val name = configuration.first().trim('%', '&', ' ')
        conjunctions.forEach { conj ->
            if (conj.key in configuration.last()) {
                conjunctions[conj.key]!!.add(name)
            }
        }
    }
    return conjunctions
}

private fun countSignals(moduleMap: MutableMap<String, Module>): Triple<Long, Long, Long> {
    val seenStates = mutableMapOf<List<String>, Int>()
    val signalQueue: Queue<Signal> = LinkedList()
    var highSignals = 0L
    var lowSignals = 0L
    var cycles = 1000L
    for (i in 1..1000) {
        val moduleListAsKey = mutableListOf<String>()
        moduleMap.forEach { module -> moduleListAsKey.add(module.value.getState()) }
        if (moduleListAsKey in seenStates) {
            cycles = i.toLong() - 1L
            break
        }
        seenStates[moduleListAsKey] = i
        signalQueue.add(Signal("button", "broadcaster", 0))
        while (signalQueue.isNotEmpty()) {
            val signal = signalQueue.remove()
            when (signal.value) {
                1 -> highSignals += 1L
                0 -> lowSignals += 1L
                else -> continue
            }
            val destinationModule = moduleMap[signal.destination] ?: continue
            val nextSignal = destinationModule.receiveSignal(signal.source, signal.value)
            val nextModules = destinationModule.next
            signalQueue.addAll(createNextSignals(destinationModule.name, nextModules, nextSignal))
        }

    }
    return Triple(highSignals, lowSignals, cycles)
}

private fun parseInputToModules(
    input: List<String>,
    conjunctions: MutableMap<String, MutableList<String>>
): MutableMap<String, Module> {
    val moduleList = mutableMapOf<String, Module>()
    input.forEach { line ->
        val configuration = line.split("->")
        val module: Module
        val name: String
        when {
            configuration.first().contains("%") -> {
                name = configuration.first().trim('%', ' ')
                module = FlipFlop(
                    name,
                    configuration.last().trim().split(',').map { it.trim() }.toList()
                )
            }

            configuration.first().contains("&") -> {
                name = configuration.first().trim('&', ' ')
                module = Conjunction(
                    name,
                    configuration.last().split(',').map { it.trim() }.toList()
                )
                module.initializeConnectedInputs(conjunctions[name]!!)
            }

            else -> {
                name = configuration.first().trim()
                module = Broadcaster(
                    name,
                    configuration.last().split(',').map { it.trim() }.toList()
                )
            }
        }
        moduleList[name] = module
    }
    return moduleList
}

private fun createNextSignals(source: String, destination: List<String>, value: Int): List<Signal> {
    val nextSignals = mutableListOf<Signal>()
    for (module in destination) {
        nextSignals.add(Signal(source, module, value))
    }
    return nextSignals
}
