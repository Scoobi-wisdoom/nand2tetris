public class VMTranslator {
    public static void main(String[] args) {
        String fileName = args[0];
        if (!isVmFile(fileName)) return;
    }

    private static boolean isVmFile(String fileName) {
        return fileName.endsWith(VM_FILE_EXTENSION);
    }

    private static final String VM_FILE_EXTENSION = ".vm";
    private static final String ASSEMBLY_FILE_EXTENSION = ".asm";
}
