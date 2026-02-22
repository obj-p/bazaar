package com.bazaar.sema

import com.bazaar.parser.ast.Decl

enum class SymbolKind { COMPONENT, DATA, MODIFIER, ENUM, FUNCTION, TEMPLATE, PREVIEW }

data class Symbol(
    val name: String,
    val kind: SymbolKind,
    val decl: Decl,
)

class SymbolTable {
    private val symbols = mutableMapOf<String, Symbol>()

    fun define(symbol: Symbol): SemaDiagnostic? {
        val existing = symbols[symbol.name]
        if (existing != null) {
            return SemaDiagnostic(
                SemaSeverity.ERROR,
                "duplicate declaration '${symbol.name}' (already declared as ${existing.kind})",
            )
        }
        symbols[symbol.name] = symbol
        return null
    }

    fun lookup(name: String): Symbol? = symbols[name]

    fun allSymbols(): Collection<Symbol> = symbols.values
}
