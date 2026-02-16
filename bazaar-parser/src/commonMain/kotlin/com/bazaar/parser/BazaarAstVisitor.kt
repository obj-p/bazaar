package com.bazaar.parser

import com.bazaar.parser.antlr.BazaarParser as AntlrParser
import com.bazaar.parser.antlr.BazaarParserBaseVisitor
import com.bazaar.parser.ast.*

internal class BazaarAstVisitor : BazaarParserBaseVisitor<Any>() {

    override fun defaultResult(): Any = error("Unexpected parse tree node")

    // ── File structure ──────────────────────────────────────────

    override fun visitBazaarFile(ctx: AntlrParser.BazaarFileContext): BazaarFile {
        val pkg = ctx.packageDecl()?.let { visitPackageDecl(it) }
        val imports = ctx.importDecl().map { visitImportDecl(it) }
        val decls = ctx.topLevelDecl().map { visitTopLevelDecl(it) }
        return BazaarFile(pkg, imports, decls)
    }

    override fun visitPackageDecl(ctx: AntlrParser.PackageDeclContext): PackageDecl {
        return PackageDecl(visitQualifiedName(ctx.qualifiedName()))
    }

    override fun visitImportDecl(ctx: AntlrParser.ImportDeclContext): ImportDecl {
        val segments = visitQualifiedName(ctx.qualifiedName())
        val alias = ctx.IDENTIFIER()?.text
        return ImportDecl(segments, alias)
    }

    override fun visitQualifiedName(ctx: AntlrParser.QualifiedNameContext): List<String> {
        return ctx.IDENTIFIER().map { it.text }
    }

    // ── Declarations ────────────────────────────────────────────

    override fun visitTopLevelDecl(ctx: AntlrParser.TopLevelDeclContext): Decl {
        ctx.enumDecl()?.let { return visitEnumDecl(it) }
        ctx.componentDecl()?.let { return visitComponentDecl(it) }
        ctx.dataDecl()?.let { return visitDataDecl(it) }
        ctx.modifierDecl()?.let { return visitModifierDecl(it) }
        ctx.functionDecl()?.let { return visitFunctionDecl(it) }
        ctx.templateDecl()?.let { return visitTemplateDecl(it) }
        ctx.previewDecl()?.let { return visitPreviewDecl(it) }
        error("Unknown top-level declaration")
    }

    override fun visitEnumDecl(ctx: AntlrParser.EnumDeclContext): EnumDecl {
        // First IDENTIFIER is the enum name, rest are values
        val identifiers = ctx.IDENTIFIER()
        val name = identifiers[0].text
        val values = identifiers.drop(1).map { it.text }
        return EnumDecl(name, values)
    }

    override fun visitComponentDecl(ctx: AntlrParser.ComponentDeclContext): ComponentDecl {
        val name = ctx.IDENTIFIER().text
        val members = ctx.memberDecl().map { visitMemberDecl(it) }
        return ComponentDecl(name, members)
    }

    override fun visitDataDecl(ctx: AntlrParser.DataDeclContext): DataDecl {
        val name = ctx.IDENTIFIER().text
        val members = ctx.memberDecl().map { visitMemberDecl(it) }
        return DataDecl(name, members)
    }

    override fun visitModifierDecl(ctx: AntlrParser.ModifierDeclContext): ModifierDecl {
        val name = ctx.IDENTIFIER().text
        val members = ctx.memberDecl().map { visitMemberDecl(it) }
        return ModifierDecl(name, members)
    }

    override fun visitFunctionDecl(ctx: AntlrParser.FunctionDeclContext): FunctionDecl {
        val name = ctx.IDENTIFIER().text
        val params = visitParamList(ctx.parameterList())
        val returnType = ctx.typeDecl()?.let { visitTypeDecl(it) }
        val body = ctx.block()?.let { visitBlockToStmts(it) }
        return FunctionDecl(name, params, returnType, body)
    }

    override fun visitTemplateDecl(ctx: AntlrParser.TemplateDeclContext): TemplateDecl {
        val name = ctx.IDENTIFIER().text
        val params = visitParamList(ctx.parameterList())
        val body = visitBlockToStmts(ctx.block())
        return TemplateDecl(name, params, body)
    }

    override fun visitPreviewDecl(ctx: AntlrParser.PreviewDeclContext): PreviewDecl {
        val name = ctx.IDENTIFIER().text
        val body = visitBlockToStmts(ctx.block())
        return PreviewDecl(name, body)
    }

    // ── Members / params ────────────────────────────────────────

    override fun visitMemberDecl(ctx: AntlrParser.MemberDeclContext): MemberDecl {
        ctx.constructorDecl()?.let { return visitConstructorDecl(it) }
        ctx.fieldDecl()?.let { return visitFieldDecl(it) }
        error("Unknown member declaration")
    }

    override fun visitFieldDecl(ctx: AntlrParser.FieldDeclContext): FieldDecl {
        val name = ident(ctx.identOrKeyword())
        val type = visitTypeDecl(ctx.typeDecl())
        val default = ctx.expr()?.let { visit(it) as Expr }
        return FieldDecl(name, type, default)
    }

    override fun visitConstructorDecl(ctx: AntlrParser.ConstructorDeclContext): ConstructorDecl {
        val params = visitParamList(ctx.parameterList())
        val value = visit(ctx.expr()) as Expr
        return ConstructorDecl(params, value)
    }

    override fun visitParameterDecl(ctx: AntlrParser.ParameterDeclContext): ParameterDecl {
        val name = ident(ctx.identOrKeyword())
        val type = visitTypeDecl(ctx.typeDecl())
        val default = ctx.expr()?.let { visit(it) as Expr }
        return ParameterDecl(name, type, default)
    }

    // ── Types ───────────────────────────────────────────────────

    override fun visitTypeDecl(ctx: AntlrParser.TypeDeclContext): TypeDecl {
        val nullable = ctx.QUESTION() != null
        if (ctx.FUNC() != null) {
            val paramTypes = ctx.typeList()?.typeDecl()?.map { visitTypeDecl(it) } ?: emptyList()
            val returnType = if (ctx.ARROW() != null) {
                // The return type is the direct typeDecl child (not inside typeList)
                val allTypes = ctx.typeDecl()
                if (allTypes.isNotEmpty()) visitTypeDecl(allTypes.last()!!) else null
            } else null
            return FunctionType(paramTypes, returnType, nullable)
        }
        if (ctx.LBRACK() != null) {
            val elementType = visitTypeDecl(ctx.typeDecl(0)!!)
            return ArrayType(elementType, nullable)
        }
        if (ctx.LBRACE() != null) {
            val keyType = visitTypeDecl(ctx.typeDecl(0)!!)
            val valueType = visitTypeDecl(ctx.typeDecl(1)!!)
            return MapType(keyType, valueType, nullable)
        }
        if (ctx.LPAREN() != null) {
            val inner = visitTypeDecl(ctx.typeDecl(0)!!)
            if (!nullable) return inner
            return when (inner) {
                is ValueType -> inner.copy(nullable = true)
                is FunctionType -> inner.copy(nullable = true)
                is ArrayType -> inner.copy(nullable = true)
                is MapType -> inner.copy(nullable = true)
            }
        }
        if (ctx.COMPONENT() != null) {
            return ValueType("component", nullable)
        }
        // IDENTIFIER
        return ValueType(ctx.IDENTIFIER()!!.text, nullable)
    }

    // ── Expressions ─────────────────────────────────────────────

    override fun visitMemberExpr(ctx: AntlrParser.MemberExprContext): MemberExpr {
        val target = visit(ctx.expr()) as Expr
        val member = ident(ctx.identOrKeyword())
        return MemberExpr(target, member)
    }

    override fun visitOptionalMemberExpr(ctx: AntlrParser.OptionalMemberExprContext): MemberExpr {
        val target = visit(ctx.expr()) as Expr
        val member = ident(ctx.identOrKeyword())
        return MemberExpr(target, member, optional = true)
    }

    override fun visitCallExpr(ctx: AntlrParser.CallExprContext): CallExpr {
        val target = visit(ctx.expr()) as Expr
        val args = visitArgListToArgs(ctx.argList())
        val lambda = ctx.lambda()?.let { visitLambda(it) }
        return CallExpr(target, args, lambda)
    }

    override fun visitOptionalCallExpr(ctx: AntlrParser.OptionalCallExprContext): CallExpr {
        val target = visit(ctx.expr()) as Expr
        val args = visitArgListToArgs(ctx.argList())
        return CallExpr(target, args, optional = true)
    }

    override fun visitIndexExpr(ctx: AntlrParser.IndexExprContext): IndexExpr {
        val target = visit(ctx.expr(0)!!) as Expr
        val index = visit(ctx.expr(1)!!) as Expr
        return IndexExpr(target, index)
    }

    override fun visitOptionalIndexExpr(ctx: AntlrParser.OptionalIndexExprContext): IndexExpr {
        val target = visit(ctx.expr(0)!!) as Expr
        val index = visit(ctx.expr(1)!!) as Expr
        return IndexExpr(target, index, optional = true)
    }

    override fun visitTrailingLambdaExpr(ctx: AntlrParser.TrailingLambdaExprContext): CallExpr {
        val target = visit(ctx.expr()) as Expr
        val lambda = visitLambda(ctx.lambda())
        return CallExpr(target, emptyList(), lambda)
    }

    override fun visitUnaryExpr(ctx: AntlrParser.UnaryExprContext): UnaryExpr {
        val op = if (ctx.BANG() != null) UnaryOp.NOT else UnaryOp.NEGATE
        val operand = visit(ctx.expr()) as Expr
        return UnaryExpr(op, operand)
    }

    override fun visitPowerExpr(ctx: AntlrParser.PowerExprContext): BinaryExpr {
        val left = visit(ctx.expr(0)!!) as Expr
        val right = visit(ctx.expr(1)!!) as Expr
        return BinaryExpr(left, BinaryOp.POW, right)
    }

    override fun visitMulExpr(ctx: AntlrParser.MulExprContext): BinaryExpr {
        val left = visit(ctx.expr(0)!!) as Expr
        val right = visit(ctx.expr(1)!!) as Expr
        val op = when {
            ctx.STAR() != null -> BinaryOp.MUL
            ctx.SLASH() != null -> BinaryOp.DIV
            else -> BinaryOp.MOD
        }
        return BinaryExpr(left, op, right)
    }

    override fun visitAddExpr(ctx: AntlrParser.AddExprContext): BinaryExpr {
        val left = visit(ctx.expr(0)!!) as Expr
        val right = visit(ctx.expr(1)!!) as Expr
        val op = if (ctx.PLUS() != null) BinaryOp.ADD else BinaryOp.SUB
        return BinaryExpr(left, op, right)
    }

    override fun visitCompareExpr(ctx: AntlrParser.CompareExprContext): BinaryExpr {
        val left = visit(ctx.expr(0)!!) as Expr
        val right = visit(ctx.expr(1)!!) as Expr
        val op = when {
            ctx.LESS() != null -> BinaryOp.LT
            ctx.LESS_EQUAL() != null -> BinaryOp.LTE
            ctx.GREATER() != null -> BinaryOp.GT
            else -> BinaryOp.GTE
        }
        return BinaryExpr(left, op, right)
    }

    override fun visitEqualExpr(ctx: AntlrParser.EqualExprContext): BinaryExpr {
        val left = visit(ctx.expr(0)!!) as Expr
        val right = visit(ctx.expr(1)!!) as Expr
        val op = if (ctx.EQUAL_EQUAL() != null) BinaryOp.EQ else BinaryOp.NEQ
        return BinaryExpr(left, op, right)
    }

    override fun visitAndExpr(ctx: AntlrParser.AndExprContext): BinaryExpr {
        val left = visit(ctx.expr(0)!!) as Expr
        val right = visit(ctx.expr(1)!!) as Expr
        return BinaryExpr(left, BinaryOp.AND, right)
    }

    override fun visitOrExpr(ctx: AntlrParser.OrExprContext): BinaryExpr {
        val left = visit(ctx.expr(0)!!) as Expr
        val right = visit(ctx.expr(1)!!) as Expr
        return BinaryExpr(left, BinaryOp.OR, right)
    }

    override fun visitCoalesceExpr(ctx: AntlrParser.CoalesceExprContext): BinaryExpr {
        val left = visit(ctx.expr(0)!!) as Expr
        val right = visit(ctx.expr(1)!!) as Expr
        return BinaryExpr(left, BinaryOp.COALESCE, right)
    }

    override fun visitNullExpr(ctx: AntlrParser.NullExprContext): Expr = NullLiteral

    override fun visitTrueExpr(ctx: AntlrParser.TrueExprContext): Expr = BoolLiteral(true)

    override fun visitFalseExpr(ctx: AntlrParser.FalseExprContext): Expr = BoolLiteral(false)

    override fun visitNumberExpr(ctx: AntlrParser.NumberExprContext): NumberLiteral {
        return NumberLiteral(ctx.NUMBER().text)
    }

    override fun visitStringExpr(ctx: AntlrParser.StringExprContext): StringLiteral {
        return visitStringLiteral(ctx.stringLiteral())
    }

    override fun visitArrayExpr(ctx: AntlrParser.ArrayExprContext): ArrayLiteral {
        val elements = ctx.argList()?.arg()?.map { visit(it.expr()) as Expr } ?: emptyList()
        return ArrayLiteral(elements)
    }

    override fun visitMapExpr(ctx: AntlrParser.MapExprContext): MapLiteral {
        return buildMapLiteral(ctx.mapLiteral())
    }

    override fun visitLambdaExpr(ctx: AntlrParser.LambdaExprContext): LambdaExpr {
        return visitLambda(ctx.lambda())
    }

    override fun visitIdentExpr(ctx: AntlrParser.IdentExprContext): ReferenceExpr {
        return ReferenceExpr(ident(ctx.identOrKeyword()))
    }

    override fun visitParenExpr(ctx: AntlrParser.ParenExprContext): Expr {
        return visit(ctx.expr()) as Expr
    }

    // ── condExpr visitors ───────────────────────────────────────

    override fun visitCondMemberExpr(ctx: AntlrParser.CondMemberExprContext): MemberExpr {
        val target = visit(ctx.condExpr()) as Expr
        val member = ident(ctx.identOrKeyword())
        return MemberExpr(target, member)
    }

    override fun visitCondOptionalMemberExpr(ctx: AntlrParser.CondOptionalMemberExprContext): MemberExpr {
        val target = visit(ctx.condExpr()) as Expr
        val member = ident(ctx.identOrKeyword())
        return MemberExpr(target, member, optional = true)
    }

    override fun visitCondCallExpr(ctx: AntlrParser.CondCallExprContext): CallExpr {
        val target = visit(ctx.condExpr()) as Expr
        val args = visitArgListToArgs(ctx.argList())
        return CallExpr(target, args)
    }

    override fun visitCondOptionalCallExpr(ctx: AntlrParser.CondOptionalCallExprContext): CallExpr {
        val target = visit(ctx.condExpr()) as Expr
        val args = visitArgListToArgs(ctx.argList())
        return CallExpr(target, args, optional = true)
    }

    override fun visitCondIndexExpr(ctx: AntlrParser.CondIndexExprContext): IndexExpr {
        val target = visit(ctx.condExpr()) as Expr
        val index = visit(ctx.expr()) as Expr
        return IndexExpr(target, index)
    }

    override fun visitCondOptionalIndexExpr(ctx: AntlrParser.CondOptionalIndexExprContext): IndexExpr {
        val target = visit(ctx.condExpr()) as Expr
        val index = visit(ctx.expr()) as Expr
        return IndexExpr(target, index, optional = true)
    }

    override fun visitCondUnaryExpr(ctx: AntlrParser.CondUnaryExprContext): UnaryExpr {
        val op = if (ctx.BANG() != null) UnaryOp.NOT else UnaryOp.NEGATE
        val operand = visit(ctx.condExpr()) as Expr
        return UnaryExpr(op, operand)
    }

    override fun visitCondPowerExpr(ctx: AntlrParser.CondPowerExprContext): BinaryExpr {
        val left = visit(ctx.condExpr(0)!!) as Expr
        val right = visit(ctx.condExpr(1)!!) as Expr
        return BinaryExpr(left, BinaryOp.POW, right)
    }

    override fun visitCondMulExpr(ctx: AntlrParser.CondMulExprContext): BinaryExpr {
        val left = visit(ctx.condExpr(0)!!) as Expr
        val right = visit(ctx.condExpr(1)!!) as Expr
        val op = when {
            ctx.STAR() != null -> BinaryOp.MUL
            ctx.SLASH() != null -> BinaryOp.DIV
            else -> BinaryOp.MOD
        }
        return BinaryExpr(left, op, right)
    }

    override fun visitCondAddExpr(ctx: AntlrParser.CondAddExprContext): BinaryExpr {
        val left = visit(ctx.condExpr(0)!!) as Expr
        val right = visit(ctx.condExpr(1)!!) as Expr
        val op = if (ctx.PLUS() != null) BinaryOp.ADD else BinaryOp.SUB
        return BinaryExpr(left, op, right)
    }

    override fun visitCondCompareExpr(ctx: AntlrParser.CondCompareExprContext): BinaryExpr {
        val left = visit(ctx.condExpr(0)!!) as Expr
        val right = visit(ctx.condExpr(1)!!) as Expr
        val op = when {
            ctx.LESS() != null -> BinaryOp.LT
            ctx.LESS_EQUAL() != null -> BinaryOp.LTE
            ctx.GREATER() != null -> BinaryOp.GT
            else -> BinaryOp.GTE
        }
        return BinaryExpr(left, op, right)
    }

    override fun visitCondEqualExpr(ctx: AntlrParser.CondEqualExprContext): BinaryExpr {
        val left = visit(ctx.condExpr(0)!!) as Expr
        val right = visit(ctx.condExpr(1)!!) as Expr
        val op = if (ctx.EQUAL_EQUAL() != null) BinaryOp.EQ else BinaryOp.NEQ
        return BinaryExpr(left, op, right)
    }

    override fun visitCondAndExpr(ctx: AntlrParser.CondAndExprContext): BinaryExpr {
        val left = visit(ctx.condExpr(0)!!) as Expr
        val right = visit(ctx.condExpr(1)!!) as Expr
        return BinaryExpr(left, BinaryOp.AND, right)
    }

    override fun visitCondOrExpr(ctx: AntlrParser.CondOrExprContext): BinaryExpr {
        val left = visit(ctx.condExpr(0)!!) as Expr
        val right = visit(ctx.condExpr(1)!!) as Expr
        return BinaryExpr(left, BinaryOp.OR, right)
    }

    override fun visitCondCoalesceExpr(ctx: AntlrParser.CondCoalesceExprContext): BinaryExpr {
        val left = visit(ctx.condExpr(0)!!) as Expr
        val right = visit(ctx.condExpr(1)!!) as Expr
        return BinaryExpr(left, BinaryOp.COALESCE, right)
    }

    override fun visitCondNullExpr(ctx: AntlrParser.CondNullExprContext): Expr = NullLiteral

    override fun visitCondTrueExpr(ctx: AntlrParser.CondTrueExprContext): Expr = BoolLiteral(true)

    override fun visitCondFalseExpr(ctx: AntlrParser.CondFalseExprContext): Expr = BoolLiteral(false)

    override fun visitCondNumberExpr(ctx: AntlrParser.CondNumberExprContext): NumberLiteral {
        return NumberLiteral(ctx.NUMBER().text)
    }

    override fun visitCondStringExpr(ctx: AntlrParser.CondStringExprContext): StringLiteral {
        return visitStringLiteral(ctx.stringLiteral())
    }

    override fun visitCondArrayExpr(ctx: AntlrParser.CondArrayExprContext): ArrayLiteral {
        val elements = ctx.argList()?.arg()?.map { visit(it.expr()) as Expr } ?: emptyList()
        return ArrayLiteral(elements)
    }

    override fun visitCondMapExpr(ctx: AntlrParser.CondMapExprContext): MapLiteral {
        return buildMapLiteral(ctx.mapLiteral())
    }

    override fun visitCondIdentExpr(ctx: AntlrParser.CondIdentExprContext): ReferenceExpr {
        return ReferenceExpr(ident(ctx.identOrKeyword()))
    }

    override fun visitCondParenExpr(ctx: AntlrParser.CondParenExprContext): Expr {
        return visit(ctx.expr()) as Expr
    }

    // ── Statements ──────────────────────────────────────────────

    override fun visitVarStmt(ctx: AntlrParser.VarStmtContext): VarDeclStmt {
        return visitVarDeclStmt(ctx.varDeclStmt())
    }

    override fun visitVarDeclStmt(ctx: AntlrParser.VarDeclStmtContext): VarDeclStmt {
        val names = ctx.destructuring()?.let { destructuringNames(it) }
            ?: listOf(ident(ctx.identOrKeyword()!!))
        val type = ctx.typeDecl()?.let { visitTypeDecl(it) }
        val value = visit(ctx.expr()) as Expr
        return VarDeclStmt(names, type, value)
    }

    override fun visitAssignStmt(ctx: AntlrParser.AssignStmtContext): AssignStmt {
        val target = ident(ctx.identOrKeyword())
        val op = visitAssignOp(ctx.assignOp())
        val value = visit(ctx.expr()) as Expr
        return AssignStmt(target, op, value)
    }

    override fun visitReturnStmt(ctx: AntlrParser.ReturnStmtContext): ReturnStmt {
        val value = ctx.expr()?.let { visit(it) as Expr }
        return ReturnStmt(value)
    }

    override fun visitExprStmt(ctx: AntlrParser.ExprStmtContext): ExprStmt {
        return ExprStmt(visit(ctx.expr()) as Expr)
    }

    override fun visitAnnotatedStmt(ctx: AntlrParser.AnnotatedStmtContext): Stmt {
        val annotations = ctx.annotation().map { visitAnnotation(it) as com.bazaar.parser.ast.Annotation }
        ctx.varDeclStmt()?.let {
            val stmt = visitVarDeclStmt(it)
            return stmt.copy(annotations = annotations)
        }
        ctx.callStmt()?.let {
            val callExpr = visitCallStmt(it)
            return CallStmt(callExpr, annotations)
        }
        error("Unknown annotated statement")
    }

    override fun visitCallStmt(ctx: AntlrParser.CallStmtContext): CallExpr {
        val target = ReferenceExpr(ident(ctx.identOrKeyword()))
        val args = visitArgListToArgs(ctx.argList())
        val lambda = ctx.lambda()?.let { visitLambda(it) }
        return CallExpr(target, args, lambda)
    }

    // ── Control flow ────────────────────────────────────────────

    override fun visitIfStatement(ctx: AntlrParser.IfStatementContext): IfStmt {
        return visitIfStmt(ctx.ifStmt())
    }

    override fun visitIfStmt(ctx: AntlrParser.IfStmtContext): IfStmt {
        val fragments = ctx.ifFragmentList().ifFragment().map { visitIfFragment(it) }
        val body = visitBlockToStmts(ctx.block(0)!!)

        // Flatten the recursive else-if chain
        val elseIfs = mutableListOf<ElseIf>()
        var elseBody: List<Stmt>? = null

        var current = ctx
        while (current.ELSE() != null) {
            val nested = current.ifStmt()
            if (nested != null) {
                val nestedFragments = nested.ifFragmentList().ifFragment().map { visitIfFragment(it) }
                val nestedBody = visitBlockToStmts(nested.block(0)!!)
                elseIfs.add(ElseIf(nestedFragments, nestedBody))
                current = nested
            } else {
                // else block (block(1) for the outermost, block(1) for nested)
                val elseBlock = current.block(1)!!
                elseBody = visitBlockToStmts(elseBlock)
                break
            }
        }

        return IfStmt(fragments, body, elseIfs, elseBody)
    }

    private fun visitIfFragment(ctx: AntlrParser.IfFragmentContext): IfFragment {
        return when (ctx) {
            is AntlrParser.IfVarFragmentContext -> visitIfVarFragment(ctx)
            is AntlrParser.IfExprFragmentContext -> visitIfExprFragment(ctx)
            else -> error("Unknown if fragment type")
        }
    }

    override fun visitIfVarFragment(ctx: AntlrParser.IfVarFragmentContext): IfVarFragment {
        val names = ctx.destructuring()?.let { destructuringNames(it) }
            ?: listOf(ident(ctx.identOrKeyword()!!))
        val type = ctx.typeDecl()?.let { visitTypeDecl(it) }
        val value = ctx.condExpr()?.let { visit(it) as Expr }
        return IfVarFragment(names, type, value)
    }

    override fun visitIfExprFragment(ctx: AntlrParser.IfExprFragmentContext): IfExprFragment {
        return IfExprFragment(visit(ctx.condExpr()) as Expr)
    }

    override fun visitForStatement(ctx: AntlrParser.ForStatementContext): ForStmt {
        return visit(ctx.forStmt()) as ForStmt
    }

    override fun visitForInStmt(ctx: AntlrParser.ForInStmtContext): ForInStmt {
        val names = ctx.destructuring()?.let { destructuringNames(it) }
            ?: listOf(ident(ctx.identOrKeyword()!!))
        val iterable = visit(ctx.condExpr()) as Expr
        val body = visitBlockToStmts(ctx.block())
        return ForInStmt(names, iterable, body)
    }

    override fun visitForCondStmt(ctx: AntlrParser.ForCondStmtContext): ForCondStmt {
        val condition = visit(ctx.condExpr()) as Expr
        val body = visitBlockToStmts(ctx.block())
        return ForCondStmt(condition, body)
    }

    override fun visitSwitchStatement(ctx: AntlrParser.SwitchStatementContext): SwitchStmt {
        return visitSwitchStmt(ctx.switchStmt())
    }

    override fun visitSwitchStmt(ctx: AntlrParser.SwitchStmtContext): SwitchStmt {
        val expr = visit(ctx.condExpr()) as Expr
        val cases = ctx.switchCase().map { visitSwitchCase(it) }
        val default = ctx.switchDefault()?.let { it.stmt().map { s -> visit(s) as Stmt } }
        return SwitchStmt(expr, cases, default)
    }

    override fun visitSwitchCase(ctx: AntlrParser.SwitchCaseContext): SwitchCase {
        val expr = visit(ctx.condExpr()) as Expr
        val body = ctx.stmt().map { visit(it) as Stmt }
        return SwitchCase(expr, body)
    }

    // ── Support ─────────────────────────────────────────────────

    override fun visitLambda(ctx: AntlrParser.LambdaContext): LambdaExpr {
        val params = ctx.lambdaParams()?.lambdaParam()?.map { visitLambdaParam(it) } ?: emptyList()
        val returnType = if (ctx.ARROW() != null) ctx.typeDecl()?.let { visitTypeDecl(it) } else null
        val body = ctx.stmt().map { visit(it) as Stmt }
        return LambdaExpr(params, returnType, body)
    }

    override fun visitLambdaParam(ctx: AntlrParser.LambdaParamContext): LambdaParam {
        val name = ident(ctx.identOrKeyword())
        val type = ctx.typeDecl()?.let { visitTypeDecl(it) }
        return LambdaParam(name, type)
    }

    override fun visitArg(ctx: AntlrParser.ArgContext): Argument {
        val name = ctx.IDENTIFIER()?.text
        val value = visit(ctx.expr()) as Expr
        return Argument(name, value)
    }

    override fun visitAnnotation(ctx: AntlrParser.AnnotationContext): com.bazaar.parser.ast.Annotation {
        val name = ident(ctx.identOrKeyword())
        val args = if (ctx.LPAREN() != null) visitArgListToArgs(ctx.argList()) else null
        return com.bazaar.parser.ast.Annotation(name, args)
    }

    override fun visitStringLiteral(ctx: AntlrParser.StringLiteralContext): StringLiteral {
        val parts = ctx.stringPart().map { visitStringPart(it) }
        return StringLiteral(parts)
    }

    override fun visitStringPart(ctx: AntlrParser.StringPartContext): StringPart {
        ctx.STRING_TEXT()?.let { return TextPart(it.text) }
        ctx.STRING_DOLLAR()?.let { return TextPart(it.text) }
        ctx.STRING_ESCAPE()?.let { return EscapePart(it.text) }
        ctx.UNICODE_SHORT_ESCAPE()?.let { return EscapePart(it.text) }
        ctx.UNICODE_LONG_ESCAPE()?.let { return EscapePart(it.text) }
        ctx.stringInterp()?.let { return visitStringInterp(it) }
        error("Unknown string part")
    }

    override fun visitStringInterp(ctx: AntlrParser.StringInterpContext): InterpolationPart {
        return InterpolationPart(visit(ctx.expr()) as Expr)
    }

    override fun visitAssignOp(ctx: AntlrParser.AssignOpContext): AssignOp {
        return when {
            ctx.EQUAL() != null -> AssignOp.ASSIGN
            ctx.PLUS_EQUAL() != null -> AssignOp.ADD_ASSIGN
            ctx.MINUS_EQUAL() != null -> AssignOp.SUB_ASSIGN
            ctx.STAR_EQUAL() != null -> AssignOp.MUL_ASSIGN
            ctx.SLASH_EQUAL() != null -> AssignOp.DIV_ASSIGN
            else -> AssignOp.MOD_ASSIGN
        }
    }

    // ── Private helpers ─────────────────────────────────────────

    private fun ident(ctx: AntlrParser.IdentOrKeywordContext): String = ctx.text

    private fun visitBlockToStmts(ctx: AntlrParser.BlockContext): List<Stmt> {
        return ctx.stmt().map { visit(it) as Stmt }
    }

    private fun visitArgListToArgs(ctx: AntlrParser.ArgListContext?): List<Argument> {
        return ctx?.arg()?.map { visitArg(it) as Argument } ?: emptyList()
    }

    private fun visitParamList(ctx: AntlrParser.ParameterListContext?): List<ParameterDecl> {
        return ctx?.parameterDecl()?.map { visitParameterDecl(it) as ParameterDecl } ?: emptyList()
    }

    private fun destructuringNames(ctx: AntlrParser.DestructuringContext): List<String> {
        return ctx.identOrKeyword().map { ident(it) }
    }

    private fun buildMapLiteral(ctx: AntlrParser.MapLiteralContext): MapLiteral {
        val entries = ctx.mapEntry().map { entry ->
            val key = visit(entry.expr(0)!!) as Expr
            val value = visit(entry.expr(1)!!) as Expr
            MapEntry(key, value)
        }
        return MapLiteral(entries)
    }
}
