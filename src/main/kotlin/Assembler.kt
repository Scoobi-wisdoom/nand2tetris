import java.io.File

fun main() {
    val fileNames = listOf(
        "add/Add$ASM_EXTENSION",
        "max/Max$ASM_EXTENSION",
        "pong/Pong$ASM_EXTENSION",
        "rect/Rect$ASM_EXTENSION",
    )
    val fileNameToLines = fileNames.associateWith { getLines(it) }

    val readAssembly = ReadAssembly()
    val fileNameToCommands = fileNameToLines.mapValues { (_, lines) ->
        readAssembly.parseCommands(lines)
    }


}

private fun getLines(fileName: String): List<String> {
    return File(fileName).useLines {
        it.toList()
    }
}

const val ASM_EXTENSION = ".asm"
const val HACK_EXTENSION = ".hack"
