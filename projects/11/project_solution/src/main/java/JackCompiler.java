import code.generator.CompilationEngine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class JackCompiler {
    private static final String JACK = ".jack";

    public static void main(String[] args) {
        if (args.length != 1)
            return;

        String inputPath = args[0];
        File inputFile = new File(inputPath);

        if (!inputFile.exists())
            throw new RuntimeException("Error: Input file or directory does not exist.");

        try {
            if (inputFile.isDirectory()) {
                File[] jackFiles = inputFile.listFiles(((dir, name) -> name.endsWith(JACK)));
                if (jackFiles == null) throw new RuntimeException("No .jack file found");
                for (File jackFile : jackFiles) {
                    createVmFile(jackFile);
                }
            } else if (inputFile.isFile() && inputFile.getName().endsWith(JACK)) {
                createVmFile(inputFile);
            } else {
                throw new RuntimeException("Invalid file name: " + inputFile.getName());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createVmFile(File jackFile) throws IOException {
        String xmlFilePath = jackFile.getAbsolutePath().replace(JACK, ".vm");

        try (
                FileInputStream inputStream = new FileInputStream(jackFile);
                FileOutputStream outputStream = new FileOutputStream(xmlFilePath)
        ) {
            CompilationEngine compileEngine = new CompilationEngine(inputStream, outputStream);
            compileEngine.compileClass();
        }
    }
}
