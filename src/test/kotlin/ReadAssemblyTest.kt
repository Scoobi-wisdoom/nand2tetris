import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ReadAssemblyTest {
    private val readAssembly = ReadAssembly()

    @Test
    fun parseCommands() {
        val comment = "// This file is part of www.nand2tetris.org"
        val commandWithComment = "D=M              // D = first number"

        val result = readAssembly.parseCommands(
            listOf(
                comment,
                commandWithComment,
            )
        )
        assertTrue(result == listOf("D=M"))
    }
}
