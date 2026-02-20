package com.bazaar.sema

data class SemaDiagnostic(val severity: SemaSeverity, val message: String)

enum class SemaSeverity { ERROR, WARNING }
