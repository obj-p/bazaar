package com.bazaar.parser

import org.antlr.v4.kotlinruntime.BaseErrorListener
import org.antlr.v4.kotlinruntime.RecognitionException
import org.antlr.v4.kotlinruntime.Recognizer

internal class BazaarErrorListener : BaseErrorListener() {

    private val _diagnostics = mutableListOf<Diagnostic>()
    val diagnostics: List<Diagnostic> get() = _diagnostics

    override fun syntaxError(
        recognizer: Recognizer<*, *>,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String,
        e: RecognitionException?,
    ) {
        _diagnostics.add(
            Diagnostic(
                severity = Severity.ERROR,
                line = line,
                column = charPositionInLine + 1,
                message = msg,
            )
        )
    }
}
