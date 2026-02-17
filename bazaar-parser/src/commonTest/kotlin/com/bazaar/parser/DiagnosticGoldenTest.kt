package com.bazaar.parser

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class DiagnosticGoldenTest {

    private val record = getEnv("RECORD") == "true"
    private val testdataDir = getEnv("TESTDATA_DIR")
        ?: error("TESTDATA_DIR environment variable not set")

    private fun goldenTest(name: String) {
        val source = SystemFileSystem.source(Path(testdataDir, "errors", "$name.bzr")).buffered().use {
            it.readString()
        }

        val result = BazaarParser.parseWithDiagnostics(source)
        val actual = serializeDiagnostics(result.diagnostics)

        val goldenPath = Path(testdataDir, "errors", "$name.bzr.diagnostics.golden")

        if (record) {
            SystemFileSystem.sink(goldenPath).buffered().use { it.writeString(actual) }
            println("Recorded golden file: $goldenPath")
            return
        }

        if (!SystemFileSystem.exists(goldenPath)) {
            fail(
                "Golden file not found: $goldenPath\n" +
                    "Run with RECORD=true to generate it.",
            )
        }

        val expected = SystemFileSystem.source(goldenPath).buffered().use { it.readString() }
        assertEquals(expected, actual, "Diagnostics mismatch for $name")
    }

    @Test fun missingClosingBrace() = goldenTest("missing-closing-brace")
    @Test fun unexpectedToken() = goldenTest("unexpected-token")
    @Test fun missingExpression() = goldenTest("missing-expression")
    @Test fun unterminatedString() = goldenTest("unterminated-string")
    @Test fun unexpectedEof() = goldenTest("unexpected-eof")
    @Test fun invalidEscape() = goldenTest("invalid-escape")
    @Test fun multipleErrors() = goldenTest("multiple-errors")
}
