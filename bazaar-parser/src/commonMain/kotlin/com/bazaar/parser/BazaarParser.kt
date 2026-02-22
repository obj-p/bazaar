package com.bazaar.parser

import com.bazaar.parser.antlr.BazaarLexer
import com.bazaar.parser.ast.BazaarFile
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import com.bazaar.parser.antlr.BazaarParser as AntlrParser

object BazaarParser {
    fun parse(source: String): BazaarFile {
        val result = parseWithDiagnostics(source)
        if (result.hasErrors) {
            throw ParseException(result.diagnostics)
        }
        return result.ast ?: error("Internal error: parse succeeded with no errors but produced no AST")
    }

    fun parseWithDiagnostics(source: String): ParseResult {
        val errorListener = BazaarErrorListener()

        val lexer = BazaarLexer(CharStreams.fromString(source))
        lexer.removeErrorListeners()
        lexer.addErrorListener(errorListener)

        val tokens = CommonTokenStream(lexer)
        val parser = AntlrParser(tokens)
        parser.removeErrorListeners()
        parser.addErrorListener(errorListener)

        val tree = parser.bazaarFile()

        val ast =
            try {
                BazaarAstVisitor().visitBazaarFile(tree)
            } catch (_: IllegalStateException) {
                // Visitor can throw on malformed trees produced by ANTLR error recovery
                null
            } catch (_: NullPointerException) {
                // ANTLR error recovery can produce parse tree nodes with null children
                null
            }

        val diagnostics = errorListener.diagnostics.toMutableList()
        if (ast == null && diagnostics.none { it.severity == Severity.ERROR }) {
            diagnostics.add(Diagnostic(Severity.ERROR, 1, 1, "failed to build AST"))
        }
        diagnostics.sortWith(
            compareBy({ it.line }, { it.column }, { it.message }),
        )

        return ParseResult(ast, diagnostics.toList())
    }
}
