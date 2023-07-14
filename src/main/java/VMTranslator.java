import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class VMTranslator {
    public static void main(String[] args) {
        String fileName = args[0];
        if (!isVmFile(fileName)) return;

        List<String> parsedCommands = getParsedCommands(fileName);

        List<String> writtenCodes = getWrittenCodes(parsedCommands);

        File outputFile = getOutputFile(fileName);
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(
                    writtenCodes.stream()
                            .collect(
                                    Collectors.joining(System.lineSeparator())
                            )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isVmFile(String fileName) {
        return fileName.endsWith(VM_FILE_EXTENSION);
    }

    private static final String VM_FILE_EXTENSION = ".vm";

    private static List<String> getParsedCommands(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            Parser parser = new Parser();

            return reader.lines()
                    .map(parser::parse)
                    .toList();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    private static List<String> getWrittenCodes(List<String> commands) {
        CodeWriter codeWriter = new CodeWriter();
        return commands.stream()
                .flatMap(command -> codeWriter.writeCode(command).stream())
                .collect(Collectors.toList());

    }

    private static File getOutputFile(String fileName) {
        File inputFile = new File(fileName);
        String baseFileName = inputFile.getName();
        String outputFileName = baseFileName.substring(0, baseFileName.lastIndexOf('.')) + ASSEMBLY_FILE_EXTENSION;
        return new File(inputFile.getParent(), outputFileName);
    }

    private static final String ASSEMBLY_FILE_EXTENSION = ".asm";
}
