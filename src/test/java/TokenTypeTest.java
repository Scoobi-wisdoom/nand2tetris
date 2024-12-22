import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class TokenTypeTest {
    @Test
    public void getKeywordType() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(TokenType.KEYWORD, TokenType.getTokenType("class")),
                () -> Assertions.assertEquals(TokenType.KEYWORD, TokenType.getTokenType("method")),
                () -> Assertions.assertEquals(TokenType.KEYWORD, TokenType.getTokenType("function")),
                () -> Assertions.assertEquals(TokenType.KEYWORD, TokenType.getTokenType("constructor")),
                () -> Assertions.assertEquals(TokenType.KEYWORD, TokenType.getTokenType("int")),
                () -> Assertions.assertEquals(TokenType.KEYWORD, TokenType.getTokenType("boolean")),
                () -> Assertions.assertEquals(TokenType.KEYWORD, TokenType.getTokenType("char")),
                () -> Assertions.assertEquals(TokenType.KEYWORD, TokenType.getTokenType("void")),
                () -> Assertions.assertEquals(TokenType.KEYWORD, TokenType.getTokenType("var")),
                () -> Assertions.assertEquals(TokenType.KEYWORD, TokenType.getTokenType("static")),
                () -> Assertions.assertEquals(TokenType.KEYWORD, TokenType.getTokenType("field")),
                () -> Assertions.assertEquals(TokenType.KEYWORD, TokenType.getTokenType("let")),
                () -> Assertions.assertEquals(TokenType.KEYWORD, TokenType.getTokenType("do")),
                () -> Assertions.assertEquals(TokenType.KEYWORD, TokenType.getTokenType("if")),
                () -> Assertions.assertEquals(TokenType.KEYWORD, TokenType.getTokenType("else")),
                () -> Assertions.assertEquals(TokenType.KEYWORD, TokenType.getTokenType("while")),
                () -> Assertions.assertEquals(TokenType.KEYWORD, TokenType.getTokenType("return")),
                () -> Assertions.assertEquals(TokenType.KEYWORD, TokenType.getTokenType("true")),
                () -> Assertions.assertEquals(TokenType.KEYWORD, TokenType.getTokenType("false")),
                () -> Assertions.assertEquals(TokenType.KEYWORD, TokenType.getTokenType("null")),
                () -> Assertions.assertEquals(TokenType.KEYWORD, TokenType.getTokenType("this"))
        );
    }

    @Test
    public void getSymbolType() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(TokenType.SYMBOL, TokenType.getTokenType("{")),
                () -> Assertions.assertEquals(TokenType.SYMBOL, TokenType.getTokenType("}")),
                () -> Assertions.assertEquals(TokenType.SYMBOL, TokenType.getTokenType("(")),
                () -> Assertions.assertEquals(TokenType.SYMBOL, TokenType.getTokenType(")")),
                () -> Assertions.assertEquals(TokenType.SYMBOL, TokenType.getTokenType("[")),
                () -> Assertions.assertEquals(TokenType.SYMBOL, TokenType.getTokenType("]")),
                () -> Assertions.assertEquals(TokenType.SYMBOL, TokenType.getTokenType(".")),
                () -> Assertions.assertEquals(TokenType.SYMBOL, TokenType.getTokenType(",")),
                () -> Assertions.assertEquals(TokenType.SYMBOL, TokenType.getTokenType(";")),
                () -> Assertions.assertEquals(TokenType.SYMBOL, TokenType.getTokenType("+")),
                () -> Assertions.assertEquals(TokenType.SYMBOL, TokenType.getTokenType("-")),
                () -> Assertions.assertEquals(TokenType.SYMBOL, TokenType.getTokenType("*")),
                () -> Assertions.assertEquals(TokenType.SYMBOL, TokenType.getTokenType("/")),
                () -> Assertions.assertEquals(TokenType.SYMBOL, TokenType.getTokenType("&")),
                () -> Assertions.assertEquals(TokenType.SYMBOL, TokenType.getTokenType("<")),
                () -> Assertions.assertEquals(TokenType.SYMBOL, TokenType.getTokenType(">")),
                () -> Assertions.assertEquals(TokenType.SYMBOL, TokenType.getTokenType("=")),
                () -> Assertions.assertEquals(TokenType.SYMBOL, TokenType.getTokenType("~"))
        );
    }

    @Test
    public void getIntConstType() {
        Assertions.assertEquals(TokenType.INT_CONST, TokenType.getTokenType("1"));
    }

    @Test
    public void getStringConstType() {
        Assertions.assertEquals(TokenType.STRING_CONST, TokenType.getTokenType("\"\""));
    }

    @Test
    public void getIdentifierConstType() {
        Assertions.assertEquals(TokenType.IDENTIFIER, TokenType.getTokenType("variable"));
    }
}
