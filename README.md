This project is, as [a part of Nand2Tetris](https://github.com/Scoobi-wisdoom/nand2tetris), to generate byte codes from
high level language Jack (project 11).   

In project 10, `code.generator.CompilationEngine` generates xml format data. By requirements, however, project 11 has nothing to do with xml data.

# Implementation guideline

There are many functions to be implemented in `code.generator.CompilationEngine`. Implementing `code.generator.CompilationEngine` is tricky without
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

In my humble opinion, TDD is bad for application development. However, for me to implement `code.generator.CompilationEngine` TDD was a
hero.

# Jack Language: L(1)

According to the lecture, Jack language is L(1), which means that the way how the current token is processed depends on
the next token. In implementation, this is done by `class syntax.analyzer.JackTokenizer`'s method `retreat()` checking the token and
then revert the cursor.   
Examples of L(1) where `retreat()` is used are below:

- `code.generator.CompilationEngine`'s method `compileTerm()`
- `code.generator.CompilationEngine`'s method `compileExpression()`
- `code.generator.CompilationEngine`'s method `compileExpressionList()`
- `code.generator.CompilationEngine`'s method `compileParameterList()`
- `code.generator.CompilationEngine`'s method `compileStatements()`
- `code.generator.CompilationEngine`'s method `compileIf()`

# References

- https://github.com/jahnagoldman/nand2tetris/blob/master/Project10/src/edu/uchicago/jagoldman/code.generator.CompilationEngine.java
