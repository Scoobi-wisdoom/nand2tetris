import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class VMTranslator {
    private static final String VM_FILE_EXTENSION = ".vm";
    private static final String ASSEMBLY_FILE_EXTENSION = ".asm";

    public static void main(String[] args) {
        final String fileName = args[0];
        if (!fileName.endsWith(VM_FILE_EXTENSION)) throw new RuntimeException("Invalid file name: " + fileName);
        final File file = new File(fileName);

        final Parser parser;
        try {
            parser = new Parser(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        File outputFile = new File(file.getParent(), file.getName().split(VM_FILE_EXTENSION)[0] + ASSEMBLY_FILE_EXTENSION);
        final CodeWriter codeWriter = new CodeWriter(outputFile);
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
            }
        }

        codeWriter.close();
    }
}
