package com.bazaar.sema.ir

import com.bazaar.parser.ast.AssignStmt
import com.bazaar.parser.ast.Expr
import com.bazaar.parser.ast.IfFragment
import com.bazaar.parser.ast.LambdaExpr
import com.bazaar.parser.ast.Stmt
import com.bazaar.sema.SymbolKind

sealed interface IrTemplateNode

// Placeholder: raw AST body not yet analyzed by Pass 4.
// Present between Pass 2 and Pass 4; replaced by Pass 4.
data class IrRawBody(val stmts: List<Stmt>) : IrTemplateNode

// Component or template instantiation: Row { ... }, Text("hello")
data class IrComponentCall(
    val name: String,
    val symbolKind: SymbolKind,
    val args: List<IrCallArg>,
    val modifier: IrModifierCall?,
    val children: List<IrTemplateNode>,
) : IrTemplateNode

// Function call: Print(message)
data class IrFunctionCall(
    val name: String,
    val args: List<IrCallArg>,
    val trailingLambda: LambdaExpr?,
) : IrTemplateNode

// Mapped from Argument(name, value) in the parser AST
data class IrCallArg(val name: String?, val value: Expr)

data class IrModifierCall(val name: String, val args: List<IrCallArg>)

// @State variable: @State var count = 0
data class IrStateDecl(val name: String, val value: Expr) : IrTemplateNode

// Local variable: var label = "hi"
data class IrLocalDecl(val names: List<String>, val value: Expr) : IrTemplateNode

// For-in loop
data class IrForNode(val names: List<String>, val iterable: Expr, val body: List<IrTemplateNode>) : IrTemplateNode

// While-style loop: for condition { ... }
data class IrForCondNode(val condition: Expr, val body: List<IrTemplateNode>) : IrTemplateNode

// If/else conditional
data class IrIfNode(
    val fragments: List<IfFragment>,
    val body: List<IrTemplateNode>,
    val elseIfs: List<IrElseIfBranch>,
    val elseBody: List<IrTemplateNode>?,
) : IrTemplateNode

data class IrElseIfBranch(val fragments: List<IfFragment>, val body: List<IrTemplateNode>)

// Switch statement
data class IrSwitchNode(val expr: Expr, val cases: List<IrSwitchCase>, val default: List<IrTemplateNode>?) : IrTemplateNode
data class IrSwitchCase(val expr: Expr, val body: List<IrTemplateNode>)

// Assignment: count += 1
data class IrAssignNode(val stmt: AssignStmt) : IrTemplateNode

// Return statement — error in templates, but captured for diagnostics
data class IrReturnNode(val value: Expr?) : IrTemplateNode

// Catch-all expression statement (non-call expressions)
data class IrExprNode(val expr: Expr) : IrTemplateNode
