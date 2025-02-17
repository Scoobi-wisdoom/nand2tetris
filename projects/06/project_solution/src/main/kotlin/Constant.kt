object Constant {
    const val AT = "@"
    const val C_INSTRUCTION_PREFIX = "111"
    const val SEMI_COLON = ";"
    const val EQUAL = "="
    const val SIXTEEN = 16
    private const val ZERO = "0"
    private const val ONE = "1"

    val numericRegex = Regex("\\d+")

    val predefinedSymbols = mapOf(
        "KBD" to 24576,
        "SCREEN" to 16384,
        "SP" to 0,
        "LCL" to 1,
        "ARG" to 2,
        "THIS" to 3,
        "THAT" to 4,
        "R0" to 0,
        "R1" to 1,
        "R2" to 2,
        "R3" to 3,
        "R4" to 4,
        "R5" to 5,
        "R6" to 6,
        "R7" to 7,
        "R8" to 8,
        "R9" to 9,
        "R10" to 10,
        "R11" to 11,
        "R12" to 12,
        "R13" to 13,
        "R14" to 14,
        "R15" to 15,
    )

    val computationToBinary = mapOf(
        /**
         * a == 0
         */
        "0" to "${ZERO}101010",
        "1" to "${ZERO}111111",
        "-1" to "${ZERO}111010",
        "D" to "${ZERO}001100",
        "A" to "${ZERO}110000",
        "!D" to "${ZERO}001101",
        "!A" to "${ZERO}110001",
        "-D" to "${ZERO}001111",
        "-A" to "${ZERO}110011",
        "D+1" to "${ZERO}011111",
        "A+1" to "${ZERO}110111",
        "D-1" to "${ZERO}001110",
        "A-1" to "${ZERO}110010",
        "D+A" to "${ZERO}000010",
        "D-A" to "${ZERO}010011",
        "A-D" to "${ZERO}000111",
        "D&A" to "${ZERO}000000",
        "D|A" to "${ZERO}010101",

        /**
         * a == 1
         */
        "M" to "${ONE}110000",
        "!M" to "${ONE}110001",
        "-M" to "${ONE}110011",
        "M+1" to "${ONE}110111",
        "M-1" to "${ONE}110010",
        "D+M" to "${ONE}000010",
        "D-M" to "${ONE}010011",
        "M-D" to "${ONE}000111",
        "D&M" to "${ONE}000000",
        "D|M" to "${ONE}010101",
    )

    val destinationToBinary = mapOf(
        "M" to "001",
        "D" to "010",
        "A" to "100",

        "DM" to "011",
        "MD" to "011",

        "AM" to "101",
        "MA" to "101",

        "AD" to "110",
        "DA" to "110",

        "ADM" to "111",
        "AMD" to "111",
        "DAM" to "111",
        "DMA" to "111",
        "MDA" to "111",
        "MAD" to "111",
    )

    val jumpToBinary = mapOf(
        "JGT" to "001",
        "JEQ" to "010",
        "JGE" to "011",
        "JLT" to "100",
        "JNE" to "101",
        "JLE" to "110",
        "JMP" to "111",
    )
}