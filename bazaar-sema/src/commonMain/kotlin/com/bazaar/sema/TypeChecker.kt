package com.bazaar.sema

import com.bazaar.parser.ast.*
import com.bazaar.sema.ir.*

data class TypeCheckResult(val diagnostics: List<SemaDiagnostic>)

object TypeChecker {

    fun check(ir: IrFile, symbolTable: SymbolTable): TypeCheckResult {
        val ctx = CheckContext(symbolTable)
        for (decl in ir.declarations) {
            ctx.checkDeclaration(decl)
        }
        return TypeCheckResult(ctx.diagnostics)
    }

    private class CheckContext(private val symbolTable: SymbolTable) {
        val diagnostics = mutableListOf<SemaDiagnostic>()

        fun checkDeclaration(decl: IrDeclaration) {
            when (decl) {
                is IrComponent -> {
                    checkFieldDefaults(decl.name, decl.fields)
                    checkConstructors(decl.name, decl.fields, decl.constructors)
                }
                is IrData -> {
                    checkFieldDefaults(decl.name, decl.fields)
                    checkConstructors(decl.name, decl.fields, decl.constructors)
                }
                is IrModifier -> {
                    checkFieldDefaults(decl.name, decl.fields)
                    checkConstructors(decl.name, decl.fields, decl.constructors)
                }
                is IrFunction -> checkParamDefaults(decl.name, decl.params)
                is IrTemplate -> checkParamDefaults(decl.name, decl.params)
                is IrEnum, is IrPreview -> { /* nothing to check */ }
            }
        }

        private fun checkFieldDefaults(declName: String, fields: List<IrField>) {
            for (field in fields) {
                val default = field.default ?: continue
                checkDefault(default, field.type, "field", field.name, declName)
            }
        }

        private fun checkParamDefaults(declName: String, params: List<IrParam>) {
            for (param in params) {
                val default = param.default ?: continue
                checkDefault(default, param.type, "parameter", param.name, declName)
            }
        }

        private fun checkDefault(
            expr: Expr,
            targetType: IrType,
            memberKind: String,
            memberName: String,
            declName: String,
        ) {
            // Check enum value validity first
            if (expr is MemberExpr) {
                val target = expr.target
                if (target is ReferenceExpr) {
                    val symbol = symbolTable.lookup(target.name)
                    if (symbol != null && symbol.kind == SymbolKind.ENUM) {
                        val enumDecl = symbol.decl as EnumDecl
                        if (expr.member !in enumDecl.values) {
                            val valueList = enumDecl.values.joinToString("', '", "'", "'")
                            diagnostics += SemaDiagnostic(
                                SemaSeverity.ERROR,
                                "unknown enum value '${expr.member}' in $memberKind '$memberName' of '$declName': " +
                                    "'${target.name}' has values $valueList",
                            )
                            return
                        }
                    }
                }
            }

            val result = ExprTypeInferrer.infer(expr, symbolTable)
            when (result) {
                is InferResult.NullLiteralResult -> {
                    if (!targetType.nullable) {
                        diagnostics += SemaDiagnostic(
                            SemaSeverity.ERROR,
                            "type mismatch in $memberKind '$memberName' of '$declName': " +
                                "null is not assignable to non-nullable type '${formatType(targetType)}'",
                        )
                    }
                }
                is InferResult.Inferred -> {
                    if (!isAssignable(result.type, targetType)) {
                        diagnostics += SemaDiagnostic(
                            SemaSeverity.ERROR,
                            "type mismatch in $memberKind '$memberName' of '$declName': " +
                                "expected ${formatType(targetType)}, got ${formatType(result.type)}",
                        )
                    }
                }
                is InferResult.Uninferrable -> { /* skip — deferred to Milestone 3 */ }
            }
        }

        private fun checkConstructors(declName: String, fields: List<IrField>, constructors: List<IrConstructor>) {
            for (ctor in constructors) {
                checkConstructor(declName, fields, ctor)
            }
        }

        private fun checkConstructor(declName: String, fields: List<IrField>, ctor: IrConstructor) {
            val value = ctor.value

            // Value must be a CallExpr targeting the declaration itself
            if (value !is CallExpr) return // uninferrable — skip
            val callTarget = value.target
            if (callTarget !is ReferenceExpr) return // uninferrable — skip

            if (callTarget.name != declName) {
                diagnostics += SemaDiagnostic(
                    SemaSeverity.ERROR,
                    "constructor of '$declName' must construct an instance of '$declName', but calls '${callTarget.name}'",
                )
                return
            }

            // Check argument count
            val args = value.args
            if (args.size != fields.size) {
                diagnostics += SemaDiagnostic(
                    SemaSeverity.ERROR,
                    "constructor of '$declName': expected ${fields.size} arguments but got ${args.size}",
                )
                return
            }

            // Build param scope from constructor params
            val paramScope = ctor.params.associate { it.name to it.type }

            // Check each argument type against corresponding field
            for ((index, pair) in args.zip(fields).withIndex()) {
                val (arg, field) = pair
                val argType = resolveArgType(arg.value, paramScope)
                if (argType != null && !isAssignable(argType, field.type)) {
                    diagnostics += SemaDiagnostic(
                        SemaSeverity.ERROR,
                        "constructor of '$declName': argument ${index + 1} has type ${formatType(argType)} " +
                            "but field '${field.name}' expects ${formatType(field.type)}",
                    )
                }
            }
        }

        private fun resolveArgType(expr: Expr, paramScope: Map<String, IrType>): IrType? {
            // If it's a reference to a constructor param, use the param's type
            if (expr is ReferenceExpr) {
                val paramType = paramScope[expr.name]
                if (paramType != null) return paramType
            }
            // Otherwise, infer from expression
            return when (val result = ExprTypeInferrer.infer(expr, symbolTable)) {
                is InferResult.Inferred -> result.type
                else -> null // NullLiteralResult and Uninferrable — skip
            }
        }
    }
}
