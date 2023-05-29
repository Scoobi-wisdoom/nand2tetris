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

    fun convert(command: String): String {
        val lowercaseCommand = command.lowercase()

        return if (command.startsWith(AT)) {
            // TODO("starting with @ does not mean it is either an address or variable since it could be a label.")
            convertIntoAInstruction(lowercaseCommand)
        } else {
            convertIntoCInstruction(lowercaseCommand)
        }
    }

    private fun convertIntoAInstruction(command: String): String {
        check(command.startsWith(AT))
        val addressOrVariable = command.substringAfter(AT)

        return if (isAddress(addressOrVariable)) {
            val decimalAddress = predefinedSymbols[addressOrVariable] ?: parseDigit(addressOrVariable).toInt()
            getSixteenDigitBinary(decimalAddress)
        } else {
            TODO("The total number of variables and the index of the given variable are required.")
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
}
