package com.bazaar.parser

import com.bazaar.parser.ast.BazaarFile

data class Diagnostic(
    val severity: Severity,
    val line: Int,
    val column: Int,
    val message: String,
)

enum class Severity { ERROR, WARNING }

data class ParseResult(
    val ast: BazaarFile?,
    val diagnostics: List<Diagnostic>,
) {
    val hasErrors get() = ast == null || diagnostics.any { it.severity == Severity.ERROR }
}

class ParseException(
    val diagnostics: List<Diagnostic>,
) : Exception(diagnostics.firstOrNull()?.let { "${it.line}:${it.column}: ${it.message}" } ?: "parse error")
