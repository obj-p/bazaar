package com.bazaar.sema.ir

import com.bazaar.sema.SymbolKind

sealed interface IrType {
    val nullable: Boolean
}

enum class IrPrimitiveKind { INT, DOUBLE, STRING, BOOL, COMPONENT }

data class IrBuiltinType(
    val name: String,
    val kind: IrPrimitiveKind,
    override val nullable: Boolean = false,
) : IrType

data class IrDeclaredType(
    val name: String,
    val symbolKind: SymbolKind,
    override val nullable: Boolean = false,
) : IrType

data class IrArrayType(
    val elementType: IrType,
    override val nullable: Boolean = false,
) : IrType

data class IrMapType(
    val keyType: IrType,
    val valueType: IrType,
    override val nullable: Boolean = false,
) : IrType

data class IrFunctionType(
    val paramTypes: List<IrType>,
    val returnType: IrType?,
    override val nullable: Boolean = false,
) : IrType
