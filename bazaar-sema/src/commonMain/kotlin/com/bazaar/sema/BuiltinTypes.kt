package com.bazaar.sema

object BuiltinTypes {
    private val builtins = setOf("int", "double", "string", "bool", "component")

    fun isBuiltin(name: String): Boolean = name in builtins

    fun allNames(): Set<String> = builtins
}
