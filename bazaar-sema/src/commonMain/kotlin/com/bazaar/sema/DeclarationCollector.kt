package com.bazaar.sema

import com.bazaar.parser.ast.*

object DeclarationCollector {

    fun collect(file: BazaarFile): CollectionResult {
        val table = SymbolTable()
        val diagnostics = mutableListOf<SemaDiagnostic>()

        for (decl in file.declarations) {
            val name = declName(decl)
            val kind = declKind(decl)

            if (BuiltinTypes.isBuiltin(name)) {
                diagnostics += SemaDiagnostic(
                    SemaSeverity.ERROR,
                    "declaration '$name' shadows built-in type",
                )
                continue
            }

            val error = table.define(Symbol(name, kind, decl))
            if (error != null) diagnostics += error
        }

        return CollectionResult(table, diagnostics)
    }

    private fun declName(decl: Decl): String = when (decl) {
        is ComponentDecl -> decl.name
        is DataDecl -> decl.name
        is ModifierDecl -> decl.name
        is EnumDecl -> decl.name
        is FunctionDecl -> decl.name
        is TemplateDecl -> decl.name
        is PreviewDecl -> decl.name
    }

    private fun declKind(decl: Decl): SymbolKind = when (decl) {
        is ComponentDecl -> SymbolKind.COMPONENT
        is DataDecl -> SymbolKind.DATA
        is ModifierDecl -> SymbolKind.MODIFIER
        is EnumDecl -> SymbolKind.ENUM
        is FunctionDecl -> SymbolKind.FUNCTION
        is TemplateDecl -> SymbolKind.TEMPLATE
        is PreviewDecl -> SymbolKind.PREVIEW
    }
}

data class CollectionResult(
    val symbolTable: SymbolTable,
    val diagnostics: List<SemaDiagnostic>,
)
