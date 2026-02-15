package com.bazaar.parser.ast

sealed interface Expr

// --- Binary / Unary ---

data class BinaryExpr(val left: Expr, val op: BinaryOp, val right: Expr) : Expr

data class UnaryExpr(val op: UnaryOp, val operand: Expr) : Expr

// --- Access ---

data class ReferenceExpr(val name: String) : Expr

data class MemberExpr(val target: Expr, val member: String, val optional: Boolean = false) : Expr

data class IndexExpr(val target: Expr, val index: Expr, val optional: Boolean = false) : Expr

data class CallExpr(
    val target: Expr,
    val args: List<Argument>,
    val trailingLambda: LambdaExpr? = null,
    val optional: Boolean = false,
) : Expr

data class LambdaExpr(val params: List<LambdaParam>, val returnType: TypeDecl? = null, val body: List<Stmt>) : Expr

// --- Literals ---

data object NullLiteral : Expr

data class BoolLiteral(val value: Boolean) : Expr

data class NumberLiteral(val value: String) : Expr

data class StringLiteral(val parts: List<StringPart>) : Expr

data class ArrayLiteral(val elements: List<Expr>) : Expr

data class MapLiteral(val entries: List<MapEntry>) : Expr

// --- String parts ---

sealed interface StringPart

data class TextPart(val text: String) : StringPart

data class EscapePart(val text: String) : StringPart

data class InterpolationPart(val expr: Expr) : StringPart

// --- Support types ---

data class Argument(val name: String? = null, val value: Expr)

data class LambdaParam(val name: String, val type: TypeDecl? = null)

data class MapEntry(val key: Expr, val value: Expr)

data class Annotation(val name: String, val args: List<Argument>? = null)
