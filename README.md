This project is, as [a part of Nand2Tetris](https://github.com/Scoobi-wisdoom/nand2tetris), to convert Jack byte codes into assembly language.

For example, below is VM source code to be converted:
```
function SimpleFunction.test 2
push local 0
push local 1
add
not
push argument 0
add
push argument 1
sub
return
```
This VM code is converted into below by the compiler:
```
// Write function SimpleFunction.test
(SimpleFunction.test)
@0
D=A
@SP
A=M
M=D
@SP
M=M+1
@0
D=A
@SP
A=M
M=D
@SP
M=M+1
// push local 0
@LCL
A=M
D=M
@SP
A=M
M=D
@SP
M=M+1
// push local 1
@1
D=A
@LCL
A=D+M
D=M
@SP
A=M
M=D
@SP
M=M+1
// add
@SP
AM=M-1
D=M
A=A-1
M=D+M
// not
@SP
A=M-1
M=!M
// push argument 0
@ARG
A=M
D=M
@SP
A=M
M=D
@SP
M=M+1
// add
@SP
AM=M-1
D=M
A=A-1
M=D+M
// push argument 1
@1
D=A
@ARG
A=D+M
D=M
@SP
A=M
M=D
@SP
M=M+1
// sub
@SP
AM=M-1
D=M
A=A-1
M=M-D
// Write return
@LCL
D=M
@R14
M=D
@5
A=D-A
D=M
@R15
M=D
@SP
AM=M-1
D=M
@ARG
A=M
M=D
@ARG
D=M
@SP
M=D+1
@R14
D=M
@1
A=D-A
D=M
@THAT
M=D
@R14
D=M
@2
A=D-A
D=M
@THIS
M=D
@R14
D=M
@3
A=D-A
D=M
@ARG
M=D
@R14
D=M
@4
A=D-A
D=M
@LCL
M=D
@R15
A=M
0;JMP
```

Below is a paraphrased drawing from the textbook Figure 8.3. This figure explains how stack looks like right after a function is called. 
```text
+--------------------+
|    arg - callee    |
+--------------------+
|   RETURN address   |
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

# References
## Memory Initial Value
- https://www.quora.com/A-memory-byte-is-never-empty-but-its-initial-content-may-be-meaningless-to-your-program-What-does-this-mean
- https://softwareengineering.stackexchange.com/questions/53272/the-default-state-of-unused-memory
## C-Instruction Label Translation
- https://github.com/xctom/Nand2Tetris/blob/master/projects/07/src/CodeWriter.java
- https://github.com/thomas-stockx/nand2tetris/blob/master/projects/07/VMTranslator/src/com/stockxit/nand2tetris/CodeWriter.java
- https://github.com/xxyzz/nand2tetris/tree/master/08
- https://github.com/msohaibalam/nand2tetris/blob/master/projects/08/code_writer.py
