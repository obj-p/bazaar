package com.bazaar.sema

import com.bazaar.parser.ast.*
import com.bazaar.sema.ir.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TypeCheckerTest {

    private fun analyze(vararg decls: Decl): AnalysisResult =
        BazaarAnalyzer.analyze(BazaarFile(declarations = decls.toList()))

    // --- Field default checks ---

    @Test
    fun acceptsIntFieldWithIntDefault() {
        val result = analyze(ComponentDecl("Point", listOf(
            FieldDecl("x", ValueType("int"), NumberLiteral("0")),
        )))
        assertTrue(result.diagnostics.isEmpty())
    }

    @Test
    fun acceptsDoubleFieldWithDoubleDefault() {
        val result = analyze(ComponentDecl("Point", listOf(
            FieldDecl("x", ValueType("double"), NumberLiteral("3.14")),
        )))
        assertTrue(result.diagnostics.isEmpty())
    }

    @Test
    fun acceptsIntToDoubleWidening() {
        val result = analyze(ComponentDecl("Point", listOf(
            FieldDecl("x", ValueType("double"), NumberLiteral("0")),
        )))
        assertTrue(result.diagnostics.isEmpty())
    }

    @Test
    fun rejectsDoubleToInt() {
        val result = analyze(ComponentDecl("Point", listOf(
            FieldDecl("x", ValueType("int"), NumberLiteral("3.14")),
        )))
        assertTrue(result.hasErrors)
        assertEquals(1, result.diagnostics.size)
        assertTrue(result.diagnostics[0].message.contains("type mismatch in field 'x' of 'Point'"))
        assertTrue(result.diagnostics[0].message.contains("expected int, got double"))
    }

    @Test
    fun acceptsStringFieldWithStringDefault() {
        val result = analyze(ComponentDecl("Label", listOf(
            FieldDecl("text", ValueType("string"), StringLiteral(listOf(TextPart("hello")))),
        )))
        assertTrue(result.diagnostics.isEmpty())
    }

    @Test
    fun rejectsStringToInt() {
        val result = analyze(ComponentDecl("Label", listOf(
            FieldDecl("count", ValueType("int"), StringLiteral(listOf(TextPart("hello")))),
        )))
        assertTrue(result.hasErrors)
        assertTrue(result.diagnostics[0].message.contains("expected int, got string"))
    }

    @Test
    fun acceptsBoolFieldWithBoolDefault() {
        val result = analyze(ComponentDecl("Toggle", listOf(
            FieldDecl("enabled", ValueType("bool"), BoolLiteral(true)),
        )))
        assertTrue(result.diagnostics.isEmpty())
    }

    @Test
    fun acceptsNullDefaultForNullableField() {
        val result = analyze(ComponentDecl("Model", listOf(
            FieldDecl("name", ValueType("string", nullable = true), NullLiteral),
        )))
        assertTrue(result.diagnostics.isEmpty())
    }

    @Test
    fun rejectsNullDefaultForNonNullableField() {
        val result = analyze(ComponentDecl("Model", listOf(
            FieldDecl("name", ValueType("string"), NullLiteral),
        )))
        assertTrue(result.hasErrors)
        assertTrue(result.diagnostics[0].message.contains("null is not assignable to non-nullable type 'string'"))
    }

    @Test
    fun acceptsValidEnumDefault() {
        val result = analyze(
            EnumDecl("RowAlignment", listOf("top", "center", "bottom")),
            ComponentDecl("Row", listOf(
                FieldDecl("alignment", ValueType("RowAlignment"),
                    MemberExpr(ReferenceExpr("RowAlignment"), "center")),
            )),
        )
        assertTrue(result.diagnostics.isEmpty())
    }

    @Test
    fun rejectsInvalidEnumValue() {
        val result = analyze(
            EnumDecl("RowAlignment", listOf("top", "center", "bottom")),
            ComponentDecl("Row", listOf(
                FieldDecl("alignment", ValueType("RowAlignment"),
                    MemberExpr(ReferenceExpr("RowAlignment"), "middle")),
            )),
        )
        assertTrue(result.hasErrors)
        assertTrue(result.diagnostics[0].message.contains("unknown enum value 'middle'"))
        assertTrue(result.diagnostics[0].message.contains("'RowAlignment' has values 'top', 'center', 'bottom'"))
    }

    // --- Param default checks ---

    @Test
    fun acceptsValidParamDefault() {
        val result = analyze(FunctionDecl("dismiss", listOf(
            ParameterDecl("animated", ValueType("bool"), BoolLiteral(true)),
        ), returnType = null))
        assertTrue(result.diagnostics.isEmpty())
    }

    @Test
    fun rejectsInvalidParamDefault() {
        val result = analyze(FunctionDecl("dismiss", listOf(
            ParameterDecl("animated", ValueType("bool"), NumberLiteral("1")),
        ), returnType = null))
        assertTrue(result.hasErrors)
        assertTrue(result.diagnostics[0].message.contains("type mismatch in parameter 'animated' of 'dismiss'"))
        assertTrue(result.diagnostics[0].message.contains("expected bool, got int"))
    }

    // --- Fields without defaults ---

    @Test
    fun skipsFieldsWithoutDefaults() {
        val result = analyze(ComponentDecl("Point", listOf(
            FieldDecl("x", ValueType("int")),
            FieldDecl("y", ValueType("int")),
        )))
        assertTrue(result.diagnostics.isEmpty())
    }

    // --- Multiple errors ---

    @Test
    fun reportsMultipleErrors() {
        val result = analyze(ComponentDecl("Bad", listOf(
            FieldDecl("a", ValueType("int"), StringLiteral(listOf(TextPart("oops")))),
            FieldDecl("b", ValueType("bool"), NumberLiteral("42")),
        )))
        assertTrue(result.hasErrors)
        assertEquals(2, result.diagnostics.size)
    }

    // --- Empty collection defaults ---

    @Test
    fun acceptsEmptyArrayDefault() {
        val result = analyze(ComponentDecl("List", listOf(
            FieldDecl("items", ArrayType(ValueType("string")), ArrayLiteral(emptyList())),
        )))
        assertTrue(result.diagnostics.isEmpty())
    }

    @Test
    fun acceptsEmptyMapDefault() {
        val result = analyze(ComponentDecl("Config", listOf(
            FieldDecl("settings", MapType(ValueType("string"), ValueType("int")), MapLiteral(emptyList())),
        )))
        assertTrue(result.diagnostics.isEmpty())
    }

    // --- Constructor checks ---

    @Test
    fun acceptsValidConstructor() {
        val result = analyze(ComponentDecl("Padding", listOf(
            FieldDecl("top", ValueType("double")),
            FieldDecl("bottom", ValueType("double")),
            FieldDecl("left", ValueType("double")),
            FieldDecl("right", ValueType("double")),
        ) + listOf(ConstructorDecl(
            listOf(
                ParameterDecl("top", ValueType("double")),
                ParameterDecl("bottom", ValueType("double")),
                ParameterDecl("left", ValueType("double")),
                ParameterDecl("right", ValueType("double")),
            ),
            CallExpr(ReferenceExpr("Padding"), listOf(
                Argument(value = ReferenceExpr("top")),
                Argument(value = ReferenceExpr("bottom")),
                Argument(value = ReferenceExpr("left")),
                Argument(value = ReferenceExpr("right")),
            )),
        ))))
        assertTrue(result.diagnostics.isEmpty())
    }

    @Test
    fun rejectsConstructorWithWrongArgCount() {
        val result = analyze(ComponentDecl("Padding", listOf(
            FieldDecl("top", ValueType("double")),
            FieldDecl("bottom", ValueType("double")),
            FieldDecl("left", ValueType("double")),
            FieldDecl("right", ValueType("double")),
        ) + listOf(ConstructorDecl(
            listOf(
                ParameterDecl("value", ValueType("double")),
            ),
            CallExpr(ReferenceExpr("Padding"), listOf(
                Argument(value = ReferenceExpr("value")),
                Argument(value = ReferenceExpr("value")),
            )),
        ))))
        assertTrue(result.hasErrors)
        assertTrue(result.diagnostics.any {
            it.message.contains("expected 4 arguments but got 2")
        })
    }

    @Test
    fun rejectsConstructorArgTypeMismatch() {
        val result = analyze(ComponentDecl("Point", listOf(
            FieldDecl("x", ValueType("double")),
            FieldDecl("y", ValueType("double")),
        ) + listOf(ConstructorDecl(
            listOf(
                ParameterDecl("x", ValueType("string")),
                ParameterDecl("y", ValueType("double")),
            ),
            CallExpr(ReferenceExpr("Point"), listOf(
                Argument(value = ReferenceExpr("x")),
                Argument(value = ReferenceExpr("y")),
            )),
        ))))
        assertTrue(result.hasErrors)
        assertTrue(result.diagnostics.any {
            it.message.contains("argument 1 has type string but field 'x' expects double")
        })
    }

    @Test
    fun acceptsIntToDoubleWideningInConstructor() {
        val result = analyze(ComponentDecl("Point", listOf(
            FieldDecl("x", ValueType("double")),
        ) + listOf(ConstructorDecl(
            listOf(
                ParameterDecl("x", ValueType("int")),
            ),
            CallExpr(ReferenceExpr("Point"), listOf(
                Argument(value = ReferenceExpr("x")),
            )),
        ))))
        assertTrue(result.diagnostics.isEmpty())
    }

    @Test
    fun rejectsConstructorWithWrongCallTarget() {
        val result = analyze(
            ComponentDecl("Other"),
            ComponentDecl("Padding", listOf(
                FieldDecl("top", ValueType("double")),
            ) + listOf(ConstructorDecl(
                listOf(ParameterDecl("top", ValueType("double"))),
                CallExpr(ReferenceExpr("Other"), listOf(
                    Argument(value = ReferenceExpr("top")),
                )),
            ))),
        )
        assertTrue(result.hasErrors)
        assertTrue(result.diagnostics.any {
            it.message.contains("must construct an instance of 'Padding', but calls 'Other'")
        })
    }

    @Test
    fun checksMultipleConstructors() {
        val result = analyze(ComponentDecl("Padding", listOf(
            FieldDecl("top", ValueType("double")),
            FieldDecl("bottom", ValueType("double")),
        ) + listOf(
            // Valid constructor
            ConstructorDecl(
                listOf(
                    ParameterDecl("top", ValueType("double")),
                    ParameterDecl("bottom", ValueType("double")),
                ),
                CallExpr(ReferenceExpr("Padding"), listOf(
                    Argument(value = ReferenceExpr("top")),
                    Argument(value = ReferenceExpr("bottom")),
                )),
            ),
            // Invalid constructor — wrong arg count
            ConstructorDecl(
                listOf(ParameterDecl("all", ValueType("double"))),
                CallExpr(ReferenceExpr("Padding"), listOf(
                    Argument(value = ReferenceExpr("all")),
                )),
            ),
        )))
        assertTrue(result.hasErrors)
        assertTrue(result.diagnostics.any {
            it.message.contains("expected 2 arguments but got 1")
        })
    }

    @Test
    fun acceptsConstructorWithLiteralArgs() {
        val result = analyze(ComponentDecl("Point", listOf(
            FieldDecl("x", ValueType("int")),
            FieldDecl("y", ValueType("int")),
        ) + listOf(ConstructorDecl(
            emptyList(),
            CallExpr(ReferenceExpr("Point"), listOf(
                Argument(value = NumberLiteral("0")),
                Argument(value = NumberLiteral("0")),
            )),
        ))))
        assertTrue(result.diagnostics.isEmpty())
    }

    @Test
    fun rejectsNullConstructorArgForNonNullableField() {
        val result = analyze(ComponentDecl("Point", listOf(
            FieldDecl("x", ValueType("int")),
        ) + listOf(ConstructorDecl(
            emptyList(),
            CallExpr(ReferenceExpr("Point"), listOf(
                Argument(value = NullLiteral),
            )),
        ))))
        assertTrue(result.hasErrors)
        assertTrue(result.diagnostics.any {
            it.message.contains("argument 1 is null") &&
                it.message.contains("field 'x' expects non-nullable int")
        })
    }

    @Test
    fun acceptsNullConstructorArgForNullableField() {
        val result = analyze(ComponentDecl("Model", listOf(
            FieldDecl("name", ValueType("string", nullable = true)),
        ) + listOf(ConstructorDecl(
            emptyList(),
            CallExpr(ReferenceExpr("Model"), listOf(
                Argument(value = NullLiteral),
            )),
        ))))
        assertTrue(result.diagnostics.isEmpty())
    }

    // --- Data and modifier declaration checks ---

    @Test
    fun checksDataDeclFieldDefaults() {
        val result = analyze(DataDecl("Model", listOf(
            FieldDecl("count", ValueType("int"), StringLiteral(listOf(TextPart("bad")))),
        )))
        assertTrue(result.hasErrors)
        assertTrue(result.diagnostics[0].message.contains("type mismatch in field 'count' of 'Model'"))
    }

    @Test
    fun checksModifierDeclFieldDefaults() {
        val result = analyze(ModifierDecl("Padding", listOf(
            FieldDecl("top", ValueType("double"), BoolLiteral(true)),
        )))
        assertTrue(result.hasErrors)
        assertTrue(result.diagnostics[0].message.contains("type mismatch in field 'top' of 'Padding'"))
    }

    @Test
    fun checksTemplateParamDefaults() {
        val result = analyze(TemplateDecl("ItemList", listOf(
            ParameterDecl("count", ValueType("int"), StringLiteral(listOf(TextPart("bad")))),
        )))
        assertTrue(result.hasErrors)
        assertTrue(result.diagnostics[0].message.contains("type mismatch in parameter 'count' of 'ItemList'"))
    }

    // --- Non-empty collection type mismatch ---

    @Test
    fun rejectsArrayWithWrongElementType() {
        val result = analyze(ComponentDecl("List", listOf(
            FieldDecl("items", ArrayType(ValueType("int")),
                ArrayLiteral(listOf(StringLiteral(listOf(TextPart("bad")))))),
        )))
        assertTrue(result.hasErrors)
        assertTrue(result.diagnostics[0].message.contains("expected [int], got [string]"))
    }
}
