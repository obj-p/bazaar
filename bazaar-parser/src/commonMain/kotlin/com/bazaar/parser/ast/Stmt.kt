package com.bazaar.parser.ast

sealed interface Stmt

// --- Simple statements ---

data class VarDeclStmt(
    val names: List<String>,
    val type: TypeDecl? = null,
    val value: Expr,
    val annotations: List<Annotation> = emptyList(),
) : Stmt

data class AssignStmt(val target: String, val op: AssignOp, val value: Expr) : Stmt

data class CallStmt(
    val expr: CallExpr,
    val annotations: List<Annotation> = emptyList(),
) : Stmt

data class ReturnStmt(val value: Expr? = null) : Stmt

data class ExprStmt(val expr: Expr) : Stmt

// --- If ---

data class IfStmt(
    val fragments: List<IfFragment>,
    val body: List<Stmt>,
    val elseIfs: List<ElseIf> = emptyList(),
    val elseBody: List<Stmt>? = null,
) : Stmt

data class ElseIf(val fragments: List<IfFragment>, val body: List<Stmt>)

sealed interface IfFragment

data class IfVarFragment(
    val names: List<String>,
    val type: TypeDecl? = null,
    val value: Expr? = null,
) : IfFragment

data class IfExprFragment(val expr: Expr) : IfFragment

// --- For ---

sealed interface ForStmt : Stmt

data class ForInStmt(
    val names: List<String>,
    val iterable: Expr,
    val body: List<Stmt>,
) : ForStmt

data class ForCondStmt(val condition: Expr, val body: List<Stmt>) : ForStmt

// --- Switch ---

data class SwitchStmt(
    val expr: Expr,
    val cases: List<SwitchCase>,
    val default: List<Stmt>? = null,
) : Stmt

data class SwitchCase(val expr: Expr, val body: List<Stmt>)
