package com.bazaar.parser.antlr

import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.Token
import kotlin.test.Test
import kotlin.test.assertEquals

class StringLexerTest {

    private fun tokenTypes(input: String): List<Int> {
        val lexer = BazaarLexer(CharStreams.fromString(input))
        val stream = CommonTokenStream(lexer)
        stream.fill()
        return stream.tokens
            .filter { it.type != Token.EOF }
            .map { it.type }
    }

    private fun tokenTexts(input: String): List<String> {
        val lexer = BazaarLexer(CharStreams.fromString(input))
        val stream = CommonTokenStream(lexer)
        stream.fill()
        return stream.tokens
            .filter { it.type != Token.EOF }
            .map { it.text!! }
    }

    @Test
    fun emptyString() {
        val types = tokenTypes("\"\"")
        assertEquals(listOf(BazaarLexer.Tokens.STRING_OPEN, BazaarLexer.Tokens.STRING_CLOSE), types)
    }

    @Test
    fun simpleString() {
        val types = tokenTypes("\"hello\"")
        assertEquals(
            listOf(
                BazaarLexer.Tokens.STRING_OPEN,
                BazaarLexer.Tokens.STRING_TEXT,
                BazaarLexer.Tokens.STRING_CLOSE,
            ),
            types,
        )
        assertEquals(listOf("\"", "hello", "\""), tokenTexts("\"hello\""))
    }

    @Test
    fun escapeSequences() {
        val types = tokenTypes("\"line\\nbreak\"")
        assertEquals(
            listOf(
                BazaarLexer.Tokens.STRING_OPEN,
                BazaarLexer.Tokens.STRING_TEXT,
                BazaarLexer.Tokens.STRING_ESCAPE,
                BazaarLexer.Tokens.STRING_TEXT,
                BazaarLexer.Tokens.STRING_CLOSE,
            ),
            types,
        )
    }

    @Test
    fun unicodeShortEscape() {
        val types = tokenTypes("\"\\u0041\"")
        assertEquals(
            listOf(
                BazaarLexer.Tokens.STRING_OPEN,
                BazaarLexer.Tokens.UNICODE_SHORT_ESCAPE,
                BazaarLexer.Tokens.STRING_CLOSE,
            ),
            types,
        )
        assertEquals(listOf("\"", "\\u0041", "\""), tokenTexts("\"\\u0041\""))
    }

    @Test
    fun unicodeLongEscape() {
        val types = tokenTypes("\"\\U00000041\"")
        assertEquals(
            listOf(
                BazaarLexer.Tokens.STRING_OPEN,
                BazaarLexer.Tokens.UNICODE_LONG_ESCAPE,
                BazaarLexer.Tokens.STRING_CLOSE,
            ),
            types,
        )
        assertEquals(listOf("\"", "\\U00000041", "\""), tokenTexts("\"\\U00000041\""))
    }

    @Test
    fun multipleEscapes() {
        // Bazaar string: \\\" — backslash escape followed by escaped quote
        val types = tokenTypes("\"\\\\\\\"\"")
        assertEquals(
            listOf(
                BazaarLexer.Tokens.STRING_OPEN,
                BazaarLexer.Tokens.STRING_ESCAPE,
                BazaarLexer.Tokens.STRING_ESCAPE,
                BazaarLexer.Tokens.STRING_CLOSE,
            ),
            types,
        )
    }

    @Test
    fun dollarSign() {
        val types = tokenTypes("\"\$5.99\"")
        assertEquals(
            listOf(
                BazaarLexer.Tokens.STRING_OPEN,
                BazaarLexer.Tokens.STRING_DOLLAR,
                BazaarLexer.Tokens.STRING_TEXT,
                BazaarLexer.Tokens.STRING_CLOSE,
            ),
            types,
        )
    }

    @Test
    fun dollarOnly() {
        val types = tokenTypes("\"\$\"")
        assertEquals(
            listOf(
                BazaarLexer.Tokens.STRING_OPEN,
                BazaarLexer.Tokens.STRING_DOLLAR,
                BazaarLexer.Tokens.STRING_CLOSE,
            ),
            types,
        )
    }

    @Test
    fun simpleInterpolation() {
        val types = tokenTypes("\"\${x}\"")
        assertEquals(
            listOf(
                BazaarLexer.Tokens.STRING_OPEN,
                BazaarLexer.Tokens.STRING_INTERP_OPEN,
                BazaarLexer.Tokens.IDENTIFIER,
                BazaarLexer.Tokens.RBRACE,
                BazaarLexer.Tokens.STRING_CLOSE,
            ),
            types,
        )
    }

    @Test
    fun twoInterpolations() {
        val types = tokenTypes("\"\${a} and \${b}\"")
        assertEquals(
            listOf(
                BazaarLexer.Tokens.STRING_OPEN,
                BazaarLexer.Tokens.STRING_INTERP_OPEN,
                BazaarLexer.Tokens.IDENTIFIER,
                BazaarLexer.Tokens.RBRACE,
                BazaarLexer.Tokens.STRING_TEXT,
                BazaarLexer.Tokens.STRING_INTERP_OPEN,
                BazaarLexer.Tokens.IDENTIFIER,
                BazaarLexer.Tokens.RBRACE,
                BazaarLexer.Tokens.STRING_CLOSE,
            ),
            types,
        )
    }

    @Test
    fun nestedBracesInInterpolation() {
        // "${map { k -> v }}" — braces inside interpolation
        val types = tokenTypes("\"\${map { k -> v }}\"")
        assertEquals(
            listOf(
                BazaarLexer.Tokens.STRING_OPEN,
                BazaarLexer.Tokens.STRING_INTERP_OPEN,
                BazaarLexer.Tokens.IDENTIFIER,  // map
                BazaarLexer.Tokens.LBRACE,
                BazaarLexer.Tokens.IDENTIFIER,  // k
                BazaarLexer.Tokens.ARROW,        // ->
                BazaarLexer.Tokens.IDENTIFIER,  // v
                BazaarLexer.Tokens.RBRACE,       // closes inner {
                BazaarLexer.Tokens.RBRACE,       // closes ${
                BazaarLexer.Tokens.STRING_CLOSE,
            ),
            types,
        )
    }

    @Test
    fun nestedStringInInterpolation() {
        // "${foo("bar")}" — string inside interpolation
        val types = tokenTypes("\"\${foo(\"bar\")}\"")
        assertEquals(
            listOf(
                BazaarLexer.Tokens.STRING_OPEN,
                BazaarLexer.Tokens.STRING_INTERP_OPEN,
                BazaarLexer.Tokens.IDENTIFIER,       // foo
                BazaarLexer.Tokens.LPAREN,
                BazaarLexer.Tokens.STRING_OPEN,
                BazaarLexer.Tokens.STRING_TEXT,       // bar
                BazaarLexer.Tokens.STRING_CLOSE,
                BazaarLexer.Tokens.RPAREN,
                BazaarLexer.Tokens.RBRACE,
                BazaarLexer.Tokens.STRING_CLOSE,
            ),
            types,
        )
    }

    @Test
    fun emptyInterpolation() {
        val types = tokenTypes("\"\${}\"")
        assertEquals(
            listOf(
                BazaarLexer.Tokens.STRING_OPEN,
                BazaarLexer.Tokens.STRING_INTERP_OPEN,
                BazaarLexer.Tokens.RBRACE,
                BazaarLexer.Tokens.STRING_CLOSE,
            ),
            types,
        )
    }

    @Test
    fun adjacentInterpolations() {
        val types = tokenTypes("\"\${a}\${b}\"")
        assertEquals(
            listOf(
                BazaarLexer.Tokens.STRING_OPEN,
                BazaarLexer.Tokens.STRING_INTERP_OPEN,
                BazaarLexer.Tokens.IDENTIFIER,  // a
                BazaarLexer.Tokens.RBRACE,
                BazaarLexer.Tokens.STRING_INTERP_OPEN,
                BazaarLexer.Tokens.IDENTIFIER,  // b
                BazaarLexer.Tokens.RBRACE,
                BazaarLexer.Tokens.STRING_CLOSE,
            ),
            types,
        )
    }

    @Test
    fun deeplyNestedInterpolation() {
        // "${" ${ x } "}" — string in interpolation in string
        val types = tokenTypes("\"\${\"\${x}\"}\"")
        assertEquals(
            listOf(
                BazaarLexer.Tokens.STRING_OPEN,
                BazaarLexer.Tokens.STRING_INTERP_OPEN,
                BazaarLexer.Tokens.STRING_OPEN,
                BazaarLexer.Tokens.STRING_INTERP_OPEN,
                BazaarLexer.Tokens.IDENTIFIER,  // x
                BazaarLexer.Tokens.RBRACE,
                BazaarLexer.Tokens.STRING_CLOSE,
                BazaarLexer.Tokens.RBRACE,
                BazaarLexer.Tokens.STRING_CLOSE,
            ),
            types,
        )
    }

    @Test
    fun interpolationWithExpression() {
        val types = tokenTypes("\"\${a + b}\"")
        assertEquals(
            listOf(
                BazaarLexer.Tokens.STRING_OPEN,
                BazaarLexer.Tokens.STRING_INTERP_OPEN,
                BazaarLexer.Tokens.IDENTIFIER,  // a
                BazaarLexer.Tokens.PLUS,
                BazaarLexer.Tokens.IDENTIFIER,  // b
                BazaarLexer.Tokens.RBRACE,
                BazaarLexer.Tokens.STRING_CLOSE,
            ),
            types,
        )
    }

    @Test
    fun topLevelBraces() {
        val types = tokenTypes("{ }")
        assertEquals(
            listOf(
                BazaarLexer.Tokens.LBRACE,
                BazaarLexer.Tokens.RBRACE,
            ),
            types,
        )
    }

    @Test
    fun dollarAtEndOfString() {
        val types = tokenTypes("\"hello\$\"")
        assertEquals(
            listOf(
                BazaarLexer.Tokens.STRING_OPEN,
                BazaarLexer.Tokens.STRING_TEXT,
                BazaarLexer.Tokens.STRING_DOLLAR,
                BazaarLexer.Tokens.STRING_CLOSE,
            ),
            types,
        )
    }

    @Test
    fun badEscape() {
        val types = tokenTypes("\"\\a\"")
        assertEquals(
            listOf(
                BazaarLexer.Tokens.STRING_OPEN,
                BazaarLexer.Tokens.STRING_BAD_ESCAPE,
                BazaarLexer.Tokens.STRING_CLOSE,
            ),
            types,
        )
    }

    @Test
    fun newlineTerminatesString() {
        val types = tokenTypes("\"abc\ndef\"")
        assertEquals(
            listOf(
                BazaarLexer.Tokens.STRING_OPEN,
                BazaarLexer.Tokens.STRING_TEXT,   // abc
                BazaarLexer.Tokens.STRING_NL,
                BazaarLexer.Tokens.IDENTIFIER,    // def
                BazaarLexer.Tokens.STRING_OPEN,   // trailing " opens a new string
            ),
            types,
        )
    }
}
