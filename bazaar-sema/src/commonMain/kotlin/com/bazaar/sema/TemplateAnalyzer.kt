package com.bazaar.sema

import com.bazaar.parser.ast.*
import com.bazaar.sema.ir.*

data class TemplateAnalysisResult(val ir: IrFile, val diagnostics: List<SemaDiagnostic>)

object TemplateAnalyzer {

    fun analyze(ir: IrFile, symbolTable: SymbolTable): TemplateAnalysisResult {
        val diagnostics = mutableListOf<SemaDiagnostic>()
        val newDeclarations = ir.declarations.map { decl ->
            when (decl) {
                is IrTemplate -> {
                    val ctx = AnalyzeContext(symbolTable, decl.name, "template", diagnostics)
                    val body = ctx.analyzeBody(decl.body)
                    decl.copy(body = body)
                }
                is IrPreview -> {
                    val ctx = AnalyzeContext(symbolTable, decl.name, "preview", diagnostics)
                    val body = ctx.analyzeBody(decl.body)
                    decl.copy(body = body)
                }
                else -> decl
            }
        }
        return TemplateAnalysisResult(
            ir = ir.copy(declarations = newDeclarations),
            diagnostics = diagnostics,
        )
    }

    private class AnalyzeContext(
        private val symbolTable: SymbolTable,
        private val declName: String,
        private val declKind: String,
        private val diagnostics: MutableList<SemaDiagnostic>,
    ) {
        fun analyzeBody(body: List<IrTemplateNode>): List<IrTemplateNode> {
            return body.flatMap { node ->
                when (node) {
                    is IrRawBody -> analyzeStmts(node.stmts)
                    else -> listOf(node)
                }
            }
        }

        private fun analyzeStmts(stmts: List<Stmt>): List<IrTemplateNode> =
            stmts.map { analyzeStmt(it) }

        private fun analyzeStmt(stmt: Stmt): IrTemplateNode = when (stmt) {
            is CallStmt -> analyzeCallStmt(stmt)
            is ExprStmt -> analyzeExprStmt(stmt)
            is VarDeclStmt -> analyzeVarDecl(stmt)
            is ForInStmt -> IrForNode(
                names = stmt.names,
                iterable = stmt.iterable,
                body = analyzeStmts(stmt.body),
            )
            is ForCondStmt -> IrForCondNode(
                condition = stmt.condition,
                body = analyzeStmts(stmt.body),
            )
            is IfStmt -> analyzeIfStmt(stmt)
            is SwitchStmt -> IrSwitchNode(
                expr = stmt.expr,
                cases = stmt.cases.map { IrSwitchCase(it.expr, analyzeStmts(it.body)) },
                default = stmt.default?.let { analyzeStmts(it) },
            )
            is AssignStmt -> IrAssignNode(stmt)
            is ReturnStmt -> {
                diagnostics += SemaDiagnostic(
                    SemaSeverity.ERROR,
                    "return statements are not allowed in $declKind '$declName'",
                )
                IrReturnNode(stmt.value)
            }
        }

        private fun analyzeCallStmt(stmt: CallStmt): IrTemplateNode {
            val callExpr = stmt.expr

            // Check for @State on call statement — always an error
            val stateAnnotation = stmt.annotations.find { it.name == "State" }
            if (stateAnnotation != null) {
                val callName = (callExpr.target as? ReferenceExpr)?.name ?: "unknown"
                diagnostics += SemaDiagnostic(
                    SemaSeverity.ERROR,
                    "@State annotation is not valid on call to '$callName' in $declKind '$declName'",
                )
            }

            val resolved = resolveCall(callExpr)

            // Handle @Modifier annotation (only valid on component/template calls)
            val modifierAnnotation = stmt.annotations.find { it.name == "Modifier" }
            if (modifierAnnotation != null && resolved is IrComponentCall) {
                val modifier = resolveModifier(modifierAnnotation)
                return resolved.copy(modifier = modifier)
            }
            if (modifierAnnotation != null && resolved is IrFunctionCall) {
                diagnostics += SemaDiagnostic(
                    SemaSeverity.ERROR,
                    "@Modifier annotation is not valid on function call '${resolved.name}' in $declKind '$declName'",
                )
            }

            return resolved
        }

        private fun analyzeExprStmt(stmt: ExprStmt): IrTemplateNode {
            val expr = stmt.expr
            if (expr is CallExpr) {
                return resolveCall(expr)
            }
            return IrExprNode(expr)
        }

        private fun resolveCall(callExpr: CallExpr): IrTemplateNode {
            val target = callExpr.target
            if (target !is ReferenceExpr) {
                return IrExprNode(callExpr)
            }

            val name = target.name
            val symbol = symbolTable.lookup(name)
            if (symbol == null) {
                diagnostics += SemaDiagnostic(
                    SemaSeverity.ERROR,
                    "undefined call target '$name' in $declKind '$declName'",
                )
                return IrExprNode(callExpr)
            }

            val args = callExpr.args.map { IrCallArg(it.name, it.value) }

            return when (symbol.kind) {
                SymbolKind.COMPONENT, SymbolKind.TEMPLATE -> {
                    val children = callExpr.trailingLambda?.let { analyzeStmts(it.body) } ?: emptyList()
                    IrComponentCall(
                        name = name,
                        symbolKind = symbol.kind,
                        args = args,
                        modifier = null,
                        children = children,
                    )
                }
                SymbolKind.FUNCTION, SymbolKind.DATA -> {
                    IrFunctionCall(
                        name = name,
                        args = args,
                        trailingLambda = callExpr.trailingLambda,
                    )
                }
                SymbolKind.ENUM -> {
                    diagnostics += SemaDiagnostic(
                        SemaSeverity.ERROR,
                        "'$name' is an enum, not a component or function, in $declKind '$declName'",
                    )
                    IrExprNode(callExpr)
                }
                SymbolKind.MODIFIER -> {
                    diagnostics += SemaDiagnostic(
                        SemaSeverity.ERROR,
                        "'$name' is a modifier, not a component or function, in $declKind '$declName'",
                    )
                    IrExprNode(callExpr)
                }
                SymbolKind.PREVIEW -> {
                    diagnostics += SemaDiagnostic(
                        SemaSeverity.ERROR,
                        "preview '$name' cannot be called from $declKind '$declName'",
                    )
                    IrExprNode(callExpr)
                }
            }
        }

        private fun resolveModifier(annotation: Annotation): IrModifierCall? {
            val annotationArgs = annotation.args
            if (annotationArgs == null || annotationArgs.isEmpty()) {
                diagnostics += SemaDiagnostic(
                    SemaSeverity.ERROR,
                    "@Modifier annotation requires a modifier constructor call in $declKind '$declName'",
                )
                return null
            }

            val argValue = annotationArgs[0].value
            if (argValue !is CallExpr) {
                diagnostics += SemaDiagnostic(
                    SemaSeverity.ERROR,
                    "@Modifier argument must be a modifier constructor call in $declKind '$declName'",
                )
                return null
            }

            val modifierTarget = argValue.target
            if (modifierTarget !is ReferenceExpr) {
                diagnostics += SemaDiagnostic(
                    SemaSeverity.ERROR,
                    "@Modifier argument must be a modifier constructor call in $declKind '$declName'",
                )
                return null
            }

            val modifierName = modifierTarget.name
            val symbol = symbolTable.lookup(modifierName)
            if (symbol == null) {
                diagnostics += SemaDiagnostic(
                    SemaSeverity.ERROR,
                    "undefined modifier '$modifierName' in @Modifier annotation in $declKind '$declName'",
                )
                return null
            }

            if (symbol.kind != SymbolKind.MODIFIER) {
                diagnostics += SemaDiagnostic(
                    SemaSeverity.ERROR,
                    "'$modifierName' is not a modifier type in @Modifier annotation in $declKind '$declName'",
                )
                return null
            }

            return IrModifierCall(
                name = modifierName,
                args = argValue.args.map { IrCallArg(it.name, it.value) },
            )
        }

        private fun analyzeVarDecl(stmt: VarDeclStmt): IrTemplateNode {
            val isState = stmt.annotations.any { it.name == "State" }
            if (isState) {
                if (stmt.names.size != 1) {
                    diagnostics += SemaDiagnostic(
                        SemaSeverity.ERROR,
                        "@State variable must have exactly one name in $declKind '$declName'",
                    )
                }
                return IrStateDecl(name = stmt.names.first(), value = stmt.value)
            }
            return IrLocalDecl(names = stmt.names, value = stmt.value)
        }

        private fun analyzeIfStmt(stmt: IfStmt): IrIfNode {
            return IrIfNode(
                fragments = stmt.fragments,
                body = analyzeStmts(stmt.body),
                elseIfs = stmt.elseIfs.map {
                    IrElseIfBranch(fragments = it.fragments, body = analyzeStmts(it.body))
                },
                elseBody = stmt.elseBody?.let { analyzeStmts(it) },
            )
        }
    }
}
