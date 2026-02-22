package com.bazaar.parser.ast

sealed interface TypeDecl {
    val nullable: Boolean
}

data class ValueType(
    val name: String,
    override val nullable: Boolean = false,
) : TypeDecl

data class FunctionType(
    val paramTypes: List<TypeDecl>,
    val returnType: TypeDecl? = null,
    override val nullable: Boolean = false,
) : TypeDecl

data class ArrayType(
    val elementType: TypeDecl,
    override val nullable: Boolean = false,
) : TypeDecl

data class MapType(
    val keyType: TypeDecl,
    val valueType: TypeDecl,
    override val nullable: Boolean = false,
) : TypeDecl
