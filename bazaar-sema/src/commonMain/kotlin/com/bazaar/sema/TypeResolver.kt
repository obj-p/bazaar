package com.bazaar.sema

import com.bazaar.parser.ast.*
import com.bazaar.sema.ir.*

object TypeResolver {

    fun resolve(file: BazaarFile, symbolTable: SymbolTable): ResolutionResult {
        val diagnostics = mutableListOf<SemaDiagnostic>()
        val ctx = ResolveContext(symbolTable, diagnostics)

        val irDeclarations = file.declarations.map { ctx.resolveDecl(it) }

        val packageName = file.packageDecl?.segments?.joinToString(".")

        return ResolutionResult(
            ir = IrFile(packageName, irDeclarations),
            diagnostics = diagnostics,
        )
    }

    private class ResolveContext(
        private val symbolTable: SymbolTable,
        private val diagnostics: MutableList<SemaDiagnostic>,
    ) {
        private var currentDecl: String? = null
        private var currentMember: String? = null

        fun resolveDecl(decl: Decl): IrDeclaration {
            currentDecl = declName(decl)
            currentMember = null
            return when (decl) {
                is ComponentDecl -> IrComponent(
                    name = decl.name,
                    fields = decl.members.filterIsInstance<FieldDecl>().map { resolveField(it) },
                    constructors = decl.members.filterIsInstance<ConstructorDecl>().map { resolveConstructor(it) },
                )
                is DataDecl -> IrData(
                    name = decl.name,
                    fields = decl.members.filterIsInstance<FieldDecl>().map { resolveField(it) },
                    constructors = decl.members.filterIsInstance<ConstructorDecl>().map { resolveConstructor(it) },
                )
                is ModifierDecl -> IrModifier(
                    name = decl.name,
                    fields = decl.members.filterIsInstance<FieldDecl>().map { resolveField(it) },
                    constructors = decl.members.filterIsInstance<ConstructorDecl>().map { resolveConstructor(it) },
                )
                is EnumDecl -> IrEnum(
                    name = decl.name,
                    values = decl.values,
                )
                is FunctionDecl -> IrFunction(
                    name = decl.name,
                    params = decl.params.map { resolveParam(it) },
                    returnType = decl.returnType?.let { resolveType(it) },
                    body = decl.body,
                )
                is TemplateDecl -> IrTemplate(
                    name = decl.name,
                    params = decl.params.map { resolveParam(it) },
                    body = decl.body,
                )
                is PreviewDecl -> IrPreview(
                    name = decl.name,
                    body = decl.body,
                )
            }
        }

        private fun resolveField(field: FieldDecl): IrField {
            currentMember = field.name
            return IrField(
                name = field.name,
                type = resolveType(field.type),
                default = field.default,
            )
        }

        private fun resolveParam(param: ParameterDecl): IrParam {
            currentMember = param.name
            return IrParam(
                name = param.name,
                type = resolveType(param.type),
                default = param.default,
            )
        }

        private fun resolveConstructor(ctor: ConstructorDecl): IrConstructor {
            currentMember = "constructor"
            return IrConstructor(
                params = ctor.params.map { resolveParam(it) },
                value = ctor.value,
            )
        }

        fun resolveType(typeDecl: TypeDecl): IrType = when (typeDecl) {
            is ValueType -> resolveValueType(typeDecl)
            is ArrayType -> IrArrayType(
                elementType = resolveType(typeDecl.elementType),
                nullable = typeDecl.nullable,
            )
            is MapType -> IrMapType(
                keyType = resolveType(typeDecl.keyType),
                valueType = resolveType(typeDecl.valueType),
                nullable = typeDecl.nullable,
            )
            is FunctionType -> IrFunctionType(
                paramTypes = typeDecl.paramTypes.map { resolveType(it) },
                returnType = typeDecl.returnType?.let { resolveType(it) },
                nullable = typeDecl.nullable,
            )
        }

        private fun resolveValueType(type: ValueType): IrType {
            val primitiveKind = BuiltinTypes.kindOf(type.name)
            if (primitiveKind != null) {
                return IrBuiltinType(type.name, primitiveKind, type.nullable)
            }

            val symbol = symbolTable.lookup(type.name)
            if (symbol != null) {
                return IrDeclaredType(type.name, symbol.kind, type.nullable)
            }

            val context = buildString {
                append("undefined type '${type.name}'")
                if (currentMember != null && currentDecl != null) {
                    append(" in '$currentMember' of '$currentDecl'")
                }
            }
            diagnostics += SemaDiagnostic(SemaSeverity.ERROR, context)
            return IrErrorType(type.name, type.nullable)
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
    }
}

data class ResolutionResult(
    val ir: IrFile,
    val diagnostics: List<SemaDiagnostic>,
)
