package com.bazaar.parser

fun serializeDiagnostics(diagnostics: List<Diagnostic>): String =
    diagnostics.joinToString("\n", postfix = if (diagnostics.isNotEmpty()) "\n" else "") {
        "${it.severity} ${it.line}:${it.column} ${it.message}"
    }
