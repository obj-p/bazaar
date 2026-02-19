package com.bazaar.cli

import com.bazaar.parser.BazaarParser
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    when (val result = parseArgs(args)) {
        is ArgsResult.Help -> {
            println(usageText())
            exitProcess(0)
        }
        is ArgsResult.Error -> {
            printStderr("Error: ${result.message}")
            printStderr(usageText())
            exitProcess(2)
        }
        is ArgsResult.Success -> exitProcess(run(result.args))
    }
}

private fun usageText(): String =
    """
    |Usage: bazaar-cli [options] <file>...
    |
    |Options:
    |  --format text|json  Output format (default: text)
    |  --check             Check for parse errors only (no output on success)
    |  -h, --help          Show this help message
    """.trimMargin()


private fun run(args: CliArgs): Int {
    val multiFile = args.files.size > 1
    var hasErrors = false
    val jsonResults = if (args.format == OutputFormat.JSON && !args.check) mutableListOf<String>() else null

    for (filePath in args.files) {
        val source = try {
            SystemFileSystem.source(Path(filePath)).buffered().use { it.readString() }
        } catch (e: Exception) {
            printStderr("Error: Cannot read file: $filePath (${e.message})")
            hasErrors = true
            continue
        }

        val result = BazaarParser.parseWithDiagnostics(source)

        if (result.hasErrors) {
            hasErrors = true
        }

        if (args.check) {
            if (result.hasErrors) {
                printStderr("$filePath: parse errors found")
                for (diag in result.diagnostics) {
                    printStderr("  ${diag.severity} ${diag.line}:${diag.column} ${diag.message}")
                }
            }
        } else {
            when (args.format) {
                OutputFormat.TEXT -> print(formatTextResult(filePath, result, multiFile))
                OutputFormat.JSON -> jsonResults!!.add(formatJsonResult(filePath, result))
            }
        }
    }

    if (jsonResults != null) {
        if (multiFile) {
            println("[")
            jsonResults.forEachIndexed { index, json ->
                print(json)
                if (index < jsonResults.size - 1) println(",") else println()
            }
            println("]")
        } else {
            jsonResults.forEach { println(it) }
        }
    }

    return if (hasErrors) 1 else 0
}
