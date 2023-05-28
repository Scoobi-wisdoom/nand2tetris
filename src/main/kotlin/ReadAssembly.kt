class ReadAssembly {
    fun parseCommands(lines: List<String>): List<String> {
        return lines
            .filterNot { it.startsWith("//") || it.isBlank() }
            .map { it.substringBefore("//").trim() }
    }
}
