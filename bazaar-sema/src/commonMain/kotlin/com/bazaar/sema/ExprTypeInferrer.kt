package com.bazaar.sema

import com.bazaar.parser.ast.*
import com.bazaar.sema.ir.*

sealed interface InferResult {
    data class Inferred(val type: IrType) : InferResult
    data object NullLiteralResult : InferResult
    data object Uninferrable : InferResult
}

object ExprTypeInferrer {

    fun infer(expr: Expr, symbolTable: SymbolTable): InferResult = when (expr) {
        is NumberLiteral -> inferNumber(expr)
        is StringLiteral -> InferResult.Inferred(IrBuiltinType("string", IrPrimitiveKind.STRING))
        is BoolLiteral -> InferResult.Inferred(IrBuiltinType("bool", IrPrimitiveKind.BOOL))
        is NullLiteral -> InferResult.NullLiteralResult
        is ArrayLiteral -> inferArray(expr, symbolTable)
        is MapLiteral -> inferMap(expr, symbolTable)
        is MemberExpr -> inferMember(expr, symbolTable)
        is UnaryExpr -> inferUnary(expr, symbolTable)
        else -> InferResult.Uninferrable
    }

    private fun inferNumber(expr: NumberLiteral): InferResult.Inferred {
        val isDouble = expr.value.contains('.') || expr.value.contains('e') || expr.value.contains('E')
        return if (isDouble) {
            InferResult.Inferred(IrBuiltinType("double", IrPrimitiveKind.DOUBLE))
        } else {
            InferResult.Inferred(IrBuiltinType("int", IrPrimitiveKind.INT))
        }
    }

    private fun inferArray(expr: ArrayLiteral, symbolTable: SymbolTable): InferResult {
        if (expr.elements.isEmpty()) {
            return InferResult.Inferred(IrArrayType(IrErrorType(IrErrorType.UNKNOWN)))
        }
        // Infer from first element only; heterogeneous arrays deferred to Milestone 3
        val first = infer(expr.elements[0], symbolTable)
        return when (first) {
            is InferResult.Inferred -> InferResult.Inferred(IrArrayType(first.type))
            else -> InferResult.Uninferrable
        }
    }

    private fun inferMap(expr: MapLiteral, symbolTable: SymbolTable): InferResult {
        if (expr.entries.isEmpty()) {
            return InferResult.Inferred(IrMapType(IrErrorType(IrErrorType.UNKNOWN), IrErrorType(IrErrorType.UNKNOWN)))
        }
        val firstKey = infer(expr.entries[0].key, symbolTable)
        val firstValue = infer(expr.entries[0].value, symbolTable)
        return if (firstKey is InferResult.Inferred && firstValue is InferResult.Inferred) {
            InferResult.Inferred(IrMapType(firstKey.type, firstValue.type))
        } else {
            InferResult.Uninferrable
        }
    }

    private fun inferMember(expr: MemberExpr, symbolTable: SymbolTable): InferResult {
        val target = expr.target
        if (target is ReferenceExpr) {
            val symbol = symbolTable.lookup(target.name)
            if (symbol != null && symbol.kind == SymbolKind.ENUM) {
                return InferResult.Inferred(IrDeclaredType(target.name, SymbolKind.ENUM))
            }
        }
        return InferResult.Uninferrable
    }

    private fun inferUnary(expr: UnaryExpr, symbolTable: SymbolTable): InferResult {
        val operand = expr.operand
        return when {
            expr.op == UnaryOp.NEGATE && operand is NumberLiteral -> inferNumber(operand)
            expr.op == UnaryOp.NOT && operand is BoolLiteral ->
                InferResult.Inferred(IrBuiltinType("bool", IrPrimitiveKind.BOOL))
            else -> InferResult.Uninferrable
        }
    }
}
