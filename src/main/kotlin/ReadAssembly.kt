import Constant.AT
import Constant.SIXTEEN
import Constant.predefinedSymbols

class ReadAssembly {
    fun parseCommands(lines: List<String>): List<String> {
        return lines
            .filterNot { it.startsWith("//") || it.isBlank() }
            .map { it.substringBefore("//").trim() }
    }

    fun getSymbolToAddress(lines: List<String>): Map<String, Int> {
        val labelToLocation = getLabelToLocation(lines)

        val variableToLocation = getVariableToLocation(
            lines = lines,
            labels = labelToLocation.keys,
        )

        return labelToLocation + variableToLocation
    }

    private fun getLabelToLocation(lines: List<String>): Map<String, Int> {
        return lines.mapIndexedNotNull { index, parsedCommand ->
            if (parsedCommand.startsWith("(") && parsedCommand.endsWith(")")) getLabel(parsedCommand) to index
            else null
        }.toMap()
    }

    private fun getLabel(parsedCommand: String): String {
        return parsedCommand.substringAfter("(")
            .substringBefore(")")
    }

    private fun getVariableToLocation(
        lines: List<String>,
        labels: Collection<String>
    ): Map<String, Int> {
        val variables = lines.filter { parsedCommand ->
            parsedCommand.startsWith(AT) &&
                    parsedCommand.substringAfter(AT) !in predefinedSymbols &&
                    parsedCommand.substringAfter(AT) !in labels
        }.distinct()


        return variables.mapIndexed { index, parsedCommand ->
            parsedCommand.substringAfter(AT) to SIXTEEN + index
        }.toMap()
    }
}
