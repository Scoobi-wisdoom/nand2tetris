This project is, as [a part of Nand2Tetris](https://github.com/Scoobi-wisdoom/nand2tetris), to generate byte codes from
high level language Jack (project 11).   

In project 10, `code.generator.CompilationEngine` generates xml format data. By requirements, however, project 11 has nothing to do with xml data.

Project 11 targets to implement a Jack compiler.
For example, below is Jack language code before compilation:
```
class Main {
    function void main() {
        do Main.checkRange(3, 4);
        return;
    }

    function void checkRange(int a, int a_len) {
        return;
    }
}
```
This Jack code is converted into below by the compiler:
```
function Main.main 0
push constant 3
push constant 4
call Main.checkRange 2
pop temp 0
push constant 0
return
function Main.checkRange 0
push constant 0
return
```
In my humble opinion, TDD is bad for application development. However, for me to implement `code.generator.CompilationEngine` TDD was a hero. You can find more than 40 tests.
