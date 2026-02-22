package com.bazaar.parser

import com.bazaar.parser.ast.AstSerializer
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class AstGoldenTest {
    private val record = getEnv("RECORD") == "true"
    private val testdataDir =
        getEnv("TESTDATA_DIR")
            ?: error("TESTDATA_DIR environment variable not set")

    private fun goldenTest(name: String) {
        val source =
            SystemFileSystem.source(Path(testdataDir, "$name.bzr")).buffered().use {
                it.readString()
            }

        val ast = BazaarParser.parse(source)
        val actual = AstSerializer.serialize(ast)

        val goldenPath = Path(testdataDir, "$name.bzr.ast.golden")

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
        assertEquals(expected, actual, "AST mismatch for $name")
    }

    @Test fun annotations() = goldenTest("annotations")

    @Test fun arithmetic() = goldenTest("arithmetic")

    @Test fun arrays() = goldenTest("arrays")

    @Test fun assigns() = goldenTest("assigns")

    @Test fun builtins() = goldenTest("builtins")

    @Test fun calls() = goldenTest("calls")

    @Test fun edgeCases() = goldenTest("edge-cases")

    @Test fun example() = goldenTest("example")

    @Test fun fors() = goldenTest("fors")

    @Test fun functions() = goldenTest("functions")

    @Test fun ifs() = goldenTest("ifs")

    @Test fun imports() = goldenTest("imports")

    @Test fun lambdas() = goldenTest("lambdas")

    @Test fun maps() = goldenTest("maps")

    @Test fun operators() = goldenTest("operators")

    @Test fun optionalChaining() = goldenTest("optional-chaining")

    @Test fun references() = goldenTest("references")

    @Test fun scientific() = goldenTest("scientific")

    @Test fun strings() = goldenTest("strings")

    @Test fun switches() = goldenTest("switches")

    @Test fun todo() = goldenTest("todo")
}
