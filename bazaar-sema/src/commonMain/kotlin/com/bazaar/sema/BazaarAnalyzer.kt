package com.bazaar.sema

import com.bazaar.parser.ast.BazaarFile
import com.bazaar.sema.ir.IrFile

object BazaarAnalyzer {

    fun analyze(file: BazaarFile): AnalysisResult {
        // Pass 1: collect declarations into symbol table
        val collection = DeclarationCollector.collect(file)
        val allDiagnostics = collection.diagnostics.toMutableList()

        // Pass 2: resolve types
        val resolution = TypeResolver.resolve(file, collection.symbolTable)
        allDiagnostics += resolution.diagnostics

        var hasErrors = allDiagnostics.any { it.severity == SemaSeverity.ERROR }

        // Pass 3: type checking (only when no prior errors)
        if (!hasErrors) {
            val typeCheck = TypeChecker.check(resolution.ir, collection.symbolTable)
            allDiagnostics += typeCheck.diagnostics
            hasErrors = allDiagnostics.any { it.severity == SemaSeverity.ERROR }
        }

        return AnalysisResult(
            ir = if (hasErrors) null else resolution.ir,
            diagnostics = allDiagnostics,
        )
    }
}

data class AnalysisResult(
    val ir: IrFile?,
    val diagnostics: List<SemaDiagnostic>,
) {
    val hasErrors get() = ir == null || diagnostics.any { it.severity == SemaSeverity.ERROR }
}
