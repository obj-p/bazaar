package com.bazaar.parser.antlr

import org.antlr.v4.kotlinruntime.BaseErrorListener
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.RecognitionException
import org.antlr.v4.kotlinruntime.Recognizer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

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

    private fun parseFails(input: String) {
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
        parser.bazaarFile()
        assertTrue(errors.isNotEmpty(), "Expected parse errors but got none")
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
    fun mapType() {
        val tree = parse(
            """
            data Lookup {
                basic {string: int}
                optionalValue {string: int?}
                optionalMap {string: int}?
                nestedArray {string: [int]}
            }
            """.trimIndent()
        )
        val members = tree.topLevelDecl().single().dataDecl()!!.memberDecl()
        assertEquals(4, members.size)
    }

    @Test
    fun mapReturnTypeWithBody() {
        val tree = parse("func Build() -> {string: int} { }")
        val decl = tree.topLevelDecl().single().functionDecl()!!
        assertEquals("Build", decl.IDENTIFIER()!!.text)
        assertEquals(0, decl.block()!!.stmt().size)
    }

    @Test
    fun nestedMapType() {
        val tree = parse("data Nested { inner {string: {string: int}} }")
        val members = tree.topLevelDecl().single().dataDecl()!!.memberDecl()
        assertEquals(1, members.size)
    }

    @Test
    fun mapTypeInFunction() {
        val tree = parse("func Lookup(table {string: int}) -> {string: [int]}")
        val decl = tree.topLevelDecl().single().functionDecl()!!
        assertEquals(1, decl.parameterList()!!.parameterDecl().size)
    }

    @Test
    fun functionTypeFields() {
        val tree = parse(
            """
            data Callbacks {
                a func()
                b func(int, int) -> int
                c func(int,)
                d func()?
                e (func() -> string?)?
                f ((func()?))
            }
            """.trimIndent()
        )
        val members = tree.topLevelDecl().single().dataDecl()!!.memberDecl()
        assertEquals(6, members.size)
    }

    @Test
    fun higherOrderFunctionType() {
        val tree = parse("func HigherOrder() -> func() -> func()")
        val decl = tree.topLevelDecl().single().functionDecl()!!
        assertEquals("HigherOrder", decl.IDENTIFIER()!!.text)
    }

    @Test
    fun componentArrayType() {
        val tree = parse("component Row { children [component] }")
        val members = tree.topLevelDecl().single().componentDecl()!!.memberDecl()
        assertEquals(1, members.size)
    }

    @Test
    fun numberExpr() {
        val tree = parse("component Foo { x int = 42 }")
        val field = tree.topLevelDecl().single().componentDecl()!!.memberDecl().single().fieldDecl()!!
        assertIs<BazaarParser.NumberExprContext>(field.expr()!!)
    }

    @Test
    fun stringExpr() {
        val tree = parse("component Foo { x string = \"hello\" }")
        val field = tree.topLevelDecl().single().componentDecl()!!.memberDecl().single().fieldDecl()!!
        assertIs<BazaarParser.StringExprContext>(field.expr()!!)
    }

    @Test
    fun identExpr() {
        val tree = parse("component Foo { x int = y }")
        val field = tree.topLevelDecl().single().componentDecl()!!.memberDecl().single().fieldDecl()!!
        assertIs<BazaarParser.IdentExprContext>(field.expr()!!)
    }

    @Test
    fun parenExpr() {
        val tree = parse("component Foo { x int = (42) }")
        val field = tree.topLevelDecl().single().componentDecl()!!.memberDecl().single().fieldDecl()!!
        assertIs<BazaarParser.ParenExprContext>(field.expr()!!)
    }

    @Test
    fun nullLiteral() {
        val tree = parse("component Foo { x int? = null }")
        val field = tree.topLevelDecl().single().componentDecl()!!.memberDecl().single().fieldDecl()!!
        assertIs<BazaarParser.NullExprContext>(field.expr()!!)
    }

    @Test
    fun booleanLiterals() {
        val tree = parse(
            """
            component Foo {
                a bool = true
                b bool = false
            }
            """.trimIndent()
        )
        val members = tree.topLevelDecl().single().componentDecl()!!.memberDecl()
        assertIs<BazaarParser.TrueExprContext>(members[0].fieldDecl()!!.expr()!!)
        assertIs<BazaarParser.FalseExprContext>(members[1].fieldDecl()!!.expr()!!)
    }

    @Test
    fun emptyMapLiteral() {
        val tree = parse("component Foo { x {string: int} = {:} }")
        val field = tree.topLevelDecl().single().componentDecl()!!.memberDecl().single().fieldDecl()!!
        val expr = field.expr()!! as BazaarParser.MapExprContext
        val map = expr.mapLiteral()!!
        assertEquals(0, map.mapEntry().size)
    }

    @Test
    fun mapLiteralWithEntries() {
        val tree = parse(
            """
            data Foo {
                x {string: int} = {a: 1, b: 2}
                y {string: int} = {"key": 42}
                z {string: int} = {c: 3,}
            }
            """.trimIndent()
        )
        val members = tree.topLevelDecl().single().dataDecl()!!.memberDecl()
        val xMap = (members[0].fieldDecl()!!.expr()!! as BazaarParser.MapExprContext).mapLiteral()!!
        assertEquals(2, xMap.mapEntry().size)
        val yMap = (members[1].fieldDecl()!!.expr()!! as BazaarParser.MapExprContext).mapLiteral()!!
        assertEquals(1, yMap.mapEntry().size)
        val zMap = (members[2].fieldDecl()!!.expr()!! as BazaarParser.MapExprContext).mapLiteral()!!
        assertEquals(1, zMap.mapEntry().size) // trailing comma
    }

    @Test
    fun mapLiteralWithNullValue() {
        val tree = parse("component Foo { x {string: int?} = {\"key\": null} }")
        val field = tree.topLevelDecl().single().componentDecl()!!.memberDecl().single().fieldDecl()!!
        val map = (field.expr()!! as BazaarParser.MapExprContext).mapLiteral()!!
        assertEquals(1, map.mapEntry().size)
        assertIs<BazaarParser.NullExprContext>(map.mapEntry().single().expr(1)!!)
    }

    @Test
    fun mapInsideArray() {
        val tree = parse("component Foo { x [{string: int}] = [{:}] }")
        val field = tree.topLevelDecl().single().componentDecl()!!.memberDecl().single().fieldDecl()!!
        val arr = field.expr()!! as BazaarParser.ArrayExprContext
        val inner = arr.argList()!!.arg().single().expr()!! as BazaarParser.MapExprContext
        assertEquals(0, inner.mapLiteral()!!.mapEntry().size)
    }

    @Test
    fun optionalMemberAccess() {
        val tree = parse("component Foo { x int = a?.b }")
        val field = tree.topLevelDecl().single().componentDecl()!!.memberDecl().single().fieldDecl()!!
        assertIs<BazaarParser.OptionalMemberExprContext>(field.expr()!!)
    }

    @Test
    fun optionalIndexAccess() {
        val tree = parse("component Foo { x int = a?[0] }")
        val field = tree.topLevelDecl().single().componentDecl()!!.memberDecl().single().fieldDecl()!!
        assertIs<BazaarParser.OptionalIndexExprContext>(field.expr()!!)
    }

    @Test
    fun optionalCall() {
        val tree = parse("component Foo { x int = a?(b) }")
        val field = tree.topLevelDecl().single().componentDecl()!!.memberDecl().single().fieldDecl()!!
        assertIs<BazaarParser.OptionalCallExprContext>(field.expr()!!)
    }

    @Test
    fun chainedOptionalAccess() {
        val tree = parse("component Foo { x int = a?.b?.c }")
        val field = tree.topLevelDecl().single().componentDecl()!!.memberDecl().single().fieldDecl()!!
        val outer = field.expr()!! as BazaarParser.OptionalMemberExprContext
        assertIs<BazaarParser.OptionalMemberExprContext>(outer.expr()!!)
    }

    @Test
    fun mixedOptionalChain() {
        val tree = parse("component Foo { x int = a?.b?[0]?.c() }")
        val field = tree.topLevelDecl().single().componentDecl()!!.memberDecl().single().fieldDecl()!!
        // Outermost: ?.c() → callExpr wrapping optionalMemberExpr
        val call = field.expr()!! as BazaarParser.CallExprContext
        val optMember = call.expr()!! as BazaarParser.OptionalMemberExprContext
        val optIndex = optMember.expr()!! as BazaarParser.OptionalIndexExprContext
        assertIs<BazaarParser.OptionalMemberExprContext>(optIndex.expr(0)!!)
    }

    @Test
    fun chainedMemberAccess() {
        val tree = parse("component Foo { x int = a.b.c }")
        val field = tree.topLevelDecl().single().componentDecl()!!.memberDecl().single().fieldDecl()!!
        val outer = field.expr()!! as BazaarParser.MemberExprContext
        assertIs<BazaarParser.MemberExprContext>(outer.expr()!!)
    }

    @Test
    fun memberThenCall() {
        val tree = parse("component Foo { x int = a.b() }")
        val field = tree.topLevelDecl().single().componentDecl()!!.memberDecl().single().fieldDecl()!!
        val call = field.expr()!! as BazaarParser.CallExprContext
        assertIs<BazaarParser.MemberExprContext>(call.expr()!!)
    }

    @Test
    fun indexThenMember() {
        val tree = parse("component Foo { x int = a[0].b }")
        val field = tree.topLevelDecl().single().componentDecl()!!.memberDecl().single().fieldDecl()!!
        val member = field.expr()!! as BazaarParser.MemberExprContext
        assertIs<BazaarParser.IndexExprContext>(member.expr()!!)
    }

    @Test
    fun callWithNamedArgs() {
        val tree = parse(
            """
            modifier Padding {
                top double
                constructor(all double) = Padding(top = all)
            }
            """.trimIndent()
        )
        val ctor = tree.topLevelDecl().single().modifierDecl()!!.memberDecl().last().constructorDecl()!!
        val call = ctor.expr()!! as BazaarParser.CallExprContext
        val arg = call.argList()!!.arg().single()
        assertEquals("top", arg.IDENTIFIER()!!.text)
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

    @Test
    fun mapLiteralMissingColon() {
        parseFails("component Foo { x int = {a 1} }")
    }

    @Test
    fun mapLiteralLeadingComma() {
        parseFails("component Foo { x int = {, a: 1} }")
    }

    @Test
    fun trailingOptionalDot() {
        parseFails("component Foo { x int = a?. }")
    }
}
