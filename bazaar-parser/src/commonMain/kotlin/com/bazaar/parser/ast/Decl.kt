package com.bazaar.parser.ast

// --- Root ---

data class BazaarFile(
    val packageDecl: PackageDecl? = null,
    val imports: List<ImportDecl> = emptyList(),
    val declarations: List<Decl> = emptyList(),
)

// --- Package / Import ---

data class PackageDecl(
    val segments: List<String>,
)

data class ImportDecl(
    val segments: List<String>,
    val alias: String? = null,
)

// --- Declarations ---

sealed interface Decl

data class EnumDecl(
    val name: String,
    val values: List<String>,
) : Decl

data class ComponentDecl(
    val name: String,
    val members: List<MemberDecl> = emptyList(),
) : Decl

data class DataDecl(
    val name: String,
    val members: List<MemberDecl> = emptyList(),
) : Decl

data class ModifierDecl(
    val name: String,
    val members: List<MemberDecl> = emptyList(),
) : Decl

data class FunctionDecl(
    val name: String,
    val params: List<ParameterDecl> = emptyList(),
    val returnType: TypeDecl? = null,
    val body: List<Stmt>? = null,
) : Decl

data class TemplateDecl(
    val name: String,
    val params: List<ParameterDecl> = emptyList(),
    val body: List<Stmt> = emptyList(),
) : Decl

data class PreviewDecl(
    val name: String,
    val body: List<Stmt> = emptyList(),
) : Decl

// --- Members ---

sealed interface MemberDecl

data class FieldDecl(
    val name: String,
    val type: TypeDecl,
    val default: Expr? = null,
) : MemberDecl

data class ConstructorDecl(
    val params: List<ParameterDecl> = emptyList(),
    val value: Expr,
) : MemberDecl

// --- Parameters ---

data class ParameterDecl(
    val name: String,
    val type: TypeDecl,
    val default: Expr? = null,
)
