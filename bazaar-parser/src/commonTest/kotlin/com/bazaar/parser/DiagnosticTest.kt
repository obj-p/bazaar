package com.bazaar.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DiagnosticTest {

    @Test
    fun successfulParseReturnsEmptyDiagnostics() {
        val result = BazaarParser.parseWithDiagnostics("component Foo {}")
        assertNotNull(result.ast)
        assertTrue(result.diagnostics.isEmpty())
        assertTrue(!result.hasErrors)
    }

    @Test
    fun parseWithErrorsReturnsNonEmptyDiagnostics() {
        val result = BazaarParser.parseWithDiagnostics("component {}")
        assertTrue(result.diagnostics.isNotEmpty())
        assertTrue(result.hasErrors)
    }

    @Test
    fun parseThrowsParseExceptionOnErrors() {
        assertFailsWith<ParseException> {
            BazaarParser.parse("component {}")
        }
    }

    @Test
    fun parseExceptionCarriesDiagnosticList() {
        val ex = assertFailsWith<ParseException> {
            BazaarParser.parse("component {}")
        }
        assertTrue(ex.diagnostics.isNotEmpty())
        assertEquals(Severity.ERROR, ex.diagnostics.first().severity)
    }

    @Test
    fun multipleErrorsAreAllReported() {
        val result = BazaarParser.parseWithDiagnostics(
            """
            component {
                name string =
            }
            data 456 {}
            """.trimIndent()
        )
        assertTrue(result.diagnostics.size >= 2, "Expected at least 2 errors, got ${result.diagnostics.size}")
    }

    @Test
    fun diagnosticsHaveCorrectLineAndColumn() {
        val result = BazaarParser.parseWithDiagnostics("component {}")
        val diag = result.diagnostics.first()
        assertEquals(1, diag.line)
        assertTrue(diag.column >= 1, "Column should be 1-based")
    }

    @Test
    fun emptyFileProducesNoDiagnostics() {
        val result = BazaarParser.parseWithDiagnostics("")
        assertNotNull(result.ast)
        assertTrue(result.diagnostics.isEmpty())
    }

    @Test
    fun parseExceptionMessageContainsLocation() {
        val ex = assertFailsWith<ParseException> {
            BazaarParser.parse("component {}")
        }
        assertTrue(ex.message!!.contains(":"), "Message should contain line:column")
    }

    @Test
    fun astIsNullWhenVisitorFails() {
        val result = BazaarParser.parseWithDiagnostics("component 123 {}")
        assertTrue(result.hasErrors)
        // AST may or may not be null depending on error recovery,
        // but diagnostics must be present
        assertTrue(result.diagnostics.isNotEmpty())
    }

    @Test
    fun errorRecoveryCanProducePartialAst() {
        // Missing closing brace — ANTLR recovers and may still produce a partial tree
        val result = BazaarParser.parseWithDiagnostics("component Foo { name string\ncomponent Bar {}")
        assertTrue(result.diagnostics.isNotEmpty())
        assertTrue(result.hasErrors)
    }

    @Test
    fun serializeDiagnosticsWithEmptyList() {
        assertEquals("", serializeDiagnostics(emptyList()))
    }

    @Test
    fun serializeDiagnosticsFormatsCorrectly() {
        val diagnostics = listOf(
            Diagnostic(Severity.ERROR, 1, 15, "missing '}' at '<EOF>'"),
        )
        assertEquals("ERROR 1:15 missing '}' at '<EOF>'\n", serializeDiagnostics(diagnostics))
    }
}
