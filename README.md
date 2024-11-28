This project is, as [a part of Nand2Tetris](https://github.com/Scoobi-wisdoom/nand2tetris), to convert byte codes into assembly codes.

Below is a paraphrased drawing from the textbook Figure 8.3. This figure explains how stack looks like right after a function is called. 
```text
+--------------------+
|    arg - callee    |
+--------------------+
|   return address   |
+--------------------+
| LCL  pointer value |
+--------------------+
| ARG  pointer value |
+--------------------+
| THIS pointer value |
+--------------------+
| THAT pointer value |
+--------------------+
|    lcl - callee    |
+--------------------+
```
You could use this project as a jar file as following:    
- create a runnable jar file
```console
javac -d build -sourcepath src src/main/**/*.java
jar cfe VMTranslator.jar VMTranslator -C build .
```
- convert byte codes into assembly codes with the jar file
```console
java -jar VMTranslator.jar <fileName>.vm
```
# References
## Memory Initial Value
- https://www.quora.com/A-memory-byte-is-never-empty-but-its-initial-content-may-be-meaningless-to-your-program-What-does-this-mean
- https://softwareengineering.stackexchange.com/questions/53272/the-default-state-of-unused-memory
## C-Instruction Label Translation
- https://github.com/xctom/Nand2Tetris/blob/master/projects/07/src/CodeWriter.java
- https://github.com/thomas-stockx/nand2tetris/blob/master/projects/07/VMTranslator/src/com/stockxit/nand2tetris/CodeWriter.java
- https://github.com/xxyzz/nand2tetris/tree/master/08
