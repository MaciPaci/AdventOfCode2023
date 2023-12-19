import java.io.File

data class MachinePart(
    var x: Int = 0,
    var m: Int = 0,
    var a: Int = 0,
    var s: Int = 0,
) {
    fun workflowPartToMachinePart(part: String): Int {
        when (part) {
            "x" -> return x
            "m" -> return m
            "a" -> return a
            "s" -> return s
        }
        return 0
    }

    fun getRatingSum(): Int {
        return x + m + a + s
    }
}

data class Workflow(
    var part: String = "",
    var rule: String = "",
    var value: Int = 0,
    var next: String = ""
)

fun main() {
    val filepath = "./day19/input.txt"

    val input = File(filepath).readText().trim().split("\n\n")
    val workflowsInput = input.first().split("\n")
    val partsInput = input.last().split("\n")

    val machineParts = parseMachineParts(partsInput)
    val workflows = parseWorkflows(workflowsInput)

    val startingWorkflow = "in"
    val acceptedParts = mutableListOf<MachinePart>()
    val rejectedParts = mutableListOf<MachinePart>()
    machineParts.forEach { part ->
        runPartsThroughWorkflows(part, workflows, startingWorkflow, acceptedParts, rejectedParts)
    }

    println("Sum of rating of accepted parts: ${acceptedParts.fold(0) { sum: Int, machinePart: MachinePart -> sum + machinePart.getRatingSum() }}")
}

private fun runPartsThroughWorkflows(
    machinePart: MachinePart,
    workflows: MutableMap<String, MutableList<Workflow>>,
    nextWorkflow: String,
    acceptedParts: MutableList<MachinePart>,
    rejectedParts: MutableList<MachinePart>,
) {
    val currentWorkflow = workflows[nextWorkflow]!!
    currentWorkflow.forEach { workflow ->
        when (workflow.rule) {
            "<" -> {
                val partValue = machinePart.workflowPartToMachinePart(workflow.part)
                if (partValue < workflow.value) {
                    if (workflow.next == "A") {
                        acceptedParts.add(machinePart)
                        return
                    } else if (workflow.next == "R") {
                        rejectedParts.add(machinePart)
                        return
                    }
                    return runPartsThroughWorkflows(machinePart, workflows, workflow.next, acceptedParts, rejectedParts)
                }
            }

            ">" -> {
                val partValue = machinePart.workflowPartToMachinePart(workflow.part)
                if (partValue > workflow.value) {
                    if (workflow.next == "A") {
                        acceptedParts.add(machinePart)
                        return
                    } else if (workflow.next == "R") {
                        rejectedParts.add(machinePart)
                        return
                    }
                    return runPartsThroughWorkflows(machinePart, workflows, workflow.next, acceptedParts, rejectedParts)
                }
            }

            "!" -> {
                if (workflow.next == "A") {
                    acceptedParts.add(machinePart)
                    return
                } else if (workflow.next == "R") {
                    rejectedParts.add(machinePart)
                    return
                }
                return runPartsThroughWorkflows(machinePart, workflows, workflow.next, acceptedParts, rejectedParts)
            }
        }
    }
}

private fun parseWorkflows(workflowsInput: List<String>): MutableMap<String, MutableList<Workflow>> {
    val workflows = mutableMapOf<String, MutableList<Workflow>>()
    workflowsInput.forEach { w ->
        val name = w.split('{').first()
        val rules = w.split('{').last().trim('}').split(',')
        workflows[name] = mutableListOf()
        rules.forEach { r ->
            val workflow = Workflow()
            val rule = r.split(':')
            if (rule.size == 1) {
                workflow.rule = "!"
                workflow.next = rule.first()
            } else {
                val sign: String = if (rule.first().contains('>')) {
                    ">"
                } else {
                    "<"
                }
                val partRule = rule.first().split(sign)
                workflow.part = partRule.first()
                workflow.rule = sign
                workflow.value = partRule.last().toInt()
                workflow.next = rule.last()
            }
            workflows[name]!!.add(workflow)
        }
    }
    return workflows
}

private fun parseMachineParts(parts: List<String>): MutableList<MachinePart> {
    val machineParts = mutableListOf<MachinePart>()
    parts.forEach { part ->
        val machinePart = MachinePart()
        val ratings = part.trim('{', '}').split(',')
        ratings.forEach { rating ->
            val code = rating.split('=').first()
            val value = rating.split('=').last().toInt()
            when (code) {
                "x" -> machinePart.x = value
                "m" -> machinePart.m = value
                "a" -> machinePart.a = value
                "s" -> machinePart.s = value
            }
        }
        machineParts.add(machinePart)
    }
    return machineParts
}

