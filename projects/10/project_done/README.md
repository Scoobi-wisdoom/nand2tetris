This project is, as [a part of Nand2Tetris](https://github.com/Scoobi-wisdoom/nand2tetris), to generate byte codes from
high level language Jack.   
This project provides over 50 test codes as well.

For example, this `let s = "string constant";` Jack language code is tokenized as below in xml format (**Syntax Analysis**).
```xml

<letStatement>
    <keyword> let </keyword>
    <identifier> s </identifier>
    <symbol> = </symbol>
    <expression>
        <term>
            <stringConstant> string constant </stringConstant>
        </term>
    </expression>
    <symbol> ; </symbol>
</letStatement>
```
This xml tokens are to be converted into byte codes.

# Implementation guideline

There are many functions to be implemented in `CompilationEngine`. Implementing `CompilationEngine` is tricky without
knowing which method is foundation. Here, foundation methods mean the smallest unit of methods called by other
methods.   
You are recommended to implement in this order:

- Three Foundations: 1. `compileTerm()`, 2. `compileExpression()`, 3. `compileExpressionList()`.
- `compileLet()`, `compileReturn()` and `compileDo()`.
- `compileStatements()`, `compileIf` and `compileWhile`.
- `compileVarDec()`, `compileSubroutineBody()` and `compileParameterList()`.
- `compileSubroutine()`.
- `compileClassVarDec()`.
- Finally, `compileClass()`.

In my humble opinion, TDD is bad for application development. However, for me to implement `CompilationEngine` TDD was a
hero.

# Jack Language: L(1)

According to the lecture, Jack language is L(1), which means that the way how the current token is processed depends on
the next token. In implementation, this is done by `class JackTokenizer`'s method `retreat()` checking the token and
then revert the cursor.   
Examples of L(1) where `retreat()` is used are below:

- `CompilationEngine`'s method `compileTerm()`
- `CompilationEngine`'s method `compileExpression()`
- `CompilationEngine`'s method `compileExpressionList()`
- `CompilationEngine`'s method `compileParameterList()`
- `CompilationEngine`'s method `compileStatements()`
- `CompilationEngine`'s method `compileIf()`

# References

- https://github.com/jahnagoldman/nand2tetris/blob/master/Project10/src/edu/uchicago/jagoldman/CompilationEngine.java
