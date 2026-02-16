package com.bazaar.parser

import com.bazaar.parser.ast.AstSerializer
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class AstGoldenTest {

    private val record = System.getProperty("record") == "true"
    private val testdataDir = System.getProperty("testdata.dir")
        ?: error("testdata.dir system property not set")

    private fun goldenTest(name: String) {
        val source = javaClass.getResourceAsStream("/testdata/$name.bzr")
            ?.bufferedReader()?.readText()
            ?: fail("Test file not found: /testdata/$name.bzr")

        val ast = BazaarParser.parse(source)
        val actual = AstSerializer.serialize(ast)

        val goldenFile = File(testdataDir, "$name.bzr.ast.golden")

        if (record) {
            goldenFile.writeText(actual)
            println("Recorded golden file: ${goldenFile.absolutePath}")
            return
        }

        if (!goldenFile.exists()) {
            // Try classpath as fallback
            val classpathGolden = javaClass.getResourceAsStream("/testdata/$name.bzr.ast.golden")
                ?.bufferedReader()?.readText()
            if (classpathGolden == null) {
                fail(
                    "Golden file not found: ${goldenFile.absolutePath}\n" +
                        "Run with -Drecord=true to generate it.",
                )
            }
            assertEquals(classpathGolden, actual, "AST mismatch for $name")
        } else {
            val expected = goldenFile.readText()
            assertEquals(expected, actual, "AST mismatch for $name")
        }
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
