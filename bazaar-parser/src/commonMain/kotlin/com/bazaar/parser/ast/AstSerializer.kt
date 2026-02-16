package com.bazaar.parser.ast

object AstSerializer {

    fun serialize(file: BazaarFile): String {
        val sb = StringBuilder()
        writeFile(sb, file, 0)
        return sb.toString()
    }

    private fun indent(sb: StringBuilder, level: Int) {
        repeat(level) { sb.append("  ") }
    }

    private fun line(sb: StringBuilder, level: Int, text: String) {
        indent(sb, level)
        sb.appendLine(text)
    }

    // --- Root ---

    private fun writeFile(sb: StringBuilder, file: BazaarFile, level: Int) {
        line(sb, level, "BazaarFile")
        file.packageDecl?.let {
            line(sb, level + 1, "packageDecl:")
            writePackageDecl(sb, it, level + 2)
        }
        if (file.imports.isNotEmpty()) {
            line(sb, level + 1, "imports:")
            file.imports.forEach { writeImportDecl(sb, it, level + 2) }
        }
        if (file.declarations.isNotEmpty()) {
            line(sb, level + 1, "declarations:")
            file.declarations.forEach { writeDecl(sb, it, level + 2) }
        }
    }

    private fun writePackageDecl(sb: StringBuilder, decl: PackageDecl, level: Int) {
        line(sb, level, "PackageDecl")
        writeStringList(sb, "segments", decl.segments, level + 1)
    }

    private fun writeImportDecl(sb: StringBuilder, decl: ImportDecl, level: Int) {
        line(sb, level, "ImportDecl")
        writeStringList(sb, "segments", decl.segments, level + 1)
        decl.alias?.let { line(sb, level + 1, "alias: \"$it\"") }
    }

    // --- Declarations ---

    private fun writeDecl(sb: StringBuilder, decl: Decl, level: Int) {
        when (decl) {
            is EnumDecl -> writeEnumDecl(sb, decl, level)
            is ComponentDecl -> writeComponentDecl(sb, decl, level)
            is DataDecl -> writeDataDecl(sb, decl, level)
            is ModifierDecl -> writeModifierDecl(sb, decl, level)
            is FunctionDecl -> writeFunctionDecl(sb, decl, level)
            is TemplateDecl -> writeTemplateDecl(sb, decl, level)
            is PreviewDecl -> writePreviewDecl(sb, decl, level)
        }
    }

    private fun writeEnumDecl(sb: StringBuilder, decl: EnumDecl, level: Int) {
        line(sb, level, "EnumDecl")
        line(sb, level + 1, "name: \"${decl.name}\"")
        writeStringList(sb, "values", decl.values, level + 1)
    }

    private fun writeComponentDecl(sb: StringBuilder, decl: ComponentDecl, level: Int) {
        line(sb, level, "ComponentDecl")
        line(sb, level + 1, "name: \"${decl.name}\"")
        writeMembers(sb, decl.members, level + 1)
    }

    private fun writeDataDecl(sb: StringBuilder, decl: DataDecl, level: Int) {
        line(sb, level, "DataDecl")
        line(sb, level + 1, "name: \"${decl.name}\"")
        writeMembers(sb, decl.members, level + 1)
    }

    private fun writeModifierDecl(sb: StringBuilder, decl: ModifierDecl, level: Int) {
        line(sb, level, "ModifierDecl")
        line(sb, level + 1, "name: \"${decl.name}\"")
        writeMembers(sb, decl.members, level + 1)
    }

    private fun writeFunctionDecl(sb: StringBuilder, decl: FunctionDecl, level: Int) {
        line(sb, level, "FunctionDecl")
        line(sb, level + 1, "name: \"${decl.name}\"")
        writeParams(sb, decl.params, level + 1)
        decl.returnType?.let {
            line(sb, level + 1, "returnType:")
            writeTypeDecl(sb, it, level + 2)
        }
        decl.body?.let { body ->
            if (body.isNotEmpty()) {
                line(sb, level + 1, "body:")
                body.forEach { writeStmt(sb, it, level + 2) }
            }
        }
    }

    private fun writeTemplateDecl(sb: StringBuilder, decl: TemplateDecl, level: Int) {
        line(sb, level, "TemplateDecl")
        line(sb, level + 1, "name: \"${decl.name}\"")
        writeParams(sb, decl.params, level + 1)
        if (decl.body.isNotEmpty()) {
            line(sb, level + 1, "body:")
            decl.body.forEach { writeStmt(sb, it, level + 2) }
        }
    }

    private fun writePreviewDecl(sb: StringBuilder, decl: PreviewDecl, level: Int) {
        line(sb, level, "PreviewDecl")
        line(sb, level + 1, "name: \"${decl.name}\"")
        if (decl.body.isNotEmpty()) {
            line(sb, level + 1, "body:")
            decl.body.forEach { writeStmt(sb, it, level + 2) }
        }
    }

    // --- Members ---

    private fun writeMembers(sb: StringBuilder, members: List<MemberDecl>, level: Int) {
        if (members.isEmpty()) return
        line(sb, level, "members:")
        members.forEach { writeMemberDecl(sb, it, level + 1) }
    }

    private fun writeMemberDecl(sb: StringBuilder, member: MemberDecl, level: Int) {
        when (member) {
            is FieldDecl -> writeFieldDecl(sb, member, level)
            is ConstructorDecl -> writeConstructorDecl(sb, member, level)
        }
    }

    private fun writeFieldDecl(sb: StringBuilder, decl: FieldDecl, level: Int) {
        line(sb, level, "FieldDecl")
        line(sb, level + 1, "name: \"${decl.name}\"")
        line(sb, level + 1, "type:")
        writeTypeDecl(sb, decl.type, level + 2)
        decl.default?.let {
            line(sb, level + 1, "default:")
            writeExpr(sb, it, level + 2)
        }
    }

    private fun writeConstructorDecl(sb: StringBuilder, decl: ConstructorDecl, level: Int) {
        line(sb, level, "ConstructorDecl")
        writeParams(sb, decl.params, level + 1)
        line(sb, level + 1, "value:")
        writeExpr(sb, decl.value, level + 2)
    }

    // --- Parameters ---

    private fun writeParams(sb: StringBuilder, params: List<ParameterDecl>, level: Int) {
        if (params.isEmpty()) return
        line(sb, level, "params:")
        params.forEach { writeParameterDecl(sb, it, level + 1) }
    }

    private fun writeParameterDecl(sb: StringBuilder, decl: ParameterDecl, level: Int) {
        line(sb, level, "ParameterDecl")
        line(sb, level + 1, "name: \"${decl.name}\"")
        line(sb, level + 1, "type:")
        writeTypeDecl(sb, decl.type, level + 2)
        decl.default?.let {
            line(sb, level + 1, "default:")
            writeExpr(sb, it, level + 2)
        }
    }

    // --- Types ---

    private fun writeTypeDecl(sb: StringBuilder, type: TypeDecl, level: Int) {
        when (type) {
            is ValueType -> {
                line(sb, level, "ValueType")
                line(sb, level + 1, "name: \"${type.name}\"")
                if (type.nullable) line(sb, level + 1, "nullable: true")
            }
            is FunctionType -> {
                line(sb, level, "FunctionType")
                if (type.paramTypes.isNotEmpty()) {
                    line(sb, level + 1, "paramTypes:")
                    type.paramTypes.forEach { writeTypeDecl(sb, it, level + 2) }
                }
                type.returnType?.let {
                    line(sb, level + 1, "returnType:")
                    writeTypeDecl(sb, it, level + 2)
                }
                if (type.nullable) line(sb, level + 1, "nullable: true")
            }
            is ArrayType -> {
                line(sb, level, "ArrayType")
                line(sb, level + 1, "elementType:")
                writeTypeDecl(sb, type.elementType, level + 2)
                if (type.nullable) line(sb, level + 1, "nullable: true")
            }
            is MapType -> {
                line(sb, level, "MapType")
                line(sb, level + 1, "keyType:")
                writeTypeDecl(sb, type.keyType, level + 2)
                line(sb, level + 1, "valueType:")
                writeTypeDecl(sb, type.valueType, level + 2)
                if (type.nullable) line(sb, level + 1, "nullable: true")
            }
        }
    }

    // --- Expressions ---

    private fun writeExpr(sb: StringBuilder, expr: Expr, level: Int) {
        when (expr) {
            is BinaryExpr -> writeBinaryExpr(sb, expr, level)
            is UnaryExpr -> writeUnaryExpr(sb, expr, level)
            is ReferenceExpr -> writeReferenceExpr(sb, expr, level)
            is MemberExpr -> writeMemberExpr(sb, expr, level)
            is IndexExpr -> writeIndexExpr(sb, expr, level)
            is CallExpr -> writeCallExpr(sb, expr, level)
            is LambdaExpr -> writeLambdaExpr(sb, expr, level)
            is NullLiteral -> line(sb, level, "NullLiteral")
            is BoolLiteral -> writeBoolLiteral(sb, expr, level)
            is NumberLiteral -> writeNumberLiteral(sb, expr, level)
            is StringLiteral -> writeStringLiteral(sb, expr, level)
            is ArrayLiteral -> writeArrayLiteral(sb, expr, level)
            is MapLiteral -> writeMapLiteral(sb, expr, level)
        }
    }

    private fun writeBinaryExpr(sb: StringBuilder, expr: BinaryExpr, level: Int) {
        line(sb, level, "BinaryExpr")
        line(sb, level + 1, "op: ${expr.op.name}")
        line(sb, level + 1, "left:")
        writeExpr(sb, expr.left, level + 2)
        line(sb, level + 1, "right:")
        writeExpr(sb, expr.right, level + 2)
    }

    private fun writeUnaryExpr(sb: StringBuilder, expr: UnaryExpr, level: Int) {
        line(sb, level, "UnaryExpr")
        line(sb, level + 1, "op: ${expr.op.name}")
        line(sb, level + 1, "operand:")
        writeExpr(sb, expr.operand, level + 2)
    }

    private fun writeReferenceExpr(sb: StringBuilder, expr: ReferenceExpr, level: Int) {
        line(sb, level, "ReferenceExpr")
        line(sb, level + 1, "name: \"${expr.name}\"")
    }

    private fun writeMemberExpr(sb: StringBuilder, expr: MemberExpr, level: Int) {
        line(sb, level, "MemberExpr")
        line(sb, level + 1, "target:")
        writeExpr(sb, expr.target, level + 2)
        line(sb, level + 1, "member: \"${expr.member}\"")
        if (expr.optional) line(sb, level + 1, "optional: true")
    }

    private fun writeIndexExpr(sb: StringBuilder, expr: IndexExpr, level: Int) {
        line(sb, level, "IndexExpr")
        line(sb, level + 1, "target:")
        writeExpr(sb, expr.target, level + 2)
        line(sb, level + 1, "index:")
        writeExpr(sb, expr.index, level + 2)
        if (expr.optional) line(sb, level + 1, "optional: true")
    }

    private fun writeCallExpr(sb: StringBuilder, expr: CallExpr, level: Int) {
        line(sb, level, "CallExpr")
        line(sb, level + 1, "target:")
        writeExpr(sb, expr.target, level + 2)
        if (expr.args.isNotEmpty()) {
            line(sb, level + 1, "args:")
            expr.args.forEach { writeArgument(sb, it, level + 2) }
        }
        expr.trailingLambda?.let {
            line(sb, level + 1, "trailingLambda:")
            writeLambdaExpr(sb, it, level + 2)
        }
        if (expr.optional) line(sb, level + 1, "optional: true")
    }

    private fun writeLambdaExpr(sb: StringBuilder, expr: LambdaExpr, level: Int) {
        line(sb, level, "LambdaExpr")
        if (expr.params.isNotEmpty()) {
            line(sb, level + 1, "params:")
            expr.params.forEach { writeLambdaParam(sb, it, level + 2) }
        }
        expr.returnType?.let {
            line(sb, level + 1, "returnType:")
            writeTypeDecl(sb, it, level + 2)
        }
        if (expr.body.isNotEmpty()) {
            line(sb, level + 1, "body:")
            expr.body.forEach { writeStmt(sb, it, level + 2) }
        }
    }

    private fun writeBoolLiteral(sb: StringBuilder, expr: BoolLiteral, level: Int) {
        line(sb, level, "BoolLiteral")
        line(sb, level + 1, "value: ${expr.value}")
    }

    private fun writeNumberLiteral(sb: StringBuilder, expr: NumberLiteral, level: Int) {
        line(sb, level, "NumberLiteral")
        line(sb, level + 1, "value: \"${expr.value}\"")
    }

    private fun writeStringLiteral(sb: StringBuilder, expr: StringLiteral, level: Int) {
        line(sb, level, "StringLiteral")
        if (expr.parts.isNotEmpty()) {
            line(sb, level + 1, "parts:")
            expr.parts.forEach { writeStringPart(sb, it, level + 2) }
        }
    }

    private fun writeArrayLiteral(sb: StringBuilder, expr: ArrayLiteral, level: Int) {
        line(sb, level, "ArrayLiteral")
        if (expr.elements.isNotEmpty()) {
            line(sb, level + 1, "elements:")
            expr.elements.forEach { writeExpr(sb, it, level + 2) }
        }
    }

    private fun writeMapLiteral(sb: StringBuilder, expr: MapLiteral, level: Int) {
        line(sb, level, "MapLiteral")
        if (expr.entries.isNotEmpty()) {
            line(sb, level + 1, "entries:")
            expr.entries.forEach { writeMapEntry(sb, it, level + 2) }
        }
    }

    // --- String parts ---

    private fun writeStringPart(sb: StringBuilder, part: StringPart, level: Int) {
        when (part) {
            is TextPart -> {
                line(sb, level, "TextPart")
                line(sb, level + 1, "text: \"${part.text}\"")
            }
            is EscapePart -> {
                line(sb, level, "EscapePart")
                line(sb, level + 1, "text: \"${part.text}\"")
            }
            is InterpolationPart -> {
                line(sb, level, "InterpolationPart")
                line(sb, level + 1, "expr:")
                writeExpr(sb, part.expr, level + 2)
            }
        }
    }

    // --- Support types ---

    private fun writeArgument(sb: StringBuilder, arg: Argument, level: Int) {
        line(sb, level, "Argument")
        arg.name?.let { line(sb, level + 1, "name: \"$it\"") }
        line(sb, level + 1, "value:")
        writeExpr(sb, arg.value, level + 2)
    }

    private fun writeLambdaParam(sb: StringBuilder, param: LambdaParam, level: Int) {
        line(sb, level, "LambdaParam")
        line(sb, level + 1, "name: \"${param.name}\"")
        param.type?.let {
            line(sb, level + 1, "type:")
            writeTypeDecl(sb, it, level + 2)
        }
    }

    private fun writeMapEntry(sb: StringBuilder, entry: MapEntry, level: Int) {
        line(sb, level, "MapEntry")
        line(sb, level + 1, "key:")
        writeExpr(sb, entry.key, level + 2)
        line(sb, level + 1, "value:")
        writeExpr(sb, entry.value, level + 2)
    }

    private fun writeAnnotation(sb: StringBuilder, annotation: Annotation, level: Int) {
        line(sb, level, "Annotation")
        line(sb, level + 1, "name: \"${annotation.name}\"")
        annotation.args?.let { args ->
            if (args.isNotEmpty()) {
                line(sb, level + 1, "args:")
                args.forEach { writeArgument(sb, it, level + 2) }
            }
        }
    }

    // --- Statements ---

    private fun writeStmt(sb: StringBuilder, stmt: Stmt, level: Int) {
        when (stmt) {
            is VarDeclStmt -> writeVarDeclStmt(sb, stmt, level)
            is AssignStmt -> writeAssignStmt(sb, stmt, level)
            is CallStmt -> writeCallStmt(sb, stmt, level)
            is ReturnStmt -> writeReturnStmt(sb, stmt, level)
            is ExprStmt -> writeExprStmt(sb, stmt, level)
            is IfStmt -> writeIfStmt(sb, stmt, level)
            is ForInStmt -> writeForInStmt(sb, stmt, level)
            is ForCondStmt -> writeForCondStmt(sb, stmt, level)
            is SwitchStmt -> writeSwitchStmt(sb, stmt, level)
        }
    }

    private fun writeVarDeclStmt(sb: StringBuilder, stmt: VarDeclStmt, level: Int) {
        line(sb, level, "VarDeclStmt")
        writeStringList(sb, "names", stmt.names, level + 1)
        stmt.type?.let {
            line(sb, level + 1, "type:")
            writeTypeDecl(sb, it, level + 2)
        }
        line(sb, level + 1, "value:")
        writeExpr(sb, stmt.value, level + 2)
        if (stmt.annotations.isNotEmpty()) {
            line(sb, level + 1, "annotations:")
            stmt.annotations.forEach { writeAnnotation(sb, it, level + 2) }
        }
    }

    private fun writeAssignStmt(sb: StringBuilder, stmt: AssignStmt, level: Int) {
        line(sb, level, "AssignStmt")
        line(sb, level + 1, "target: \"${stmt.target}\"")
        line(sb, level + 1, "op: ${stmt.op.name}")
        line(sb, level + 1, "value:")
        writeExpr(sb, stmt.value, level + 2)
    }

    private fun writeCallStmt(sb: StringBuilder, stmt: CallStmt, level: Int) {
        line(sb, level, "CallStmt")
        if (stmt.annotations.isNotEmpty()) {
            line(sb, level + 1, "annotations:")
            stmt.annotations.forEach { writeAnnotation(sb, it, level + 2) }
        }
        line(sb, level + 1, "expr:")
        writeCallExpr(sb, stmt.expr, level + 2)
    }

    private fun writeReturnStmt(sb: StringBuilder, stmt: ReturnStmt, level: Int) {
        line(sb, level, "ReturnStmt")
        stmt.value?.let {
            line(sb, level + 1, "value:")
            writeExpr(sb, it, level + 2)
        }
    }

    private fun writeExprStmt(sb: StringBuilder, stmt: ExprStmt, level: Int) {
        line(sb, level, "ExprStmt")
        line(sb, level + 1, "expr:")
        writeExpr(sb, stmt.expr, level + 2)
    }

    // --- If ---

    private fun writeIfStmt(sb: StringBuilder, stmt: IfStmt, level: Int) {
        line(sb, level, "IfStmt")
        line(sb, level + 1, "fragments:")
        stmt.fragments.forEach { writeIfFragment(sb, it, level + 2) }
        if (stmt.body.isNotEmpty()) {
            line(sb, level + 1, "body:")
            stmt.body.forEach { writeStmt(sb, it, level + 2) }
        }
        if (stmt.elseIfs.isNotEmpty()) {
            line(sb, level + 1, "elseIfs:")
            stmt.elseIfs.forEach { writeElseIf(sb, it, level + 2) }
        }
        stmt.elseBody?.let { body ->
            if (body.isNotEmpty()) {
                line(sb, level + 1, "elseBody:")
                body.forEach { writeStmt(sb, it, level + 2) }
            }
        }
    }

    private fun writeElseIf(sb: StringBuilder, elseIf: ElseIf, level: Int) {
        line(sb, level, "ElseIf")
        line(sb, level + 1, "fragments:")
        elseIf.fragments.forEach { writeIfFragment(sb, it, level + 2) }
        if (elseIf.body.isNotEmpty()) {
            line(sb, level + 1, "body:")
            elseIf.body.forEach { writeStmt(sb, it, level + 2) }
        }
    }

    private fun writeIfFragment(sb: StringBuilder, fragment: IfFragment, level: Int) {
        when (fragment) {
            is IfVarFragment -> {
                line(sb, level, "IfVarFragment")
                writeStringList(sb, "names", fragment.names, level + 1)
                fragment.type?.let {
                    line(sb, level + 1, "type:")
                    writeTypeDecl(sb, it, level + 2)
                }
                fragment.value?.let {
                    line(sb, level + 1, "value:")
                    writeExpr(sb, it, level + 2)
                }
            }
            is IfExprFragment -> {
                line(sb, level, "IfExprFragment")
                line(sb, level + 1, "expr:")
                writeExpr(sb, fragment.expr, level + 2)
            }
        }
    }

    // --- For ---

    private fun writeForInStmt(sb: StringBuilder, stmt: ForInStmt, level: Int) {
        line(sb, level, "ForInStmt")
        writeStringList(sb, "names", stmt.names, level + 1)
        line(sb, level + 1, "iterable:")
        writeExpr(sb, stmt.iterable, level + 2)
        if (stmt.body.isNotEmpty()) {
            line(sb, level + 1, "body:")
            stmt.body.forEach { writeStmt(sb, it, level + 2) }
        }
    }

    private fun writeForCondStmt(sb: StringBuilder, stmt: ForCondStmt, level: Int) {
        line(sb, level, "ForCondStmt")
        line(sb, level + 1, "condition:")
        writeExpr(sb, stmt.condition, level + 2)
        if (stmt.body.isNotEmpty()) {
            line(sb, level + 1, "body:")
            stmt.body.forEach { writeStmt(sb, it, level + 2) }
        }
    }

    // --- Switch ---

    private fun writeSwitchStmt(sb: StringBuilder, stmt: SwitchStmt, level: Int) {
        line(sb, level, "SwitchStmt")
        line(sb, level + 1, "expr:")
        writeExpr(sb, stmt.expr, level + 2)
        if (stmt.cases.isNotEmpty()) {
            line(sb, level + 1, "cases:")
            stmt.cases.forEach { writeSwitchCase(sb, it, level + 2) }
        }
        stmt.default?.let { body ->
            if (body.isNotEmpty()) {
                line(sb, level + 1, "default:")
                body.forEach { writeStmt(sb, it, level + 2) }
            }
        }
    }

    private fun writeSwitchCase(sb: StringBuilder, case: SwitchCase, level: Int) {
        line(sb, level, "SwitchCase")
        line(sb, level + 1, "expr:")
        writeExpr(sb, case.expr, level + 2)
        if (case.body.isNotEmpty()) {
            line(sb, level + 1, "body:")
            case.body.forEach { writeStmt(sb, it, level + 2) }
        }
    }

    // --- Helpers ---

    private fun writeStringList(sb: StringBuilder, name: String, values: List<String>, level: Int) {
        if (values.isEmpty()) return
        val quoted = values.joinToString(", ") { "\"$it\"" }
        line(sb, level, "$name: [$quoted]")
    }
}
