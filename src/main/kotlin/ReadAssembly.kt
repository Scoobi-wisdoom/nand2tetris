import Constant.AT
import Constant.SIXTEEN
import Constant.numericRegex
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
        val indexedLabelCommand = lines.withIndex()
            .find { (_, parsedCommand) ->
                parsedCommand.startsWith("(") && parsedCommand.endsWith(")")
            } ?: return emptyMap()

        val currentSymbolToLocation = mapOf(
            getLabel(indexedLabelCommand.value) to indexedLabelCommand.index
        )

        val linesWithoutCurrentLabel = lines.filterIndexed { index, _ ->
            index != indexedLabelCommand.index
        }

        return currentSymbolToLocation + getLabelToLocation(linesWithoutCurrentLabel)
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
                    parsedCommand.substringAfter(AT).let {
                        it != numericRegex.find(it)?.value
                    } &&
                    parsedCommand.substringAfter(AT) !in predefinedSymbols &&
                    parsedCommand.substringAfter(AT) !in labels
        }.distinct()


        return variables.mapIndexed { index, parsedCommand ->
            parsedCommand.substringAfter(AT) to SIXTEEN + index
        }.toMap()
    }
}
