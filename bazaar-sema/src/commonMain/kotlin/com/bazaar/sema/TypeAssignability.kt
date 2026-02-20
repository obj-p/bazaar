package com.bazaar.sema

import com.bazaar.sema.ir.*

/**
 * Two-phase assignability check:
 * 1. Reject nullable source → non-nullable target (regardless of structural shape).
 * 2. Check structural compatibility ignoring nullability (shape, kind, widening).
 *
 * Error types on either side short-circuit to `true` to avoid cascading diagnostics
 * from earlier passes.
 */
fun isAssignable(source: IrType, target: IrType): Boolean {
    // Error types are always assignable (avoid cascading errors from Pass 2)
    if (source is IrErrorType || target is IrErrorType) return true

    // Nullable source to non-nullable target is never allowed
    if (source.nullable && !target.nullable) return false

    return isStructurallyAssignable(source, target)
}

private fun isStructurallyAssignable(source: IrType, target: IrType): Boolean = when {
    // Same builtin type
    source is IrBuiltinType && target is IrBuiltinType -> {
        if (source.kind == target.kind) true
        // Numeric widening: int -> double
        else source.kind == IrPrimitiveKind.INT && target.kind == IrPrimitiveKind.DOUBLE
    }

    // Same declared type
    source is IrDeclaredType && target is IrDeclaredType ->
        source.name == target.name && source.symbolKind == target.symbolKind

    // Array types
    source is IrArrayType && target is IrArrayType -> {
        // Empty array (unknown element) is assignable to any array
        if (source.elementType is IrErrorType && (source.elementType as IrErrorType).name == IrErrorType.UNKNOWN) true
        else isAssignable(source.elementType, target.elementType)
    }

    // Map types
    source is IrMapType && target is IrMapType -> {
        // Empty map (both unknown) is assignable to any map.
        // Partially-unknown maps (one unknown component) are handled by the
        // error-type bail-out in isAssignable, so they also pass correctly.
        val emptyKey = source.keyType is IrErrorType && (source.keyType as IrErrorType).name == IrErrorType.UNKNOWN
        val emptyValue = source.valueType is IrErrorType && (source.valueType as IrErrorType).name == IrErrorType.UNKNOWN
        if (emptyKey && emptyValue) true
        else isAssignable(source.keyType, target.keyType) && isAssignable(source.valueType, target.valueType)
    }

    // Function types
    source is IrFunctionType && target is IrFunctionType -> {
        if (source.paramTypes.size != target.paramTypes.size) false
        else {
            // Contravariant params
            val paramsMatch = source.paramTypes.zip(target.paramTypes).all { (s, t) -> isAssignable(t, s) }
            // Covariant return
            val returnMatch = when {
                source.returnType == null && target.returnType == null -> true
                source.returnType != null && target.returnType != null -> isAssignable(source.returnType, target.returnType)
                else -> false
            }
            paramsMatch && returnMatch
        }
    }

    else -> false
}
