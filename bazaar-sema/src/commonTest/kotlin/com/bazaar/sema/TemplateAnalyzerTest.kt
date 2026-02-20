package com.bazaar.sema

import com.bazaar.parser.ast.*
import com.bazaar.sema.ir.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TemplateAnalyzerTest {

    // -- Helpers --

    private fun buildIr(
        declarations: List<Decl>,
    ): Pair<IrFile, SymbolTable> {
        val file = BazaarFile(declarations = declarations)
        val collection = DeclarationCollector.collect(file)
        assertTrue(collection.diagnostics.isEmpty(), "setup: declaration errors: ${collection.diagnostics}")
        val resolution = TypeResolver.resolve(file, collection.symbolTable)
        assertTrue(resolution.diagnostics.isEmpty(), "setup: type resolution errors: ${resolution.diagnostics}")
        return resolution.ir to collection.symbolTable
    }

    private fun analyzeTemplate(
        declarations: List<Decl>,
    ): TemplateAnalysisResult {
        val (ir, symbolTable) = buildIr(declarations)
        return TemplateAnalyzer.analyze(ir, symbolTable)
    }

    private fun ref(name: String) = ReferenceExpr(name)
    private fun call(name: String, vararg args: Argument, trailingLambda: LambdaExpr? = null) =
        CallExpr(ref(name), args.toList(), trailingLambda)
    private fun callStmt(name: String, vararg args: Argument, trailingLambda: LambdaExpr? = null, annotations: List<Annotation> = emptyList()) =
        CallStmt(call(name, *args, trailingLambda = trailingLambda), annotations)
    private fun exprCallStmt(name: String, vararg args: Argument, trailingLambda: LambdaExpr? = null) =
        ExprStmt(call(name, *args, trailingLambda = trailingLambda))
    private fun lambda(vararg stmts: Stmt) = LambdaExpr(params = emptyList(), body = stmts.toList())

    // ============================================================
    // Call resolution
    // ============================================================

    @Test
    fun simpleComponentCallViaExprStmt() {
        val result = analyzeTemplate(listOf(
            ComponentDecl("Text", listOf(FieldDecl("value", ValueType("string")))),
            TemplateDecl("MyScreen", body = listOf(
                exprCallStmt("Text", Argument(value = NumberLiteral("42"))),
            )),
        ))

        assertTrue(result.diagnostics.isEmpty())
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[1])
        assertEquals(1, tmpl.body.size)
        val node = assertIs<IrComponentCall>(tmpl.body[0])
        assertEquals("Text", node.name)
        assertEquals(SymbolKind.COMPONENT, node.symbolKind)
        assertEquals(1, node.args.size)
        assertNull(node.modifier)
        assertTrue(node.children.isEmpty())
    }

    @Test
    fun componentCallWithTrailingLambda() {
        val result = analyzeTemplate(listOf(
            ComponentDecl("Text", listOf(FieldDecl("value", ValueType("string")))),
            ComponentDecl("Row"),
            TemplateDecl("MyScreen", body = listOf(
                exprCallStmt("Row", trailingLambda = lambda(
                    exprCallStmt("Text", Argument(value = NumberLiteral("1"))),
                )),
            )),
        ))

        assertTrue(result.diagnostics.isEmpty())
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[2])
        val row = assertIs<IrComponentCall>(tmpl.body[0])
        assertEquals("Row", row.name)
        assertEquals(1, row.children.size)
        val text = assertIs<IrComponentCall>(row.children[0])
        assertEquals("Text", text.name)
    }

    @Test
    fun nestedComponentCalls() {
        val result = analyzeTemplate(listOf(
            ComponentDecl("Text"),
            ComponentDecl("Button"),
            ComponentDecl("Row"),
            TemplateDecl("MyScreen", body = listOf(
                exprCallStmt("Row", trailingLambda = lambda(
                    exprCallStmt("Text"),
                    exprCallStmt("Button", trailingLambda = lambda(
                        AssignStmt("count", AssignOp.ADD_ASSIGN, NumberLiteral("1")),
                    )),
                )),
            )),
        ))

        assertTrue(result.diagnostics.isEmpty())
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[3])
        val row = assertIs<IrComponentCall>(tmpl.body[0])
        assertEquals(2, row.children.size)
        assertIs<IrComponentCall>(row.children[0])
        val button = assertIs<IrComponentCall>(row.children[1])
        assertEquals(1, button.children.size)
        assertIs<IrAssignNode>(button.children[0])
    }

    @Test
    fun templateCallProducesComponentCall() {
        val result = analyzeTemplate(listOf(
            TemplateDecl("Header"),
            TemplateDecl("MyScreen", body = listOf(
                exprCallStmt("Header"),
            )),
        ))

        assertTrue(result.diagnostics.isEmpty())
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[1])
        val node = assertIs<IrComponentCall>(tmpl.body[0])
        assertEquals("Header", node.name)
        assertEquals(SymbolKind.TEMPLATE, node.symbolKind)
    }

    @Test
    fun functionCallProducesFunctionCall() {
        val result = analyzeTemplate(listOf(
            FunctionDecl("Print", listOf(ParameterDecl("msg", ValueType("string")))),
            TemplateDecl("MyScreen", body = listOf(
                exprCallStmt("Print", Argument(value = NumberLiteral("1"))),
            )),
        ))

        assertTrue(result.diagnostics.isEmpty())
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[1])
        val node = assertIs<IrFunctionCall>(tmpl.body[0])
        assertEquals("Print", node.name)
        assertEquals(1, node.args.size)
        assertNull(node.trailingLambda)
    }

    @Test
    fun dataConstructorCallProducesFunctionCall() {
        val result = analyzeTemplate(listOf(
            DataDecl("Model", listOf(FieldDecl("name", ValueType("string")))),
            TemplateDecl("MyScreen", body = listOf(
                exprCallStmt("Model", Argument(value = NumberLiteral("1"))),
            )),
        ))

        assertTrue(result.diagnostics.isEmpty())
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[1])
        val node = assertIs<IrFunctionCall>(tmpl.body[0])
        assertEquals("Model", node.name)
    }

    @Test
    fun undefinedCallTargetProducesError() {
        val result = analyzeTemplate(listOf(
            TemplateDecl("MyScreen", body = listOf(
                exprCallStmt("Foo"),
            )),
        ))

        assertEquals(1, result.diagnostics.size)
        assertEquals(SemaSeverity.ERROR, result.diagnostics[0].severity)
        assertTrue(result.diagnostics[0].message.contains("undefined call target 'Foo'"))
        assertTrue(result.diagnostics[0].message.contains("in template 'MyScreen'"))
    }

    @Test
    fun enumCallTargetProducesError() {
        val result = analyzeTemplate(listOf(
            EnumDecl("Color", listOf("red", "blue")),
            TemplateDecl("MyScreen", body = listOf(
                exprCallStmt("Color"),
            )),
        ))

        assertEquals(1, result.diagnostics.size)
        assertTrue(result.diagnostics[0].message.contains("'Color' is an enum"))
    }

    @Test
    fun modifierCallTargetProducesError() {
        val result = analyzeTemplate(listOf(
            ModifierDecl("Padding", listOf(FieldDecl("all", ValueType("double")))),
            TemplateDecl("MyScreen", body = listOf(
                exprCallStmt("Padding"),
            )),
        ))

        assertEquals(1, result.diagnostics.size)
        assertTrue(result.diagnostics[0].message.contains("'Padding' is a modifier"))
    }

    @Test
    fun previewCallTargetProducesError() {
        val result = analyzeTemplate(listOf(
            PreviewDecl("PreviewMain"),
            TemplateDecl("MyScreen", body = listOf(
                exprCallStmt("PreviewMain"),
            )),
        ))

        assertEquals(1, result.diagnostics.size)
        assertTrue(result.diagnostics[0].message.contains("preview 'PreviewMain' cannot be called"))
    }

    @Test
    fun nonReferenceExprCallTargetFallsToExprNode() {
        val result = analyzeTemplate(listOf(
            TemplateDecl("MyScreen", body = listOf(
                ExprStmt(CallExpr(
                    target = MemberExpr(ref("obj"), "method"),
                    args = emptyList(),
                )),
            )),
        ))

        assertTrue(result.diagnostics.isEmpty())
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[0])
        assertIs<IrExprNode>(tmpl.body[0])
    }

    // ============================================================
    // @State
    // ============================================================

    @Test
    fun stateVarDeclProducesStateDecl() {
        val result = analyzeTemplate(listOf(
            TemplateDecl("MyScreen", body = listOf(
                VarDeclStmt(listOf("count"), value = NumberLiteral("0"), annotations = listOf(Annotation("State"))),
            )),
        ))

        assertTrue(result.diagnostics.isEmpty())
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[0])
        val node = assertIs<IrStateDecl>(tmpl.body[0])
        assertEquals("count", node.name)
    }

    @Test
    fun plainVarDeclProducesLocalDecl() {
        val result = analyzeTemplate(listOf(
            TemplateDecl("MyScreen", body = listOf(
                VarDeclStmt(listOf("x"), value = NumberLiteral("1")),
            )),
        ))

        assertTrue(result.diagnostics.isEmpty())
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[0])
        val node = assertIs<IrLocalDecl>(tmpl.body[0])
        assertEquals(listOf("x"), node.names)
    }

    @Test
    fun stateOnCallStmtProducesError() {
        val result = analyzeTemplate(listOf(
            ComponentDecl("Text"),
            TemplateDecl("MyScreen", body = listOf(
                callStmt("Text", annotations = listOf(Annotation("State"))),
            )),
        ))

        assertEquals(1, result.diagnostics.size)
        assertTrue(result.diagnostics[0].message.contains("@State annotation is not valid on call to 'Text'"))
    }

    @Test
    fun stateMultiNameProducesErrorAndFallsBackToLocalDecl() {
        val result = analyzeTemplate(listOf(
            TemplateDecl("MyScreen", body = listOf(
                VarDeclStmt(listOf("a", "b"), value = NumberLiteral("0"), annotations = listOf(Annotation("State"))),
            )),
        ))

        assertEquals(1, result.diagnostics.size)
        assertTrue(result.diagnostics[0].message.contains("@State variable must have exactly one name"))
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[0])
        val node = assertIs<IrLocalDecl>(tmpl.body[0])
        assertEquals(listOf("a", "b"), node.names)
    }

    // ============================================================
    // @Modifier
    // ============================================================

    @Test
    fun modifierAnnotationOnComponentCall() {
        val result = analyzeTemplate(listOf(
            ModifierDecl("Padding", listOf(FieldDecl("all", ValueType("double")))),
            ComponentDecl("Row"),
            TemplateDecl("MyScreen", body = listOf(
                callStmt("Row",
                    trailingLambda = lambda(),
                    annotations = listOf(Annotation("Modifier", listOf(
                        Argument(value = call("Padding", Argument(name = "all", value = NumberLiteral("12.0")))),
                    ))),
                ),
            )),
        ))

        assertTrue(result.diagnostics.isEmpty(), "diagnostics: ${result.diagnostics}")
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[2])
        val row = assertIs<IrComponentCall>(tmpl.body[0])
        assertNotNull(row.modifier)
        assertEquals("Padding", row.modifier!!.name)
        assertEquals(1, row.modifier!!.args.size)
        assertEquals("all", row.modifier!!.args[0].name)
    }

    @Test
    fun modifierWithNoArgsProducesError() {
        val result = analyzeTemplate(listOf(
            ComponentDecl("Row"),
            TemplateDecl("MyScreen", body = listOf(
                callStmt("Row", annotations = listOf(Annotation("Modifier"))),
            )),
        ))

        assertEquals(1, result.diagnostics.size)
        assertTrue(result.diagnostics[0].message.contains("@Modifier annotation requires a modifier constructor call"))
    }

    @Test
    fun modifierWithUndefinedTargetProducesError() {
        val result = analyzeTemplate(listOf(
            ComponentDecl("Row"),
            TemplateDecl("MyScreen", body = listOf(
                callStmt("Row", annotations = listOf(Annotation("Modifier", listOf(
                    Argument(value = call("Unknown")),
                )))),
            )),
        ))

        assertEquals(1, result.diagnostics.size)
        assertTrue(result.diagnostics[0].message.contains("undefined modifier 'Unknown'"))
    }

    @Test
    fun modifierWithNonModifierTypeProducesError() {
        val result = analyzeTemplate(listOf(
            ComponentDecl("Text"),
            ComponentDecl("Row"),
            TemplateDecl("MyScreen", body = listOf(
                callStmt("Row", annotations = listOf(Annotation("Modifier", listOf(
                    Argument(value = call("Text")),
                )))),
            )),
        ))

        assertEquals(1, result.diagnostics.size)
        assertTrue(result.diagnostics[0].message.contains("'Text' is not a modifier type"))
    }

    @Test
    fun modifierOnFunctionCallProducesError() {
        val result = analyzeTemplate(listOf(
            ModifierDecl("Padding", listOf(FieldDecl("all", ValueType("double")))),
            FunctionDecl("Print", listOf(ParameterDecl("msg", ValueType("string")))),
            TemplateDecl("MyScreen", body = listOf(
                callStmt("Print",
                    annotations = listOf(Annotation("Modifier", listOf(
                        Argument(value = call("Padding", Argument(name = "all", value = NumberLiteral("12.0")))),
                    ))),
                ),
            )),
        ))

        assertEquals(1, result.diagnostics.size)
        assertTrue(result.diagnostics[0].message.contains("@Modifier annotation is not valid on function call 'Print'"))
    }

    // ============================================================
    // Control flow
    // ============================================================

    @Test
    fun forInLoopWithComponentBody() {
        val result = analyzeTemplate(listOf(
            ComponentDecl("Text"),
            TemplateDecl("MyScreen", body = listOf(
                ForInStmt(listOf("item"), ref("items"), listOf(
                    exprCallStmt("Text"),
                )),
            )),
        ))

        assertTrue(result.diagnostics.isEmpty())
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[1])
        val forNode = assertIs<IrForNode>(tmpl.body[0])
        assertEquals(listOf("item"), forNode.names)
        assertEquals(1, forNode.body.size)
        assertIs<IrComponentCall>(forNode.body[0])
    }

    @Test
    fun forCondLoop() {
        val result = analyzeTemplate(listOf(
            ComponentDecl("Text"),
            TemplateDecl("MyScreen", body = listOf(
                ForCondStmt(BoolLiteral(true), listOf(
                    exprCallStmt("Text"),
                )),
            )),
        ))

        assertTrue(result.diagnostics.isEmpty())
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[1])
        val forNode = assertIs<IrForCondNode>(tmpl.body[0])
        assertEquals(1, forNode.body.size)
        assertIs<IrComponentCall>(forNode.body[0])
    }

    @Test
    fun ifElseWithComponentBodies() {
        val result = analyzeTemplate(listOf(
            ComponentDecl("Text"),
            ComponentDecl("Button"),
            TemplateDecl("MyScreen", body = listOf(
                IfStmt(
                    fragments = listOf(IfExprFragment(BoolLiteral(true))),
                    body = listOf(exprCallStmt("Text")),
                    elseBody = listOf(exprCallStmt("Button")),
                ),
            )),
        ))

        assertTrue(result.diagnostics.isEmpty())
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[2])
        val ifNode = assertIs<IrIfNode>(tmpl.body[0])
        assertEquals(1, ifNode.body.size)
        assertIs<IrComponentCall>(ifNode.body[0])
        assertNotNull(ifNode.elseBody)
        assertEquals(1, ifNode.elseBody!!.size)
        assertIs<IrComponentCall>(ifNode.elseBody!![0])
    }

    @Test
    fun ifWithElseIfChain() {
        val result = analyzeTemplate(listOf(
            ComponentDecl("A"),
            ComponentDecl("B"),
            ComponentDecl("C"),
            TemplateDecl("MyScreen", body = listOf(
                IfStmt(
                    fragments = listOf(IfExprFragment(BoolLiteral(true))),
                    body = listOf(exprCallStmt("A")),
                    elseIfs = listOf(
                        ElseIf(listOf(IfExprFragment(BoolLiteral(false))), listOf(exprCallStmt("B"))),
                    ),
                    elseBody = listOf(exprCallStmt("C")),
                ),
            )),
        ))

        assertTrue(result.diagnostics.isEmpty())
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[3])
        val ifNode = assertIs<IrIfNode>(tmpl.body[0])
        assertIs<IrComponentCall>(ifNode.body[0])
        assertEquals(1, ifNode.elseIfs.size)
        assertIs<IrComponentCall>(ifNode.elseIfs[0].body[0])
        assertNotNull(ifNode.elseBody)
        assertIs<IrComponentCall>(ifNode.elseBody!![0])
    }

    @Test
    fun switchStatement() {
        val result = analyzeTemplate(listOf(
            ComponentDecl("Text"),
            TemplateDecl("MyScreen", body = listOf(
                SwitchStmt(
                    expr = ref("x"),
                    cases = listOf(
                        SwitchCase(NumberLiteral("1"), listOf(exprCallStmt("Text"))),
                    ),
                    default = listOf(exprCallStmt("Text")),
                ),
            )),
        ))

        assertTrue(result.diagnostics.isEmpty())
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[1])
        val switchNode = assertIs<IrSwitchNode>(tmpl.body[0])
        assertEquals(1, switchNode.cases.size)
        assertIs<IrComponentCall>(switchNode.cases[0].body[0])
        assertNotNull(switchNode.default)
        assertIs<IrComponentCall>(switchNode.default!![0])
    }

    // ============================================================
    // Other statements
    // ============================================================

    @Test
    fun assignmentProducesAssignNode() {
        val result = analyzeTemplate(listOf(
            TemplateDecl("MyScreen", body = listOf(
                AssignStmt("count", AssignOp.ADD_ASSIGN, NumberLiteral("1")),
            )),
        ))

        assertTrue(result.diagnostics.isEmpty())
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[0])
        assertIs<IrAssignNode>(tmpl.body[0])
    }

    @Test
    fun nonCallExprStmtProducesExprNode() {
        val result = analyzeTemplate(listOf(
            TemplateDecl("MyScreen", body = listOf(
                ExprStmt(ref("x")),
            )),
        ))

        assertTrue(result.diagnostics.isEmpty())
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[0])
        assertIs<IrExprNode>(tmpl.body[0])
    }

    @Test
    fun emptyTemplateBody() {
        val result = analyzeTemplate(listOf(
            TemplateDecl("MyScreen"),
        ))

        assertTrue(result.diagnostics.isEmpty())
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[0])
        assertTrue(tmpl.body.isEmpty())
    }

    @Test
    fun returnStatementProducesErrorAndNode() {
        val result = analyzeTemplate(listOf(
            TemplateDecl("MyScreen", body = listOf(
                ReturnStmt(NumberLiteral("1")),
            )),
        ))

        assertEquals(1, result.diagnostics.size)
        assertTrue(result.diagnostics[0].message.contains("return statements are not allowed in template 'MyScreen'"))
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[0])
        val node = assertIs<IrReturnNode>(tmpl.body[0])
        assertNotNull(node.value)
    }

    // ============================================================
    // Edge cases
    // ============================================================

    @Test
    fun templateWithNoParamsAndEmptyBody() {
        val result = analyzeTemplate(listOf(
            TemplateDecl("Empty"),
        ))

        assertTrue(result.diagnostics.isEmpty())
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[0])
        assertTrue(tmpl.body.isEmpty())
    }

    @Test
    fun previewBodyCallingTemplate() {
        val result = analyzeTemplate(listOf(
            TemplateDecl("MainScreen"),
            PreviewDecl("PreviewMain", body = listOf(
                exprCallStmt("MainScreen"),
            )),
        ))

        assertTrue(result.diagnostics.isEmpty())
        val preview = assertIs<IrPreview>(result.ir.declarations[1])
        assertEquals(1, preview.body.size)
        val node = assertIs<IrComponentCall>(preview.body[0])
        assertEquals("MainScreen", node.name)
        assertEquals(SymbolKind.TEMPLATE, node.symbolKind)
    }

    @Test
    fun previewReturnErrorSaysPreview() {
        val result = analyzeTemplate(listOf(
            PreviewDecl("PreviewMain", body = listOf(
                ReturnStmt(),
            )),
        ))

        assertEquals(1, result.diagnostics.size)
        assertTrue(result.diagnostics[0].message.contains("in preview 'PreviewMain'"))
    }

    @Test
    fun functionCallKeepsTrailingLambdaAsRawAst() {
        val theLambda = lambda(
            ExprStmt(ref("x")),
        )
        val result = analyzeTemplate(listOf(
            FunctionDecl("onClick"),
            TemplateDecl("MyScreen", body = listOf(
                exprCallStmt("onClick", trailingLambda = theLambda),
            )),
        ))

        assertTrue(result.diagnostics.isEmpty())
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[1])
        val node = assertIs<IrFunctionCall>(tmpl.body[0])
        assertNotNull(node.trailingLambda)
        assertEquals(theLambda, node.trailingLambda)
    }

    @Test
    fun modifierWithEmptyArgsListProducesError() {
        val result = analyzeTemplate(listOf(
            ComponentDecl("Row"),
            TemplateDecl("MyScreen", body = listOf(
                callStmt("Row", annotations = listOf(Annotation("Modifier", emptyList()))),
            )),
        ))

        assertEquals(1, result.diagnostics.size)
        assertTrue(result.diagnostics[0].message.contains("@Modifier annotation requires a modifier constructor call"))
    }

    @Test
    fun callStmtComponentCallWithoutAnnotations() {
        val result = analyzeTemplate(listOf(
            ComponentDecl("Text"),
            TemplateDecl("MyScreen", body = listOf(
                callStmt("Text", Argument(value = NumberLiteral("1"))),
            )),
        ))

        assertTrue(result.diagnostics.isEmpty())
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[1])
        val node = assertIs<IrComponentCall>(tmpl.body[0])
        assertEquals("Text", node.name)
    }

    // ============================================================
    // Additional coverage (review findings m5-m8, M2)
    // ============================================================

    @Test
    fun modifierWithNonCallExprArgProducesError() {
        val result = analyzeTemplate(listOf(
            ComponentDecl("Row"),
            TemplateDecl("MyScreen", body = listOf(
                callStmt("Row", annotations = listOf(Annotation("Modifier", listOf(
                    Argument(value = ref("someVariable")),
                )))),
            )),
        ))

        assertEquals(1, result.diagnostics.size)
        assertTrue(result.diagnostics[0].message.contains("@Modifier argument must be a modifier constructor call"))
    }

    @Test
    fun modifierWithNonReferenceExprCallTargetProducesError() {
        val result = analyzeTemplate(listOf(
            ComponentDecl("Row"),
            TemplateDecl("MyScreen", body = listOf(
                callStmt("Row", annotations = listOf(Annotation("Modifier", listOf(
                    Argument(value = CallExpr(
                        target = MemberExpr(ref("obj"), "method"),
                        args = emptyList(),
                    )),
                )))),
            )),
        ))

        assertEquals(1, result.diagnostics.size)
        assertTrue(result.diagnostics[0].message.contains("@Modifier argument must be a modifier constructor call"))
    }

    @Test
    fun modifierWithMultipleArgsProducesWarning() {
        val result = analyzeTemplate(listOf(
            ModifierDecl("Padding", listOf(FieldDecl("all", ValueType("double")))),
            ModifierDecl("Margin", listOf(FieldDecl("all", ValueType("double")))),
            ComponentDecl("Row"),
            TemplateDecl("MyScreen", body = listOf(
                callStmt("Row", annotations = listOf(Annotation("Modifier", listOf(
                    Argument(value = call("Padding", Argument(name = "all", value = NumberLiteral("10.0")))),
                    Argument(value = call("Margin", Argument(name = "all", value = NumberLiteral("5.0")))),
                )))),
            )),
        ))

        assertEquals(1, result.diagnostics.size)
        assertEquals(SemaSeverity.WARNING, result.diagnostics[0].severity)
        assertTrue(result.diagnostics[0].message.contains("@Modifier annotation accepts only one argument"))
    }

    @Test
    fun callStmtWithUndefinedTarget() {
        val result = analyzeTemplate(listOf(
            TemplateDecl("MyScreen", body = listOf(
                callStmt("Unknown", annotations = listOf(Annotation("State"))),
            )),
        ))

        assertEquals(2, result.diagnostics.size)
        assertTrue(result.diagnostics.any { it.message.contains("@State annotation is not valid on call") })
        assertTrue(result.diagnostics.any { it.message.contains("undefined call target 'Unknown'") })
    }

    @Test
    fun switchStatementWithNoDefault() {
        val result = analyzeTemplate(listOf(
            ComponentDecl("Text"),
            TemplateDecl("MyScreen", body = listOf(
                SwitchStmt(
                    expr = ref("x"),
                    cases = listOf(
                        SwitchCase(NumberLiteral("1"), listOf(exprCallStmt("Text"))),
                        SwitchCase(NumberLiteral("2"), listOf(exprCallStmt("Text"))),
                    ),
                ),
            )),
        ))

        assertTrue(result.diagnostics.isEmpty())
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[1])
        val switchNode = assertIs<IrSwitchNode>(tmpl.body[0])
        assertEquals(2, switchNode.cases.size)
        assertNull(switchNode.default)
    }

    @Test
    fun returnStatementWithNoValue() {
        val result = analyzeTemplate(listOf(
            TemplateDecl("MyScreen", body = listOf(
                ReturnStmt(),
            )),
        ))

        assertEquals(1, result.diagnostics.size)
        assertTrue(result.diagnostics[0].message.contains("return statements are not allowed in template 'MyScreen'"))
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[0])
        val node = assertIs<IrReturnNode>(tmpl.body[0])
        assertNull(node.value)
    }
}
