package com.bazaar.sema

import com.bazaar.sema.ir.IrPrimitiveKind

object BuiltinTypes {
    private val builtins = mapOf(
        "int" to IrPrimitiveKind.INT,
        "double" to IrPrimitiveKind.DOUBLE,
        "string" to IrPrimitiveKind.STRING,
        "bool" to IrPrimitiveKind.BOOL,
        "component" to IrPrimitiveKind.COMPONENT,
    )

    fun isBuiltin(name: String): Boolean = name in builtins

    fun kindOf(name: String): IrPrimitiveKind? = builtins[name]

    fun allNames(): Set<String> = builtins.keys
}
