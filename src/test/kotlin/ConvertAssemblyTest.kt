import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ConvertAssemblyTest {
    private val convertAssembly = ConvertAssembly()

    @Test
    fun `convert address`() {
        val sp = convertAssembly.convert(
            "@SP",
            emptyMap(),
        )
        val zero = convertAssembly.convert(
            "@0",
            emptyMap(),
        )
        val two = convertAssembly.convert(
            "@2",
            emptyMap(),
        )
        val three = convertAssembly.convert(
            "@3",
            emptyMap(),
        )
        val thirtyTwo = convertAssembly.convert(
            "@32",
            emptyMap(),
        )

        assertAll(
            { assertTrue(sp == "0000000000000000") },
            { assertTrue(zero == "0000000000000000") },
            { assertTrue(two == "0000000000000010") },
            { assertTrue(three == "0000000000000011") },
            { assertTrue(thirtyTwo == "0000000000100000") },
        )
    }

    @Test
    fun `assign A to D`() {
        val cInstruction = convertAssembly.convert(
            "D=A",
            emptyMap()
        )

        assertTrue(cInstruction == "1110110000010000")
    }

    @Test
    fun `assign D+A to D`() {
        val cInstruction = convertAssembly.convert(
            "D=D+A",
            emptyMap(),
        )

        assertTrue(cInstruction == "1110000010010000")
    }

    @Test
    fun `assign D to M`() {
        val cInstruction = convertAssembly.convert(
            "M=D",
            emptyMap(),
        )

        assertTrue(cInstruction == "1110001100001000")
    }

    @Test
    fun `jump greater than D`() {
        val cInstruction = convertAssembly.convert(
            "D;JGT",
            emptyMap(),
        )

        assertTrue(cInstruction == "1110001100000001")
    }

    @Test
    fun `assign M - 1 to MD`() {
        val cInstruction = convertAssembly.convert(
            "MD=M-1",
            emptyMap(),
        )

        assertTrue(cInstruction == "1111110010011000")
    }
}
