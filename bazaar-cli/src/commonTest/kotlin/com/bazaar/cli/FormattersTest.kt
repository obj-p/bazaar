package com.bazaar.cli

import com.bazaar.parser.Diagnostic
import com.bazaar.parser.ParseResult
import com.bazaar.parser.Severity
import com.bazaar.parser.ast.BazaarFile
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FormattersTest {
    private val emptyAst = BazaarFile()

    // --- formatTextResult ---

    @Test
    fun textSingleFileNoHeader() {
        val result = ParseResult(emptyAst, emptyList())
        val text = formatTextResult("file.bzr", result, multiFile = false)
        assertTrue(!text.startsWith("---"))
    }

    @Test
    fun textMultiFileHasHeader() {
        val result = ParseResult(emptyAst, emptyList())
        val text = formatTextResult("file.bzr", result, multiFile = true)
        assertTrue(text.startsWith("--- file.bzr ---"))
    }

    @Test
    fun textSuccessfulParseShowsAst() {
        val result = ParseResult(emptyAst, emptyList())
        val text = formatTextResult("file.bzr", result, multiFile = false)
        assertContains(text, "BazaarFile")
    }

    @Test
    fun textAstWithDiagnosticsConcatenated() {
        val diags = listOf(Diagnostic(Severity.ERROR, 1, 5, "unexpected token"))
        val result = ParseResult(emptyAst, diags)
        val text = formatTextResult("file.bzr", result, multiFile = false)
        assertContains(text, "BazaarFile")
        assertContains(text, "ERROR 1:5 unexpected token")
    }

    @Test
    fun textNullAstWithDiagnostics() {
        val diags = listOf(Diagnostic(Severity.ERROR, 1, 1, "parse error"))
        val result = ParseResult(null, diags)
        val text = formatTextResult("file.bzr", result, multiFile = false)
        assertContains(text, "ERROR 1:1 parse error")
        assertTrue(!text.contains("BazaarFile"))
    }

    @Test
    fun textNullAstEmptyDiagnostics() {
        val result = ParseResult(null, emptyList())
        val text = formatTextResult("file.bzr", result, multiFile = false)
        assertEquals("", text)
    }

    // --- formatJsonResult ---

    @Test
    fun jsonSuccessfulParse() {
        val result = ParseResult(emptyAst, emptyList())
        val json = formatJsonResult("file.bzr", result)
        assertContains(json, "\"status\": \"ok\"")
        assertContains(json, "\"ast\":")
        assertContains(json, "\"diagnostics\": []")
    }

    @Test
    fun jsonNullAstWithErrors() {
        val diags = listOf(Diagnostic(Severity.ERROR, 2, 3, "bad syntax"))
        val result = ParseResult(null, diags)
        val json = formatJsonResult("file.bzr", result)
        assertContains(json, "\"status\": \"error\"")
        assertTrue(!json.contains("\"ast\":"))
        assertContains(json, "\"severity\": \"ERROR\"")
        assertContains(json, "\"message\": \"bad syntax\"")
        assertContains(json, "\"line\": 2")
        assertContains(json, "\"column\": 3")
    }

    @Test
    fun jsonNonNullAstWithErrors() {
        val diags = listOf(Diagnostic(Severity.ERROR, 1, 1, "recovered"))
        val result = ParseResult(emptyAst, diags)
        val json = formatJsonResult("file.bzr", result)
        assertContains(json, "\"status\": \"error\"")
        assertContains(json, "\"ast\":")
    }

    @Test
    fun jsonEscapesQuotesAndBackslashes() {
        val result = ParseResult(null, emptyList())
        val json = formatJsonResult("path\\to\\\"file\".bzr", result)
        assertContains(json, "path\\\\to\\\\\\\"file\\\"")
    }

    @Test
    fun jsonEscapesNewlines() {
        val diags = listOf(Diagnostic(Severity.ERROR, 1, 1, "line1\nline2"))
        val result = ParseResult(null, diags)
        val json = formatJsonResult("file.bzr", result)
        assertContains(json, "line1\\nline2")
    }

    @Test
    fun jsonEscapesControlChars() {
        val diags = listOf(Diagnostic(Severity.ERROR, 1, 1, "bell\u0007here"))
        val result = ParseResult(null, diags)
        val json = formatJsonResult("file.bzr", result)
        assertContains(json, "bell\\u0007here")
    }

    @Test
    fun jsonMultipleDiagnostics() {
        val diags =
            listOf(
                Diagnostic(Severity.ERROR, 1, 1, "first"),
                Diagnostic(Severity.WARNING, 2, 5, "second"),
                Diagnostic(Severity.ERROR, 3, 10, "third"),
            )
        val result = ParseResult(null, diags)
        val json = formatJsonResult("file.bzr", result)
        assertContains(json, "\"message\": \"first\"")
        assertContains(json, "\"message\": \"second\"")
        assertContains(json, "\"message\": \"third\"")
        assertContains(json, "\"severity\": \"WARNING\"")
    }

    @Test
    fun jsonWarningOnlyIsStatusOk() {
        val diags = listOf(Diagnostic(Severity.WARNING, 1, 1, "just a warning"))
        val result = ParseResult(emptyAst, diags)
        val json = formatJsonResult("file.bzr", result)
        assertContains(json, "\"status\": \"ok\"")
        assertContains(json, "\"severity\": \"WARNING\"")
    }

    @Test
    fun jsonFileFieldPresent() {
        val result = ParseResult(emptyAst, emptyList())
        val json = formatJsonResult("test.bzr", result)
        assertContains(json, "\"file\": \"test.bzr\"")
    }
}
