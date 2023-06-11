import Constant.AT
import Constant.C_INSTRUCTION_PREFIX
import Constant.EQUAL
import Constant.SEMI_COLON
import Constant.computationToBinary
import Constant.destinationToBinary
import Constant.jumpToBinary
import Constant.predefinedSymbols

class ConvertAssembly {
    private val numericRegex = Regex("\\d+")

    fun convert(
        command: String,
        symbols: Map<String, Int>,
    ): String {
        return if (command.startsWith(AT)) {
            convertIntoAInstruction(
                command = command,
                symbols = symbols,
            )
        } else if (command.startsWith("(")) {
            ""
        } else {
            convertIntoCInstruction(command)
        }
    }

    private fun convertIntoAInstruction(
        command: String,
        symbols: Map<String, Int>,
    ): String {
        check(command.startsWith(AT))
        val stringAfterAnnotation = command.substringAfter(AT)

        val correspondingValue = predefinedSymbols[stringAfterAnnotation]
            ?: symbols[stringAfterAnnotation]
            ?: parseDigit(stringAfterAnnotation).toInt()

        return getSixteenDigitBinary(correspondingValue)
    }

    private fun parseDigit(addressOrVariable: String): String {
        return checkNotNull(numericRegex.find(addressOrVariable)?.value)
    }

    private fun getSixteenDigitBinary(decimalAddress: Int): String {
        return decimalAddress.toString(2).padStart(16, '0')
    }

    private fun convertIntoCInstruction(command: String): String {
        val computation = getComputation(command)
        val destination = getDestination(command)
        val jump = getJump(command)

        return "$C_INSTRUCTION_PREFIX$computation$destination$jump"
    }

    private fun getComputation(command: String): String {
        val hasEqual = command.contains(EQUAL)
        val hasSemiColon = command.contains(SEMI_COLON)
        check(hasEqual xor hasSemiColon)

        val computation = if (hasEqual) command.substringAfter(EQUAL) else
            command.substringBefore(SEMI_COLON)

        return checkNotNull(computationToBinary[computation])
    }

    private fun getDestination(command: String): String {
        val hasEqual = command.contains(EQUAL)
        val hasSemiColon = command.contains(SEMI_COLON)
        check(hasEqual xor hasSemiColon)

        val destination = if (hasEqual) command.substringBefore(EQUAL) else null

        return destinationToBinary[destination] ?: "000"
    }

    private fun getJump(command: String): String {
        val jump = command.substringAfter(SEMI_COLON)

        return jumpToBinary[jump] ?: "000"
    }
}
