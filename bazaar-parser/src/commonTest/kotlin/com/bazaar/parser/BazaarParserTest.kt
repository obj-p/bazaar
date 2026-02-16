package com.bazaar.parser

import com.bazaar.parser.ast.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class BazaarParserTest {

    private fun parse(source: String): BazaarFile = BazaarParser.parse(source)

    // ── File structure ──────────────────────────────────────────

    @Test
    fun emptyFile() {
        val file = parse("")
        assertNull(file.packageDecl)
        assertEquals(emptyList(), file.imports)
        assertEquals(emptyList(), file.declarations)
    }

    @Test
    fun packageDeclaration() {
        val file = parse("package com.example.app")
        assertEquals(listOf("com", "example", "app"), file.packageDecl?.segments)
    }

    @Test
    fun importDeclarations() {
        val file = parse("""
            import com.example.Foo
            import com.example.Bar as Baz
        """.trimIndent())
        assertEquals(2, file.imports.size)
        assertEquals(listOf("com", "example", "Foo"), file.imports[0].segments)
        assertNull(file.imports[0].alias)
        assertEquals(listOf("com", "example", "Bar"), file.imports[1].segments)
        assertEquals("Baz", file.imports[1].alias)
    }

    // ── Declarations ────────────────────────────────────────────

    @Test
    fun enumDecl() {
        val file = parse("enum Color { Red, Green, Blue }")
        val decl = assertIs<EnumDecl>(file.declarations.single())
        assertEquals("Color", decl.name)
        assertEquals(listOf("Red", "Green", "Blue"), decl.values)
    }

    @Test
    fun componentDecl() {
        val file = parse("""
            component Button {
                label string
                onClick func() = null
            }
        """.trimIndent())
        val decl = assertIs<ComponentDecl>(file.declarations.single())
        assertEquals("Button", decl.name)
        assertEquals(2, decl.members.size)
        val field0 = assertIs<FieldDecl>(decl.members[0])
        assertEquals("label", field0.name)
        assertIs<ValueType>(field0.type)
        assertEquals("string", (field0.type as ValueType).name)
    }

    @Test
    fun dataDecl() {
        val file = parse("data Point { x int  y int }")
        val decl = assertIs<DataDecl>(file.declarations.single())
        assertEquals("Point", decl.name)
        assertEquals(2, decl.members.size)
    }

    @Test
    fun modifierDecl() {
        val file = parse("modifier Padded { amount int = 8 }")
        val decl = assertIs<ModifierDecl>(file.declarations.single())
        assertEquals("Padded", decl.name)
        val field = assertIs<FieldDecl>(decl.members.single())
        assertEquals("amount", field.name)
        assertEquals(NumberLiteral("8"), field.default)
    }

    @Test
    fun functionDecl() {
        val file = parse("func add(a int, b int) -> int { return a + b }")
        val decl = assertIs<FunctionDecl>(file.declarations.single())
        assertEquals("add", decl.name)
        assertEquals(2, decl.params.size)
        assertEquals("a", decl.params[0].name)
        assertIs<ValueType>(decl.returnType)
        assertEquals(1, decl.body?.size)
    }

    @Test
    fun templateDecl() {
        val file = parse("template Greeting(name string) { }")
        val decl = assertIs<TemplateDecl>(file.declarations.single())
        assertEquals("Greeting", decl.name)
        assertEquals(1, decl.params.size)
    }

    @Test
    fun previewDecl() {
        val file = parse("preview MyPreview { }")
        val decl = assertIs<PreviewDecl>(file.declarations.single())
        assertEquals("MyPreview", decl.name)
    }

    @Test
    fun constructorDecl() {
        val file = parse("""
            component Button {
                label string
                constructor(text string) = Button(label = text)
            }
        """.trimIndent())
        val comp = assertIs<ComponentDecl>(file.declarations.single())
        val ctor = assertIs<ConstructorDecl>(comp.members[1])
        assertEquals(1, ctor.params.size)
        assertIs<CallExpr>(ctor.value)
    }

    // ── Types ───────────────────────────────────────────────────

    @Test
    fun valueType() {
        val file = parse("component C { x int }")
        val field = assertIs<FieldDecl>(assertIs<ComponentDecl>(file.declarations.single()).members.single())
        val type = assertIs<ValueType>(field.type)
        assertEquals("int", type.name)
        assertEquals(false, type.nullable)
    }

    @Test
    fun nullableType() {
        val file = parse("component C { x int? }")
        val field = assertIs<FieldDecl>(assertIs<ComponentDecl>(file.declarations.single()).members.single())
        val type = assertIs<ValueType>(field.type)
        assertEquals(true, type.nullable)
    }

    @Test
    fun arrayType() {
        val file = parse("component C { x [int] }")
        val field = assertIs<FieldDecl>(assertIs<ComponentDecl>(file.declarations.single()).members.single())
        val type = assertIs<ArrayType>(field.type)
        assertIs<ValueType>(type.elementType)
    }

    @Test
    fun mapType() {
        val file = parse("component C { x {string: int} }")
        val field = assertIs<FieldDecl>(assertIs<ComponentDecl>(file.declarations.single()).members.single())
        val type = assertIs<MapType>(field.type)
        assertEquals("string", (type.keyType as ValueType).name)
        assertEquals("int", (type.valueType as ValueType).name)
    }

    @Test
    fun functionType() {
        val file = parse("component C { x func(int, string) -> bool }")
        val field = assertIs<FieldDecl>(assertIs<ComponentDecl>(file.declarations.single()).members.single())
        val type = assertIs<FunctionType>(field.type)
        assertEquals(2, type.paramTypes.size)
        assertEquals("bool", (type.returnType as ValueType).name)
    }

    // ── Expressions ─────────────────────────────────────────────

    @Test
    fun literals() {
        val file = parse("component C { a int = 42  b bool = true  c bool = false  d string? = null }")
        val comp = assertIs<ComponentDecl>(file.declarations.single())
        assertEquals(NumberLiteral("42"), (comp.members[0] as FieldDecl).default)
        assertEquals(BoolLiteral(true), (comp.members[1] as FieldDecl).default)
        assertEquals(BoolLiteral(false), (comp.members[2] as FieldDecl).default)
        assertEquals(NullLiteral, (comp.members[3] as FieldDecl).default)
    }

    @Test
    fun binaryOps() {
        val file = parse("func f() { return 1 + 2 * 3 }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val ret = assertIs<ReturnStmt>(fn.body!!.single())
        val add = assertIs<BinaryExpr>(ret.value)
        assertEquals(BinaryOp.ADD, add.op)
        val mul = assertIs<BinaryExpr>(add.right)
        assertEquals(BinaryOp.MUL, mul.op)
    }

    @Test
    fun unaryOps() {
        val file = parse("func f() { return !true }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val ret = assertIs<ReturnStmt>(fn.body!!.single())
        val unary = assertIs<UnaryExpr>(ret.value)
        assertEquals(UnaryOp.NOT, unary.op)
    }

    @Test
    fun memberAccess() {
        val file = parse("func f() { return a.b.c }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val ret = assertIs<ReturnStmt>(fn.body!!.single())
        val outer = assertIs<MemberExpr>(ret.value)
        assertEquals("c", outer.member)
        assertEquals(false, outer.optional)
        val inner = assertIs<MemberExpr>(outer.target)
        assertEquals("b", inner.member)
    }

    @Test
    fun optionalChaining() {
        val file = parse("func f() { return a?.b }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val ret = assertIs<ReturnStmt>(fn.body!!.single())
        val expr = assertIs<MemberExpr>(ret.value)
        assertEquals(true, expr.optional)
    }

    @Test
    fun callExpr() {
        val file = parse("func f() { foo(1, name = 2) }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val exprStmt = assertIs<ExprStmt>(fn.body!!.single())
        val call = assertIs<CallExpr>(exprStmt.expr)
        assertEquals(2, call.args.size)
        assertNull(call.args[0].name)
        assertEquals("name", call.args[1].name)
    }

    @Test
    fun callWithTrailingLambda() {
        val file = parse("func f() { foo(1) { return 2 } }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val exprStmt = assertIs<ExprStmt>(fn.body!!.single())
        val call = assertIs<CallExpr>(exprStmt.expr)
        assertEquals(1, call.args.size)
        val lambda = call.trailingLambda!!
        assertEquals(1, lambda.body.size)
    }

    @Test
    fun trailingLambdaWithoutParens() {
        val file = parse("func f() { foo { return 1 } }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val exprStmt = assertIs<ExprStmt>(fn.body!!.single())
        val call = assertIs<CallExpr>(exprStmt.expr)
        assertEquals(0, call.args.size)
        val lambda = call.trailingLambda!!
        assertEquals(1, lambda.body.size)
    }

    @Test
    fun indexExpr() {
        val file = parse("func f() { return a[0] }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val ret = assertIs<ReturnStmt>(fn.body!!.single())
        val index = assertIs<IndexExpr>(ret.value)
        assertEquals(false, index.optional)
    }

    @Test
    fun lambdaWithParams() {
        val file = parse("component C { x func() = { (a int, b) in return a } }")
        val comp = assertIs<ComponentDecl>(file.declarations.single())
        val field = assertIs<FieldDecl>(comp.members.single())
        val lambda = assertIs<LambdaExpr>(field.default)
        assertEquals(2, lambda.params.size)
        assertEquals("a", lambda.params[0].name)
        assertIs<ValueType>(lambda.params[0].type)
        assertEquals("b", lambda.params[1].name)
        assertNull(lambda.params[1].type)
    }

    @Test
    fun arrayLiteral() {
        val file = parse("func f() { return [1, 2, 3] }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val ret = assertIs<ReturnStmt>(fn.body!!.single())
        val arr = assertIs<ArrayLiteral>(ret.value)
        assertEquals(3, arr.elements.size)
    }

    @Test
    fun mapLiteral() {
        val file = parse("""func f() { return {"a": 1, "b": 2} }""")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val ret = assertIs<ReturnStmt>(fn.body!!.single())
        val map = assertIs<MapLiteral>(ret.value)
        assertEquals(2, map.entries.size)
    }

    @Test
    fun emptyMapLiteral() {
        val file = parse("func f() { return {:} }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val ret = assertIs<ReturnStmt>(fn.body!!.single())
        val map = assertIs<MapLiteral>(ret.value)
        assertEquals(0, map.entries.size)
    }

    @Test
    fun stringLiteral() {
        val file = parse("""func f() { return "hello" }""")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val ret = assertIs<ReturnStmt>(fn.body!!.single())
        val str = assertIs<StringLiteral>(ret.value)
        assertEquals(1, str.parts.size)
        assertEquals("hello", (str.parts[0] as TextPart).text)
    }

    @Test
    fun stringInterpolation() {
        val file = parse("""func f() { return "hello ${'$'}{name}" }""")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val ret = assertIs<ReturnStmt>(fn.body!!.single())
        val str = assertIs<StringLiteral>(ret.value)
        assertEquals(2, str.parts.size)
        assertIs<TextPart>(str.parts[0])
        val interp = assertIs<InterpolationPart>(str.parts[1])
        assertIs<ReferenceExpr>(interp.expr)
    }

    @Test
    fun coalesceExpr() {
        val file = parse("func f() { return a ?? b }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val ret = assertIs<ReturnStmt>(fn.body!!.single())
        val bin = assertIs<BinaryExpr>(ret.value)
        assertEquals(BinaryOp.COALESCE, bin.op)
    }

    // ── Statements ──────────────────────────────────────────────

    @Test
    fun varDecl() {
        val file = parse("func f() { var x int = 1 }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val stmt = assertIs<VarDeclStmt>(fn.body!!.single())
        assertEquals(listOf("x"), stmt.names)
        assertIs<ValueType>(stmt.type)
        assertEquals(NumberLiteral("1"), stmt.value)
    }

    @Test
    fun varDeclWithDestructuring() {
        val file = parse("func f() { var (a, b) = pair }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val stmt = assertIs<VarDeclStmt>(fn.body!!.single())
        assertEquals(listOf("a", "b"), stmt.names)
    }

    @Test
    fun assignStmt() {
        val file = parse("func f() { x += 1 }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val stmt = assertIs<AssignStmt>(fn.body!!.single())
        assertEquals("x", stmt.target)
        assertEquals(AssignOp.ADD_ASSIGN, stmt.op)
    }

    @Test
    fun returnStmt() {
        val file = parse("func f() { return }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val stmt = assertIs<ReturnStmt>(fn.body!!.single())
        assertNull(stmt.value)
    }

    @Test
    fun annotatedVarStmt() {
        val file = parse("func f() { @State var x int = 0 }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val stmt = assertIs<VarDeclStmt>(fn.body!!.single())
        assertEquals(1, stmt.annotations.size)
        assertEquals("State", stmt.annotations[0].name)
    }

    @Test
    fun annotatedCallStmt() {
        val file = parse("func f() { @Foo Bar(1) { } }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val stmt = assertIs<CallStmt>(fn.body!!.single())
        assertEquals(1, stmt.annotations.size)
        assertEquals("Foo", stmt.annotations[0].name)
        assertEquals("Bar", (stmt.expr.target as ReferenceExpr).name)
    }

    // ── Control flow ────────────────────────────────────────────

    @Test
    fun ifStmt() {
        val file = parse("func f() { if x { return 1 } }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val stmt = assertIs<IfStmt>(fn.body!!.single())
        assertEquals(1, stmt.fragments.size)
        assertIs<IfExprFragment>(stmt.fragments[0])
        assertEquals(1, stmt.body.size)
        assertEquals(0, stmt.elseIfs.size)
        assertNull(stmt.elseBody)
    }

    @Test
    fun ifElseStmt() {
        val file = parse("func f() { if x { return 1 } else { return 2 } }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val stmt = assertIs<IfStmt>(fn.body!!.single())
        assertEquals(0, stmt.elseIfs.size)
        assertEquals(1, stmt.elseBody?.size)
    }

    @Test
    fun ifElseIfElse() {
        val file = parse("func f() { if a { return 1 } else if b { return 2 } else if c { return 3 } else { return 4 } }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val stmt = assertIs<IfStmt>(fn.body!!.single())
        assertEquals(2, stmt.elseIfs.size)
        assertEquals(1, stmt.elseBody?.size)
    }

    @Test
    fun ifWithVarFragment() {
        val file = parse("func f() { if var x = getValue() { return x } }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val stmt = assertIs<IfStmt>(fn.body!!.single())
        val frag = assertIs<IfVarFragment>(stmt.fragments.single())
        assertEquals(listOf("x"), frag.names)
    }

    @Test
    fun forInStmt() {
        val file = parse("func f() { for item in list { return item } }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val stmt = assertIs<ForInStmt>(fn.body!!.single())
        assertEquals(listOf("item"), stmt.names)
        assertEquals(1, stmt.body.size)
    }

    @Test
    fun forInWithDestructuring() {
        val file = parse("func f() { for (k, v) in map { return k } }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val stmt = assertIs<ForInStmt>(fn.body!!.single())
        assertEquals(listOf("k", "v"), stmt.names)
    }

    @Test
    fun forCondStmt() {
        val file = parse("func f() { for running { x += 1 } }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val stmt = assertIs<ForCondStmt>(fn.body!!.single())
        assertIs<ReferenceExpr>(stmt.condition)
    }

    @Test
    fun switchStmt() {
        val file = parse("""
            func f() {
                switch x {
                    case 1: return "one"
                    case 2: return "two"
                    default: return "other"
                }
            }
        """.trimIndent())
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val stmt = assertIs<SwitchStmt>(fn.body!!.single())
        assertEquals(2, stmt.cases.size)
        assertEquals(1, stmt.default?.size)
    }

    // ── Integration ─────────────────────────────────────────────

    @Test
    fun fullComponent() {
        val file = parse("""
            package com.example

            import com.example.Theme

            component Counter {
                count int = 0
                label string = "Count"

                constructor(initial int) = Counter(count = initial)
            }
        """.trimIndent())
        assertEquals(listOf("com", "example"), file.packageDecl?.segments)
        assertEquals(1, file.imports.size)
        val comp = assertIs<ComponentDecl>(file.declarations.single())
        assertEquals("Counter", comp.name)
        assertEquals(3, comp.members.size)
    }

    @Test
    fun templateWithAnnotatedCalls() {
        val file = parse("""
            template MyScreen() {
                @State var count int = 0
                @Composable Column {
                    @Composable Text("hello")
                }
            }
        """.trimIndent())
        val tmpl = assertIs<TemplateDecl>(file.declarations.single())
        assertEquals(2, tmpl.body.size)
        assertIs<VarDeclStmt>(tmpl.body[0])
        assertIs<CallStmt>(tmpl.body[1])
    }

    @Test
    fun optionalCallExpr() {
        val file = parse("func f() { return a?(1) }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val ret = assertIs<ReturnStmt>(fn.body!!.single())
        val call = assertIs<CallExpr>(ret.value)
        assertEquals(true, call.optional)
    }

    @Test
    fun optionalIndexExpr() {
        val file = parse("func f() { return a?[0] }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val ret = assertIs<ReturnStmt>(fn.body!!.single())
        val index = assertIs<IndexExpr>(ret.value)
        assertEquals(true, index.optional)
    }

    @Test
    fun powerExpr() {
        val file = parse("func f() { return 2 ** 3 }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val ret = assertIs<ReturnStmt>(fn.body!!.single())
        val bin = assertIs<BinaryExpr>(ret.value)
        assertEquals(BinaryOp.POW, bin.op)
    }

    @Test
    fun comparisonExprs() {
        val file = parse("func f() { return a < b }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val ret = assertIs<ReturnStmt>(fn.body!!.single())
        val bin = assertIs<BinaryExpr>(ret.value)
        assertEquals(BinaryOp.LT, bin.op)
    }

    @Test
    fun equalityExprs() {
        val file = parse("func f() { return a == b }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val ret = assertIs<ReturnStmt>(fn.body!!.single())
        val bin = assertIs<BinaryExpr>(ret.value)
        assertEquals(BinaryOp.EQ, bin.op)
    }

    @Test
    fun logicalExprs() {
        val file = parse("func f() { return a && b || c }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val ret = assertIs<ReturnStmt>(fn.body!!.single())
        val or = assertIs<BinaryExpr>(ret.value)
        assertEquals(BinaryOp.OR, or.op)
        val and = assertIs<BinaryExpr>(or.left)
        assertEquals(BinaryOp.AND, and.op)
    }

    @Test
    fun annotationWithArgs() {
        val file = parse("func f() { @Bind(source = x) var y int = 0 }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val stmt = assertIs<VarDeclStmt>(fn.body!!.single())
        val ann = stmt.annotations.single()
        assertEquals("Bind", ann.name)
        assertEquals(1, ann.args?.size)
        assertEquals("source", ann.args!![0].name)
    }

    @Test
    fun componentTypeField() {
        val file = parse("component C { child component }")
        val comp = assertIs<ComponentDecl>(file.declarations.single())
        val field = assertIs<FieldDecl>(comp.members.single())
        val type = assertIs<ValueType>(field.type)
        assertEquals("component", type.name)
    }

    @Test
    fun lambdaWithReturnType() {
        val file = parse("component C { f func() = { (x int) -> bool in return true } }")
        val comp = assertIs<ComponentDecl>(file.declarations.single())
        val field = assertIs<FieldDecl>(comp.members.single())
        val lambda = assertIs<LambdaExpr>(field.default)
        assertEquals(1, lambda.params.size)
        val returnType = assertIs<ValueType>(lambda.returnType)
        assertEquals("bool", returnType.name)
    }

    @Test
    fun negateExpr() {
        val file = parse("func f() { return -x }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val ret = assertIs<ReturnStmt>(fn.body!!.single())
        val unary = assertIs<UnaryExpr>(ret.value)
        assertEquals(UnaryOp.NEGATE, unary.op)
    }

    @Test
    fun allAssignOps() {
        val ops = listOf("=" to AssignOp.ASSIGN, "+=" to AssignOp.ADD_ASSIGN, "-=" to AssignOp.SUB_ASSIGN,
            "*=" to AssignOp.MUL_ASSIGN, "/=" to AssignOp.DIV_ASSIGN, "%=" to AssignOp.MOD_ASSIGN)
        for ((symbol, expected) in ops) {
            val file = parse("func f() { x $symbol 1 }")
            val fn = assertIs<FunctionDecl>(file.declarations.single())
            val stmt = assertIs<AssignStmt>(fn.body!!.single())
            assertEquals(expected, stmt.op, "Failed for $symbol")
        }
    }

    @Test
    fun ifWithMultipleFragments() {
        val file = parse("func f() { if var x = a(), x > 0 { return x } }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val stmt = assertIs<IfStmt>(fn.body!!.single())
        assertEquals(2, stmt.fragments.size)
        assertIs<IfVarFragment>(stmt.fragments[0])
        assertIs<IfExprFragment>(stmt.fragments[1])
    }

    @Test
    fun keywordAsIdentifier() {
        val file = parse("component C { null int = 0 }")
        val comp = assertIs<ComponentDecl>(file.declarations.single())
        val field = assertIs<FieldDecl>(comp.members.single())
        assertEquals("null", field.name)
    }

    // ── Additional coverage ────────────────────────────────────

    @Test
    fun templateWithoutParens() {
        val file = parse("template Foo { }")
        val decl = assertIs<TemplateDecl>(file.declarations.single())
        assertEquals("Foo", decl.name)
        assertEquals(emptyList(), decl.params)
        assertEquals(emptyList(), decl.body)
    }

    @Test
    fun functionWithoutBody() {
        val file = parse("func f()")
        val decl = assertIs<FunctionDecl>(file.declarations.single())
        assertEquals("f", decl.name)
        assertNull(decl.body)
    }

    @Test
    fun emptyArrayLiteral() {
        val file = parse("func f() { return [] }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val ret = assertIs<ReturnStmt>(fn.body!!.single())
        val arr = assertIs<ArrayLiteral>(ret.value)
        assertEquals(0, arr.elements.size)
    }

    @Test
    fun annotatedCallWithLambdaOnly() {
        val file = parse("func f() { @Foo Bar { } }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val stmt = assertIs<CallStmt>(fn.body!!.single())
        assertEquals("Foo", stmt.annotations.single().name)
        val call = stmt.expr
        assertEquals("Bar", (call.target as ReferenceExpr).name)
        assertEquals(emptyList(), call.args)
        assertEquals(emptyList(), call.trailingLambda!!.body)
    }

    @Test
    fun parenthesizedType() {
        val file = parse("component C { x (int) }")
        val field = assertIs<FieldDecl>(assertIs<ComponentDecl>(file.declarations.single()).members.single())
        val type = assertIs<ValueType>(field.type)
        assertEquals("int", type.name)
        assertEquals(false, type.nullable)
    }

    @Test
    fun nullableParenthesizedValueType() {
        val file = parse("component C { x (int)? }")
        val field = assertIs<FieldDecl>(assertIs<ComponentDecl>(file.declarations.single()).members.single())
        val type = assertIs<ValueType>(field.type)
        assertEquals("int", type.name)
        assertEquals(true, type.nullable)
    }

    @Test
    fun nullableParenthesizedFuncType() {
        val file = parse("component C { x (func(int) -> bool)? }")
        val field = assertIs<FieldDecl>(assertIs<ComponentDecl>(file.declarations.single()).members.single())
        val type = assertIs<FunctionType>(field.type)
        assertEquals(true, type.nullable)
        assertEquals(1, type.paramTypes.size)
        assertEquals("bool", (type.returnType as ValueType).name)
    }

    @Test
    fun nullableParenthesizedArrayType() {
        val file = parse("component C { x ([int])? }")
        val field = assertIs<FieldDecl>(assertIs<ComponentDecl>(file.declarations.single()).members.single())
        val type = assertIs<ArrayType>(field.type)
        assertEquals(true, type.nullable)
        assertEquals("int", (type.elementType as ValueType).name)
    }

    @Test
    fun nullableParenthesizedMapType() {
        val file = parse("component C { x ({string: int})? }")
        val field = assertIs<FieldDecl>(assertIs<ComponentDecl>(file.declarations.single()).members.single())
        val type = assertIs<MapType>(field.type)
        assertEquals(true, type.nullable)
        assertEquals("string", (type.keyType as ValueType).name)
        assertEquals("int", (type.valueType as ValueType).name)
    }

    @Test
    fun nullableArrayType() {
        val file = parse("component C { x [int]? }")
        val field = assertIs<FieldDecl>(assertIs<ComponentDecl>(file.declarations.single()).members.single())
        val type = assertIs<ArrayType>(field.type)
        assertEquals(true, type.nullable)
        assertEquals("int", (type.elementType as ValueType).name)
    }

    @Test
    fun nullableMapType() {
        val file = parse("component C { x {string: int}? }")
        val field = assertIs<FieldDecl>(assertIs<ComponentDecl>(file.declarations.single()).members.single())
        val type = assertIs<MapType>(field.type)
        assertEquals(true, type.nullable)
        assertEquals("string", (type.keyType as ValueType).name)
        assertEquals("int", (type.valueType as ValueType).name)
    }

    @Test
    fun functionTypeWithoutReturnType() {
        val file = parse("component C { x func(int) }")
        val field = assertIs<FieldDecl>(assertIs<ComponentDecl>(file.declarations.single()).members.single())
        val type = assertIs<FunctionType>(field.type)
        assertEquals(1, type.paramTypes.size)
        assertNull(type.returnType)
    }

    @Test
    fun ifVarWithTypeAnnotation() {
        val file = parse("func f() { if var x int = expr { } }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val stmt = assertIs<IfStmt>(fn.body!!.single())
        val frag = assertIs<IfVarFragment>(stmt.fragments.single())
        assertEquals(listOf("x"), frag.names)
        val type = assertIs<ValueType>(frag.type)
        assertEquals("int", type.name)
    }

    @Test
    fun switchWithoutDefault() {
        val file = parse("""
            func f() {
                switch x {
                    case 1: return "one"
                    case 2: return "two"
                }
            }
        """.trimIndent())
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val stmt = assertIs<SwitchStmt>(fn.body!!.single())
        assertEquals(2, stmt.cases.size)
        assertNull(stmt.default)
    }

    @Test
    fun chainedOptionalExpressions() {
        val file = parse("func f() { return a?.b?(1)?[0] }")
        val fn = assertIs<FunctionDecl>(file.declarations.single())
        val ret = assertIs<ReturnStmt>(fn.body!!.single())
        // Outermost: a?.b?(1)?[0]  →  optionalIndex
        val optIndex = assertIs<IndexExpr>(ret.value)
        assertEquals(true, optIndex.optional)
        // Next: a?.b?(1)  →  optionalCall
        val optCall = assertIs<CallExpr>(optIndex.target)
        assertEquals(true, optCall.optional)
        // Next: a?.b  →  optionalMember
        val optMember = assertIs<MemberExpr>(optCall.target)
        assertEquals(true, optMember.optional)
        assertEquals("b", optMember.member)
        assertEquals("a", (optMember.target as ReferenceExpr).name)
    }
}
