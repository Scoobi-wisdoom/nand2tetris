import java.io.File

class ReadConvertIntegrationAssembly {
    fun getFileNameToConvertedCommands(fileNameToLines: Map<String, List<String>>): Map<String, List<String>> {
        val readAssembly = ReadAssembly()
        val fileNameToCommands = fileNameToLines.mapValues { (_, lines) ->
            readAssembly.parseCommands(lines)
        }

        val fileNameToSymbols = fileNameToCommands.mapValues { (_, lines) ->
            readAssembly.getSymbolToAddress(lines)
        }

        val convertAssembly = ConvertAssembly()

        return fileNameToCommands.mapValues { (fileName, commands) ->
            commands.map {
                convertAssembly.convert(
                    command = it,
                    symbols = fileNameToSymbols[fileName] ?: emptyMap(),
                )
            }.filter { it.isNotBlank() }
        }
    }
}