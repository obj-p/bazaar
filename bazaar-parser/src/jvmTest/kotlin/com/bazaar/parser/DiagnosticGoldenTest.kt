package com.bazaar.parser

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class DiagnosticGoldenTest {

    private val record = System.getProperty("record") == "true"
    private val testdataDir = System.getProperty("testdata.dir")
        ?: error("testdata.dir system property not set")

    private fun goldenTest(name: String) {
        val source = javaClass.getResourceAsStream("/testdata/errors/$name.bzr")
            ?.bufferedReader()?.readText()
            ?: fail("Test file not found: /testdata/errors/$name.bzr")

        val result = BazaarParser.parseWithDiagnostics(source)
        val actual = serializeDiagnostics(result.diagnostics)

        val goldenFile = File(testdataDir, "errors/$name.bzr.diagnostics.golden")

        if (record) {
            goldenFile.writeText(actual)
            println("Recorded golden file: ${goldenFile.absolutePath}")
            return
        }

        if (!goldenFile.exists()) {
            val classpathGolden = javaClass.getResourceAsStream("/testdata/errors/$name.bzr.diagnostics.golden")
                ?.bufferedReader()?.readText()
            if (classpathGolden == null) {
                fail(
                    "Golden file not found: ${goldenFile.absolutePath}\n" +
                        "Run with -Drecord=true to generate it.",
                )
            }
            assertEquals(classpathGolden, actual, "Diagnostics mismatch for $name")
        } else {
            val expected = goldenFile.readText()
            assertEquals(expected, actual, "Diagnostics mismatch for $name")
        }
    }

    @Test fun missingClosingBrace() = goldenTest("missing-closing-brace")
    @Test fun unexpectedToken() = goldenTest("unexpected-token")
    @Test fun missingExpression() = goldenTest("missing-expression")
    @Test fun unterminatedString() = goldenTest("unterminated-string")
    @Test fun unexpectedEof() = goldenTest("unexpected-eof")
    @Test fun invalidEscape() = goldenTest("invalid-escape")
    @Test fun multipleErrors() = goldenTest("multiple-errors")
}
