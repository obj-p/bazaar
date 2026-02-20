package com.bazaar.sema

import com.bazaar.sema.ir.*

fun formatType(type: IrType): String {
    val base = when (type) {
        is IrBuiltinType -> type.name
        is IrDeclaredType -> type.name
        is IrArrayType -> "[${formatType(type.elementType)}]"
        is IrMapType -> "[${formatType(type.keyType)}: ${formatType(type.valueType)}]"
        is IrFunctionType -> {
            val params = type.paramTypes.joinToString(", ") { formatType(it) }
            val ret = if (type.returnType != null) " -> ${formatType(type.returnType)}" else ""
            "($params)$ret"
        }
        is IrErrorType -> type.name
    }
    return if (type.nullable) "$base?" else base
}
