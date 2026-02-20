package com.bazaar.sema

import com.bazaar.parser.ast.*
import com.bazaar.sema.ir.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AnalyzerTest {

    @Test
    fun analyzesSimpleComponent() {
        val file = BazaarFile(declarations = listOf(
            ComponentDecl("Button", listOf(
                FieldDecl("label", ValueType("string")),
                FieldDecl("onClick", FunctionType(emptyList(), nullable = true)),
            )),
        ))
        val result = BazaarAnalyzer.analyze(file)

        assertTrue(result.diagnostics.isEmpty())
        assertNotNull(result.ir)
        assertEquals(1, result.ir!!.declarations.size)

        val comp = assertIs<IrComponent>(result.ir!!.declarations[0])
        assertEquals("Button", comp.name)
        assertEquals(2, comp.fields.size)

        val labelType = assertIs<IrBuiltinType>(comp.fields[0].type)
        assertEquals("string", labelType.name)

        val onClickType = assertIs<IrFunctionType>(comp.fields[1].type)
        assertTrue(onClickType.nullable)
    }

    @Test
    fun analyzesMultipleDeclarations() {
        val file = BazaarFile(
            packageDecl = PackageDecl(listOf("com", "example")),
            declarations = listOf(
                EnumDecl("Alignment", listOf("start", "center", "end")),
                ComponentDecl("Row", listOf(
                    FieldDecl("alignment", ValueType("Alignment")),
                )),
                DataDecl("Model", listOf(
                    FieldDecl("name", ValueType("string")),
                )),
            ),
        )
        val result = BazaarAnalyzer.analyze(file)

        assertTrue(result.diagnostics.isEmpty())
        assertNotNull(result.ir)
        assertEquals("com.example", result.ir!!.packageName)
        assertEquals(3, result.ir!!.declarations.size)

        assertIs<IrEnum>(result.ir!!.declarations[0])
        val row = assertIs<IrComponent>(result.ir!!.declarations[1])
        val alignmentType = assertIs<IrDeclaredType>(row.fields[0].type)
        assertEquals("Alignment", alignmentType.name)
        assertEquals(SymbolKind.ENUM, alignmentType.symbolKind)
        assertIs<IrData>(result.ir!!.declarations[2])
    }

    @Test
    fun returnsNullIrOnDuplicateError() {
        val file = BazaarFile(declarations = listOf(
            ComponentDecl("Foo"),
            DataDecl("Foo"),
        ))
        val result = BazaarAnalyzer.analyze(file)

        assertTrue(result.hasErrors)
        assertNull(result.ir)
        assertTrue(result.diagnostics.any { it.message.contains("duplicate") })
    }

    @Test
    fun returnsNullIrOnUndefinedType() {
        val file = BazaarFile(declarations = listOf(
            ComponentDecl("Box", listOf(
                FieldDecl("child", ValueType("Missing")),
            )),
        ))
        val result = BazaarAnalyzer.analyze(file)

        assertTrue(result.hasErrors)
        assertNull(result.ir)
        assertTrue(result.diagnostics.any { it.message.contains("undefined type") })
    }

    @Test
    fun returnsNullIrOnBuiltinShadow() {
        val file = BazaarFile(declarations = listOf(
            ComponentDecl("int"),
        ))
        val result = BazaarAnalyzer.analyze(file)

        assertTrue(result.hasErrors)
        assertNull(result.ir)
        assertTrue(result.diagnostics.any { it.message.contains("shadows built-in") })
    }

    @Test
    fun analyzesEmptyFile() {
        val file = BazaarFile()
        val result = BazaarAnalyzer.analyze(file)

        assertTrue(result.diagnostics.isEmpty())
        assertNotNull(result.ir)
        assertTrue(result.ir!!.declarations.isEmpty())
    }

    @Test
    fun analyzesTemplateWithParams() {
        val file = BazaarFile(declarations = listOf(
            DataDecl("ItemModel", listOf(
                FieldDecl("value", ValueType("string")),
            )),
            TemplateDecl("ItemList", listOf(
                ParameterDecl("items", ArrayType(ValueType("ItemModel"))),
            )),
        ))
        val result = BazaarAnalyzer.analyze(file)

        assertTrue(result.diagnostics.isEmpty())
        assertNotNull(result.ir)

        val tmpl = assertIs<IrTemplate>(result.ir!!.declarations[1])
        assertEquals("ItemList", tmpl.name)
        val paramType = assertIs<IrArrayType>(tmpl.params[0].type)
        val elemType = assertIs<IrDeclaredType>(paramType.elementType)
        assertEquals("ItemModel", elemType.name)
        assertEquals(SymbolKind.DATA, elemType.symbolKind)
    }

    @Test
    fun collectsDiagnosticsFromBothPasses() {
        val file = BazaarFile(declarations = listOf(
            ComponentDecl("A"),
            ComponentDecl("A"),
            ComponentDecl("B", listOf(
                FieldDecl("x", ValueType("Unknown")),
            )),
        ))
        val result = BazaarAnalyzer.analyze(file)

        assertTrue(result.hasErrors)
        assertNull(result.ir)
        // One duplicate error from Pass 1, one undefined type from Pass 2
        assertTrue(result.diagnostics.any { it.message.contains("duplicate") })
        assertTrue(result.diagnostics.any { it.message.contains("undefined type") })
    }

    @Test
    fun analyzesFullPipeline() {
        val file = BazaarFile(
            packageDecl = PackageDecl(listOf("com", "example", "sdui")),
            declarations = listOf(
                EnumDecl("RowAlignment", listOf("start", "center", "end")),
                ComponentDecl("Text", listOf(
                    FieldDecl("value", ValueType("string")),
                )),
                ComponentDecl("Button", listOf(
                    FieldDecl("label", ValueType("string")),
                    FieldDecl("onClick", FunctionType(emptyList(), nullable = true)),
                )),
                ModifierDecl("Padding", listOf(
                    FieldDecl("top", ValueType("int")),
                    FieldDecl("bottom", ValueType("int")),
                )),
                DataDecl("ButtonModel", listOf(
                    FieldDecl("text", ValueType("string")),
                    FieldDecl("action", FunctionType(emptyList())),
                )),
                FunctionDecl("formatLabel", listOf(
                    ParameterDecl("raw", ValueType("string")),
                ), returnType = ValueType("string")),
                TemplateDecl("ButtonRow", listOf(
                    ParameterDecl("models", ArrayType(ValueType("ButtonModel"))),
                ), body = listOf(
                    ForInStmt(listOf("model"), ReferenceExpr("models"), listOf(
                        ExprStmt(CallExpr(ReferenceExpr("Button"), listOf(
                            Argument(value = ReferenceExpr("model")),
                        ))),
                    )),
                )),
                PreviewDecl("PreviewButtonRow", body = listOf(
                    ExprStmt(CallExpr(ReferenceExpr("ButtonRow"), listOf(
                        Argument(value = ArrayLiteral(emptyList())),
                    ))),
                )),
            ),
        )
        val result = BazaarAnalyzer.analyze(file)

        assertTrue(result.diagnostics.isEmpty())
        assertNotNull(result.ir)
        assertEquals("com.example.sdui", result.ir!!.packageName)
        assertEquals(8, result.ir!!.declarations.size)

        assertIs<IrEnum>(result.ir!!.declarations[0])
        assertIs<IrComponent>(result.ir!!.declarations[1])
        assertIs<IrComponent>(result.ir!!.declarations[2])
        assertIs<IrModifier>(result.ir!!.declarations[3])
        assertIs<IrData>(result.ir!!.declarations[4])
        assertIs<IrFunction>(result.ir!!.declarations[5])

        // Verify template body is analyzed (Pass 4)
        val tmpl = assertIs<IrTemplate>(result.ir!!.declarations[6])
        assertEquals(1, tmpl.body.size)
        val forNode = assertIs<IrForNode>(tmpl.body[0])
        assertEquals(1, forNode.body.size)
        assertIs<IrComponentCall>(forNode.body[0])

        // Verify preview body is analyzed (Pass 4)
        val preview = assertIs<IrPreview>(result.ir!!.declarations[7])
        assertEquals(1, preview.body.size)
        val call = assertIs<IrComponentCall>(preview.body[0])
        assertEquals("ButtonRow", call.name)
        assertEquals(SymbolKind.TEMPLATE, call.symbolKind)
    }

    @Test
    fun fullPipelineWithStateAndModifier() {
        val file = BazaarFile(declarations = listOf(
            ComponentDecl("Text"),
            ComponentDecl("Row"),
            ModifierDecl("Padding", listOf(FieldDecl("all", ValueType("double")))),
            FunctionDecl("Print"),
            TemplateDecl("MyScreen", body = listOf(
                VarDeclStmt(listOf("count"), value = NumberLiteral("0"), annotations = listOf(Annotation("State"))),
                CallStmt(
                    CallExpr(ReferenceExpr("Row"), emptyList(), trailingLambda = LambdaExpr(
                        params = emptyList(),
                        body = listOf(
                            ExprStmt(CallExpr(ReferenceExpr("Text"), emptyList())),
                        ),
                    )),
                    annotations = listOf(Annotation("Modifier", listOf(
                        Argument(value = CallExpr(ReferenceExpr("Padding"), listOf(
                            Argument(name = "all", value = NumberLiteral("12.0")),
                        ))),
                    ))),
                ),
                ForInStmt(listOf("i"), ReferenceExpr("items"), listOf(
                    ExprStmt(CallExpr(ReferenceExpr("Text"), emptyList())),
                )),
            )),
        ))
        val result = BazaarAnalyzer.analyze(file)

        assertTrue(result.diagnostics.isEmpty(), "diagnostics: ${result.diagnostics}")
        assertNotNull(result.ir)

        val tmpl = assertIs<IrTemplate>(result.ir!!.declarations[4])
        assertEquals(3, tmpl.body.size)

        // @State var count = 0
        val state = assertIs<IrStateDecl>(tmpl.body[0])
        assertEquals("count", state.name)

        // @Modifier(Padding(all = 12.0)) Row { Text() }
        val row = assertIs<IrComponentCall>(tmpl.body[1])
        assertEquals("Row", row.name)
        assertNotNull(row.modifier)
        assertEquals("Padding", row.modifier!!.name)
        assertEquals(1, row.children.size)
        assertIs<IrComponentCall>(row.children[0])

        // for i in items { Text() }
        val forNode = assertIs<IrForNode>(tmpl.body[2])
        assertEquals(1, forNode.body.size)
        assertIs<IrComponentCall>(forNode.body[0])
    }

    @Test
    fun previewBodyWithTemplateCall() {
        val file = BazaarFile(declarations = listOf(
            TemplateDecl("MainScreen"),
            PreviewDecl("PreviewMain", body = listOf(
                ExprStmt(CallExpr(ReferenceExpr("MainScreen"), emptyList())),
            )),
        ))
        val result = BazaarAnalyzer.analyze(file)

        assertTrue(result.diagnostics.isEmpty())
        assertNotNull(result.ir)

        val preview = assertIs<IrPreview>(result.ir!!.declarations[1])
        assertEquals(1, preview.body.size)
        val call = assertIs<IrComponentCall>(preview.body[0])
        assertEquals("MainScreen", call.name)
        assertEquals(SymbolKind.TEMPLATE, call.symbolKind)
    }

    @Test
    fun templateWithUndefinedCallTargetProducesNullIr() {
        val file = BazaarFile(declarations = listOf(
            TemplateDecl("MyScreen", body = listOf(
                ExprStmt(CallExpr(ReferenceExpr("Undefined"), emptyList())),
            )),
        ))
        val result = BazaarAnalyzer.analyze(file)

        assertTrue(result.hasErrors)
        assertNull(result.ir)
        assertTrue(result.diagnostics.any { it.message.contains("undefined call target 'Undefined'") })
    }

    @Test
    fun pass4SkippedWhenPriorErrorsExist() {
        val file = BazaarFile(declarations = listOf(
            ComponentDecl("A"),
            ComponentDecl("A"), // duplicate — Pass 1 error
            TemplateDecl("MyScreen", body = listOf(
                ExprStmt(CallExpr(ReferenceExpr("Unknown"), emptyList())),
            )),
        ))
        val result = BazaarAnalyzer.analyze(file)

        assertTrue(result.hasErrors)
        assertNull(result.ir)
        // Should have duplicate error from Pass 1
        assertTrue(result.diagnostics.any { it.message.contains("duplicate") })
        // Should NOT have template analysis error (Pass 4 skipped)
        assertTrue(result.diagnostics.none { it.message.contains("undefined call target") })
    }
}
