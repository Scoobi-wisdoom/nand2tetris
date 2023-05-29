import java.io.File

fun main() {
    val fileNames = listOf(
        "add/Add$ASM_EXTENSION",
        "max/MaxL$ASM_EXTENSION",
        "pong/PongL$ASM_EXTENSION",
        "rect/RectL$ASM_EXTENSION",
    )
    val fileNameToLines = fileNames.associateWith { getLines(it) }

    val readAssembly = ReadAssembly()
    val fileNameToCommands = fileNameToLines.mapValues { (_, lines) ->
        readAssembly.parseCommands(lines)
    }

    val convertAssembly = ConvertAssembly()
    val fileNameToConvertedCommands: Map<String, List<String>> = fileNameToCommands.mapValues { (_, commands) ->
        commands.map { convertAssembly.convert(it) }
    }

    fileNameToConvertedCommands.entries.forEach {
        createFiles(
            exisingFilePath = it.key,
            newExtension = HACK_EXTENSION,
            content = it.value,
        )
    }
}

private fun getLines(fileName: String): List<String> {
    return File(fileName).useLines {
        it.toList()
    }
}

private fun createFiles(
    exisingFilePath: String,
    newExtension: String,
    content: List<String>,
) {
    val existingFileNameWithoutExtension = exisingFilePath.substringAfterLast("/").substringBefore(".")
    val filePath = "${exisingFilePath.substringBeforeLast("/")}/$existingFileNameWithoutExtension$newExtension"

    File(filePath).bufferedWriter().use { writer ->
        content.forEach { line ->
            writer.write(line)
            writer.newLine()
        }
    }
}

const val ASM_EXTENSION = ".asm"
const val HACK_EXTENSION = ".hack"
