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
        val binaryArray = convertAssembly.convert("D=A")

        assertTrue(binaryArray == "1110110000010000")
    }

    @Test
    fun `assign D+A to D`() {
        val binaryArray = convertAssembly.convert("D=D+A")

        assertTrue(binaryArray == "1110000010010000")
    }

    @Test
    fun `assign D to M`() {
        val binaryArray = convertAssembly.convert("M=D")

        assertTrue(binaryArray == "1110001100001000")
    }
}
