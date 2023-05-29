class ConvertAssembly {
    private val numericRegex = Regex("\\d+")

    fun convert(command: String): String {
        val lowercaseCommand = command.lowercase()

        return if (isAddressOrVariable(lowercaseCommand)) {
            convertAddressOrVariable(lowercaseCommand)
        } else {
            TODO()
        }
    }

    private fun isAddressOrVariable(command: String): Boolean {
        return command.startsWith("@")
    }

    private fun convertAddressOrVariable(command: String): String {
        check(isAddressOrVariable(command))
        val addressOrVariable = command.substringAfter("@")

        return if (isAddress(addressOrVariable)) {
            val decimalAddress = predefinedSymbols[addressOrVariable] ?: parseDigit(addressOrVariable).toInt()
            getSixteenDigitBinary(decimalAddress)
        } else {
            TODO()
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

    private val predefinedSymbols = mapOf(
        "kbd" to 24576,
        "screen" to 16384,
        "sp" to 0,
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
}
