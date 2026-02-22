package com.bazaar.cli

enum class OutputFormat { TEXT, JSON }

data class CliArgs(
    val files: List<String>,
    val format: OutputFormat = OutputFormat.TEXT,
    val check: Boolean = false,
)

sealed class ArgsResult {
    data class Success(
        val args: CliArgs,
    ) : ArgsResult()

    data class Error(
        val message: String,
    ) : ArgsResult()

    data object Help : ArgsResult()
}

fun parseArgs(args: Array<String>): ArgsResult {
    var format = OutputFormat.TEXT
    var check = false
    val files = mutableListOf<String>()

    var i = 0
    while (i < args.size) {
        when (args[i]) {
            "--help", "-h" -> return ArgsResult.Help
            "--check" -> check = true
            "--format" -> {
                i++
                if (i >= args.size) {
                    return ArgsResult.Error("--format requires an argument (text or json)")
                }
                format =
                    when (args[i].lowercase()) {
                        "text" -> OutputFormat.TEXT
                        "json" -> OutputFormat.JSON
                        else -> return ArgsResult.Error("Unknown format: ${args[i]}. Expected 'text' or 'json'.")
                    }
            }
            else -> {
                if (args[i].startsWith("-")) {
                    return ArgsResult.Error("Unknown option: ${args[i]}")
                }
                files.add(args[i])
            }
        }
        i++
    }

    if (files.isEmpty()) return ArgsResult.Error("No input files specified.")

    return ArgsResult.Success(CliArgs(files, format, check))
}
