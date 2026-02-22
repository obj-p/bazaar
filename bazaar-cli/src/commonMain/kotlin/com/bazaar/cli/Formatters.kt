package com.bazaar.cli

import com.bazaar.parser.ParseResult
import com.bazaar.parser.ast.AstSerializer
import com.bazaar.parser.serializeDiagnostics

fun formatTextResult(
    filePath: String,
    result: ParseResult,
    multiFile: Boolean,
): String {
    val sb = StringBuilder()
    if (multiFile) {
        sb.appendLine("--- $filePath ---")
    }
    val ast = result.ast
    if (ast != null) {
        sb.append(AstSerializer.serialize(ast))
    }
    if (result.diagnostics.isNotEmpty()) {
        sb.append(serializeDiagnostics(result.diagnostics))
    }
    return sb.toString()
}

fun formatJsonResult(
    filePath: String,
    result: ParseResult,
): String {
    val sb = StringBuilder()
    sb.appendLine("{")
    sb.appendLine("  \"file\": ${jsonString(filePath)},")
    sb.appendLine("  \"status\": ${if (result.hasErrors) "\"error\"" else "\"ok\""},")
    val ast = result.ast
    if (ast != null) {
        sb.appendLine("  \"ast\": ${jsonString(AstSerializer.serialize(ast))},")
    }
    sb.append("  \"diagnostics\": [")
    if (result.diagnostics.isNotEmpty()) {
        sb.appendLine()
        result.diagnostics.forEachIndexed { index, diag ->
            sb.append("    {")
            sb.append("\"severity\": ${jsonString(diag.severity.name)}, ")
            sb.append("\"line\": ${diag.line}, ")
            sb.append("\"column\": ${diag.column}, ")
            sb.append("\"message\": ${jsonString(diag.message)}")
            sb.append("}")
            if (index < result.diagnostics.size - 1) sb.append(",")
            sb.appendLine()
        }
        sb.append("  ")
    }
    sb.appendLine("]")
    sb.append("}")
    return sb.toString()
}

private fun jsonString(value: String): String {
    val sb = StringBuilder("\"")
    for (c in value) {
        when (c) {
            '"' -> sb.append("\\\"")
            '\\' -> sb.append("\\\\")
            '\n' -> sb.append("\\n")
            '\r' -> sb.append("\\r")
            '\t' -> sb.append("\\t")
            else ->
                if (c < '\u0020') {
                    sb.append("\\u${c.code.toString(16).padStart(4, '0')}")
                } else {
                    sb.append(c)
                }
        }
    }
    sb.append('"')
    return sb.toString()
}
