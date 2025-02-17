This project is, as [a part of Nand2Tetris](https://github.com/Scoobi-wisdoom/nand2tetris), to convert codes in assembly language into bits.     

For example, below is assembly source code to be converted:
```
@2
D=A
@3
D=D+A
@0
M=D
```
This assembly code is converted into bits:
```
0000000000000010
1110110000010000
0000000000000011
1110000010010000
0000000000000000
1110001100001000
```
