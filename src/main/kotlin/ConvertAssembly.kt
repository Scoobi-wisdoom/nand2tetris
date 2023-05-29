import ConvertAssembly.Constant.AT
import ConvertAssembly.Constant.C_INSTRUCTION_PREFIX
import ConvertAssembly.Constant.EQUAL
import ConvertAssembly.Constant.ONE
import ConvertAssembly.Constant.SEMI_COLON
import ConvertAssembly.Constant.ZERO

class ConvertAssembly {
    private val numericRegex = Regex("\\d+")

    fun convert(command: String): String {
        val lowercaseCommand = command.lowercase()

        return if (isAddressOrVariable(lowercaseCommand)) {
            convertIntoAInstruction(lowercaseCommand)
        } else {
            convertIntoCInstruction(lowercaseCommand)
        }
    }

    private fun isAddressOrVariable(command: String): Boolean {
        return command.startsWith(AT)
    }

    private fun convertIntoAInstruction(command: String): String {
        check(isAddressOrVariable(command))
        val addressOrVariable = command.substringAfter(AT)

        return if (isAddress(addressOrVariable)) {
            val decimalAddress = predefinedSymbols[addressOrVariable] ?: parseDigit(addressOrVariable).toInt()
            getSixteenDigitBinary(decimalAddress)
        } else {
            TODO("line number of each announced variable is necessary.")
        }
    }

    private fun isAddress(addressOrVariable: String): Boolean {
        return addressOrVariable in predefinedSymbols.keys || isNumeric(addressOrVariable)
    }

    private fun isNumeric(addressOrVariable: String): Boolean {
        val parsedDigit = parseDigit(addressOrVariable)
        return parsedDigit.isNotBlank() && parsedDigit.length == addressOrVariable.length
    }

    private fun parseDigit(addressOrVariable: String): String {
        return numericRegex.find(addressOrVariable)?.value ?: ""
    }

    private fun getSixteenDigitBinary(decimalAddress: Int): String {
        return decimalAddress.toString(2).padStart(16, '0')
    }

    private fun convertIntoCInstruction(lowercaseCommand: String): String {
        val computation = getComputation(lowercaseCommand)
        val destination = getDestination(lowercaseCommand)
        val jump = getJump(lowercaseCommand)

        return "$C_INSTRUCTION_PREFIX$computation$destination$jump"
    }

    private fun getComputation(lowercaseCommand: String): String {
        val hasEqual = lowercaseCommand.contains(EQUAL)
        val hasSemiColon = lowercaseCommand.contains(SEMI_COLON)
        check(hasEqual xor hasSemiColon)

        val computation = if (hasEqual) lowercaseCommand.substringAfter(EQUAL) else
            lowercaseCommand.substringBefore(SEMI_COLON)

        return checkNotNull(computationToBinary[computation])
    }

    private fun getDestination(lowercaseCommand: String): String {
        val hasEqual = lowercaseCommand.contains(EQUAL)
        val hasSemiColon = lowercaseCommand.contains(SEMI_COLON)
        check(hasEqual xor hasSemiColon)

        val destination = if (hasEqual) lowercaseCommand.substringBefore(EQUAL) else null

        return destinationToBinary[destination] ?: "000"
    }

    private fun getJump(lowercaseCommand: String): String {
        val jump = lowercaseCommand.substringAfter(SEMI_COLON)

        return jumpToBinary[jump] ?: "000"
    }

    private val predefinedSymbols = mapOf(
        "kbd" to 24576,
        "screen" to 16384,
        "sp" to 0,
        "lcl" to 1,
        "arg" to 2,
        "this" to 3,
        "that" to 4,
        "r0" to 0,
        "r1" to 1,
        "r2" to 2,
        "r3" to 3,
        "r4" to 4,
        "r5" to 5,
        "r6" to 6,
        "r7" to 7,
        "r8" to 8,
        "r9" to 9,
        "r10" to 10,
        "r11" to 11,
        "r12" to 12,
        "r13" to 13,
        "r14" to 14,
        "r15" to 15,
    )

    private val computationToBinary = mapOf(
        /**
         * a == 0
         */
        "0" to "${ZERO}101010",
        "1" to "${ZERO}111111",
        "-1" to "${ZERO}111010",
        "d" to "${ZERO}001100",
        "a" to "${ZERO}110000",
        "!d" to "${ZERO}001101",
        "!a" to "${ZERO}110001",
        "-d" to "${ZERO}001111",
        "-a" to "${ZERO}110011",
        "d+1" to "${ZERO}011111",
        "a+1" to "${ZERO}110111",
        "d-1" to "${ZERO}001110",
        "a-1" to "${ZERO}110010",
        "d+a" to "${ZERO}000010",
        "d-a" to "${ZERO}010011",
        "a-d" to "${ZERO}000111",
        "d&a" to "${ZERO}000000",
        "d|a" to "${ZERO}010101",

        /**
         * a == 1
         */
        "m" to "${ONE}110000",
        "!m" to "${ONE}110001",
        "-m" to "${ONE}110011",
        "m+1" to "${ONE}110111",
        "m-1" to "${ONE}110010",
        "d+m" to "${ONE}000010",
        "d-m" to "${ONE}010011",
        "m-d" to "${ONE}000111",
        "d&m" to "${ONE}000000",
        "d|m" to "${ONE}010101",
    )

    private val destinationToBinary = mapOf(
        "m" to "001",
        "d" to "010",
        "dm" to "011",
        "a" to "100",
        "am" to "101",
        "ad" to "110",
        "adm" to "111",
    )

    private val jumpToBinary = mapOf(
        "jgt" to "001",
        "jeq" to "010",
        "jge" to "011",
        "jlt" to "100",
        "jne" to "101",
        "jle" to "110",
        "jmp" to "111",
    )

    private object Constant {
        const val AT = "@"
        const val ZERO = "0"
        const val ONE = "1"
        const val C_INSTRUCTION_PREFIX = "111"
        const val SEMI_COLON = ";"
        const val EQUAL = "="
    }
}
