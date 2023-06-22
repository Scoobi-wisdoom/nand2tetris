import java.io.File

fun main() {
    val fileNames = getTargetFileNames()
    val fileNameToLines = fileNames.associateWith { getLines(it) }

    val fileNameToConvertedCommands = ReadConvertIntegrationAssembly().getFileNameToConvertedCommands(fileNameToLines)

    createFiles(fileNameToConvertedCommands)
}

private fun getTargetFileNames(): List<String> {
    return listOf(
        "add/Add$ASM_EXTENSION",
        "max/MaxL$ASM_EXTENSION",
        "pong/PongL$ASM_EXTENSION",
        "rect/RectL$ASM_EXTENSION",
        "max/Max$ASM_EXTENSION",
        "pong/Pong$ASM_EXTENSION",
        "rect/Rect$ASM_EXTENSION",
    )
}

private fun getLines(fileName: String): List<String> {
    return File(fileName).useLines {
        it.toList()
    }
}

private fun createFiles(fileNameToConvertedCommands: Map<String, List<String>>) {
    fileNameToConvertedCommands.entries.forEach { (fileName, content) ->
        createFile(
            exisingFilePath = fileName,
            newExtension = HACK_EXTENSION,
            content = content,
        )
    }
}

private fun createFile(
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
