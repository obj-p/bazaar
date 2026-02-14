package com.bazaar.parser.antlr

import org.antlr.v4.kotlinruntime.BaseErrorListener
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.RecognitionException
import org.antlr.v4.kotlinruntime.Recognizer
import kotlin.test.Test
import kotlin.test.assertEquals

class ParserTest {

    private fun parse(input: String): BazaarParser.BazaarFileContext {
        val lexer = BazaarLexer(CharStreams.fromString(input))
        val tokens = CommonTokenStream(lexer)
        val parser = BazaarParser(tokens)
        val errors = mutableListOf<String>()
        parser.removeErrorListeners()
        parser.addErrorListener(object : BaseErrorListener() {
            override fun syntaxError(
                recognizer: Recognizer<*, *>,
                offendingSymbol: Any?,
                line: Int,
                charPositionInLine: Int,
                msg: String,
                e: RecognitionException?,
            ) {
                errors.add("$line:$charPositionInLine $msg")
            }
        })
        val tree = parser.bazaarFile()
        assertEquals(emptyList(), errors, "Parse errors")
        return tree
    }

    @Test
    fun emptyFile() {
        parse("")
    }

    @Test
    fun packageOnly() {
        val tree = parse("package foo.bar")
        assertEquals(2, tree.packageDecl()!!.qualifiedName().IDENTIFIER().size)
    }

    @Test
    fun importsWithAliases() {
        val tree = parse(
            """
            package imp
            import a.b
            import c as D
            """.trimIndent()
        )
        assertEquals(2, tree.importDecl().size)
    }

    @Test
    fun emptyEnum() {
        val tree = parse("enum Empty {}")
        val decl = tree.topLevelDecl().single().enumDecl()!!
        assertEquals("Empty", decl.IDENTIFIER().first().text)
    }

    @Test
    fun enumWithCases() {
        val tree = parse("enum Color { red, green, blue }")
        val decl = tree.topLevelDecl().single().enumDecl()!!
        // IDENTIFIER[0] is the enum name, rest are cases
        assertEquals(4, decl.IDENTIFIER().size) // Color + red + green + blue
    }

    @Test
    fun enumTrailingComma() {
        val tree = parse("enum Color { r, g, b, }")
        val decl = tree.topLevelDecl().single().enumDecl()!!
        assertEquals(4, decl.IDENTIFIER().size) // Color + r + g + b
    }

    @Test
    fun emptyComponent() {
        val tree = parse("component Empty {}")
        val decl = tree.topLevelDecl().single().componentDecl()!!
        assertEquals(0, decl.memberDecl().size)
    }

    @Test
    fun componentWithFields() {
        val tree = parse(
            """
            component Foo {
                name string
                age int = 0
            }
            """.trimIndent()
        )
        val decl = tree.topLevelDecl().single().componentDecl()!!
        assertEquals(2, decl.memberDecl().size)
    }

    @Test
    fun emptyData() {
        val tree = parse("data Empty {}")
        val decl = tree.topLevelDecl().single().dataDecl()!!
        assertEquals(0, decl.memberDecl().size)
    }

    @Test
    fun modifierWithConstructor() {
        val tree = parse(
            """
            modifier Padding {
                top double
                bottom double
                constructor(all double) = Padding(all, all)
            }
            """.trimIndent()
        )
        val decl = tree.topLevelDecl().single().modifierDecl()!!
        assertEquals(3, decl.memberDecl().size)
        val ctor = decl.memberDecl().last().constructorDecl()!!
        assertEquals(1, ctor.parameterList()!!.parameterDecl().size)
    }

    @Test
    fun funcForwardDecl() {
        val tree = parse("func Add(x int, y int) -> int")
        val decl = tree.topLevelDecl().single().functionDecl()!!
        assertEquals(2, decl.parameterList()!!.parameterDecl().size)
        assertEquals(null, decl.block())
    }

    @Test
    fun funcWithBody() {
        val tree = parse("func Foo() -> int { }")
        val decl = tree.topLevelDecl().single().functionDecl()!!
        assertEquals("Foo", decl.IDENTIFIER()!!.text)
        assertEquals(0, decl.block()!!.stmt().size)
    }

    @Test
    fun emptyTemplate() {
        val tree = parse("template Empty {}")
        val decl = tree.topLevelDecl().single().templateDecl()!!
        assertEquals("Empty", decl.IDENTIFIER()!!.text)
        assertEquals(null, decl.parameterList())
    }

    @Test
    fun templateWithParams() {
        val tree = parse("template Foo(x int) { }")
        val decl = tree.topLevelDecl().single().templateDecl()!!
        assertEquals(1, decl.parameterList()!!.parameterDecl().size)
    }

    @Test
    fun preview() {
        val tree = parse("preview MyPreview { }")
        val decl = tree.topLevelDecl().single().previewDecl()!!
        assertEquals("MyPreview", decl.IDENTIFIER()!!.text)
    }

    @Test
    fun keywordAsFieldName() {
        val tree = parse("component Foo { data string }")
        val field = tree.topLevelDecl().single().componentDecl()!!.memberDecl().single().fieldDecl()!!
        assertEquals("data", field.identOrKeyword()!!.text)
    }

    @Test
    fun optionalAndArrayTypes() {
        val tree = parse(
            """
            component Foo {
                name string?
                items [int]
            }
            """.trimIndent()
        )
        val members = tree.topLevelDecl().single().componentDecl()!!.memberDecl()
        assertEquals(2, members.size)
    }

    @Test
    fun funcNoReturnType() {
        val tree = parse("func DoSomething()")
        val decl = tree.topLevelDecl().single().functionDecl()!!
        assertEquals("DoSomething", decl.IDENTIFIER()!!.text)
        assertEquals(null, decl.block())
    }

    @Test
    fun paramWithDefault() {
        val tree = parse("func Foo(x int = 0)")
        val param = tree.topLevelDecl().single().functionDecl()!!.parameterList()!!.parameterDecl().single()
        assertEquals("x", param.identOrKeyword()!!.text)
        assertEquals("0", param.expr()!!.text)
    }

    @Test
    fun blockWithContent() {
        val tree = parse("func Foo() { x + 1 }")
        val decl = tree.topLevelDecl().single().functionDecl()!!
        assertEquals(3, decl.block()!!.stmt().size)
    }

    @Test
    fun blockWithStringInterp() {
        val tree = parse(
            """
            template Foo { "${"\$"}{x}" }
            """.trimIndent()
        )
        val decl = tree.topLevelDecl().single().templateDecl()!!
        assertEquals(1, decl.block()!!.stmt().size)
    }

    @Test
    fun fullFile() {
        val tree = parse(
            """
            package myapp

            import ui.core
            import utils as U

            enum Direction { up, down, left, right }

            component Button {
                label string
                enabled bool = true
            }

            data Config {
                debug bool
            }

            modifier Padding {
                top double
                constructor(all double) = Padding(all)
            }

            func Add(x int, y int) -> int

            template Main(title string) {
            }

            preview Demo {
            }
            """.trimIndent()
        )
        assertEquals(7, tree.topLevelDecl().size)
    }
}
