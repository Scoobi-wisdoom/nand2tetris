import java.io.*;

public class JackAnalyzer {
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
                    createXmlFile(jackFile);
                }
            } else if (inputFile.isFile() && inputFile.getName().endsWith(JACK)) {
                createXmlFile(inputFile);
            } else {
                throw new RuntimeException("Invalid file name: " + inputFile.getName());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createXmlFile(File jackFile) throws IOException {
        String xmlFilePath = jackFile.getAbsolutePath().replace(JACK, ".xml");

        try (
                FileInputStream inputStream = new FileInputStream(jackFile);
                FileOutputStream outputStream = new FileOutputStream(xmlFilePath)
        ) {
            CompilationEngine compileEngine = new CompilationEngine(inputStream, outputStream);
            compileEngine.compileClass();
            compileEngine.close();
        }
    }
}
