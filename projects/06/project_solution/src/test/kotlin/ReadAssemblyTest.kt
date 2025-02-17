import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

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

    @Test
    @DisplayName("A command of @Non-number should be considered as a symbol.")
    fun getSymbolToAddress() {
        val symbolToAddress = readAssembly.getSymbolToAddress(
            listOf(
                "@i",
                "M=0",
                "@R2",
                "M=0",
                "(LOOP)",
                "@i",
                "D=M",
                "@R1",
                "D=D-M",
                "@STOP",
                "D;JGE",
                "@R2",
                "D=M",
                "@R0",
                "D=D+M",
                "@R2",
                "M=D",
                "@i",
                "M=M+1",
                "@LOOP",
                "0;JMP",
                "(STOP)",
                "@i",
                "D=M",
                "@R1",
                "M=D",
                "(END)",
                "@END",
                "0;JMP",
            )
        )

        assertAll(
            { assertTrue(symbolToAddress["i"] == 16) },
            { assertTrue(symbolToAddress["LOOP"] == 4) },
            { assertTrue(symbolToAddress["STOP"] == 20) },
            { assertTrue(symbolToAddress["END"] == 24) },
        )
    }

    @Test
    @DisplayName("A Command of '@number' should not be regarded as a symbol.")
    fun getSymbolToAddressWithNumber() {
        val symbolToAddress = readAssembly.getSymbolToAddress(
            listOf(
                "@2",
                "D=A",
                "@3",
                "D=D+A",
                "@0",
                "M=D"
            )
        )

        assertTrue(symbolToAddress.isEmpty())
    }
}
