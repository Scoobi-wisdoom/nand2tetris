object Constant {
    const val AT = "@"
    const val C_INSTRUCTION_PREFIX = "111"
    const val SEMI_COLON = ";"
    const val EQUAL = "="
    private const val ZERO = "0"
    private const val ONE = "1"

    val predefinedSymbols = mapOf(
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

    val computationToBinary = mapOf(
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

    val destinationToBinary = mapOf(
        "m" to "001",
        "d" to "010",
        "a" to "100",

        "dm" to "011",
        "md" to "011",

        "am" to "101",
        "ma" to "101",

        "ad" to "110",
        "da" to "110",

        "adm" to "111",
        "amd" to "111",
        "dam" to "111",
        "dma" to "111",
        "mda" to "111",
        "mad" to "111",
    )

    val jumpToBinary = mapOf(
        "jgt" to "001",
        "jeq" to "010",
        "jge" to "011",
        "jlt" to "100",
        "jne" to "101",
        "jle" to "110",
        "jmp" to "111",
    )
}