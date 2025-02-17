import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class VMTranslator {
    private static final String VM_FILE_EXTENSION = ".vm";
    private static final String ASSEMBLY_FILE_EXTENSION = ".asm";

    public static void main(String[] args) {
        final String path = args[0];
        final File file = new File(path);
        if (!file.exists()) throw new RuntimeException("File does not exist: " + path);

        final CodeWriter codeWriter;
        if (file.isDirectory()) {
            File outputFile = new File(file.getAbsolutePath() + "/" + file.getName() + ASSEMBLY_FILE_EXTENSION);
            codeWriter = new CodeWriter(outputFile);

            // "... and that the first VM function that should start executing is the OS function Sys.init."
            codeWriter.init();
            Arrays.stream(file.listFiles())
                    .filter(f -> f.getName().endsWith(VM_FILE_EXTENSION))
                    .forEach(f -> {
                                // "Informs that the translation of a new VM file has started ~"
                                codeWriter.setFileName(f.getName());
                                writeAssemblyCode(f, codeWriter);
                            }
                    );
        } else if (file.isFile()) {
            final String fileName = file.getName();
            if (!fileName.endsWith(VM_FILE_EXTENSION)) throw new RuntimeException("Invalid file name: " + fileName);
            File outputFile = new File(file.getParent() + "/" + fileName.split(VM_FILE_EXTENSION)[0] + ASSEMBLY_FILE_EXTENSION);
            codeWriter = new CodeWriter(outputFile);
            codeWriter.setFileName(fileName);

            writeAssemblyCode(file, codeWriter);
        } else {
            throw new RuntimeException("Invalid file name: " + file.getName());
        }

        codeWriter.close();
    }

    private static void writeAssemblyCode(File vmFile, CodeWriter codeWriter) {
        final Parser parser;
        try {
            parser = new Parser(new FileInputStream(vmFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        while (parser.hasMoreLines()) {
            parser.advance();
            switch (parser.commandType()) {
                case Parser.C_POP: {
                }
                case Parser.C_PUSH: {
                    codeWriter.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
                    break;
                }
                case Parser.C_ARITHMETIC: {
                    codeWriter.writeArithmetic(parser.arg1());
                    break;
                }
                case Parser.C_LABEL: {
                    codeWriter.writeLabel(parser.arg1());
                    break;
                }
                case Parser.C_GOTO: {
                    codeWriter.writeGoTo(parser.arg1());
                    break;
                }
                case Parser.C_IF: {
                    codeWriter.writeIf(parser.arg1());
                    break;
                }
                case Parser.C_FUNCTION: {
                    codeWriter.writeFunction(parser.arg1(), parser.arg2());
                    break;
                }
                case Parser.C_RETURN: {
                    codeWriter.writeReturn();
                    break;
                }
                case Parser.C_CALL: {
                    codeWriter.writeCall(parser.arg1(), parser.arg2());
                    break;
                }
            }
        }
    }
}
