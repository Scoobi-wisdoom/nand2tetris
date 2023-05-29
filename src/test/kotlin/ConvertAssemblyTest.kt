import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ConvertAssemblyTest {
    private val convertAssembly = ConvertAssembly()

    @Test
    fun `convert address`() {
        val sp = convertAssembly.convert("@SP")
        val zero = convertAssembly.convert("@0")
        val two = convertAssembly.convert("@2")
        val three = convertAssembly.convert("@3")

        assertAll(
            { assertTrue(sp == "0000000000000000") },
            { assertTrue(zero == "0000000000000000") },
            { assertTrue(two == "0000000000000010") },
            { assertTrue(three == "0000000000000011") },
        )
    }

    @Test
    fun `assign A to D`() {
        val cInstruction = convertAssembly.convert("D=A")

        println(cInstruction)

        assertTrue(cInstruction == "1110110000010000")
    }

    @Test
    fun `assign D+A to D`() {
        val cInstruction = convertAssembly.convert("D=D+A")

        assertTrue(cInstruction == "1110000010010000")
    }

    @Test
    fun `assign D to M`() {
        val cInstruction = convertAssembly.convert("M=D")

        assertTrue(cInstruction == "1110001100001000")
    }

    @Test
    fun `jump greater than D`() {
        val cInstruction = convertAssembly.convert("d;jgt")

        assertTrue(cInstruction == "1110001100000001")
    }

    @Test
    fun `assign M - 1 to MD`() {
        val cInstruction = convertAssembly.convert("md=m-1")

        assertTrue(cInstruction == "1111110010011000")
    }
}
