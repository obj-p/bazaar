package com.bazaar.sema.ir

import com.bazaar.parser.ast.Expr
import com.bazaar.parser.ast.Stmt

data class IrFile(
    val packageName: String?,
    val declarations: List<IrDeclaration>,
)

sealed interface IrDeclaration {
    val name: String
}

data class IrComponent(
    override val name: String,
    val fields: List<IrField>,
    val constructors: List<IrConstructor>,
) : IrDeclaration

data class IrData(
    override val name: String,
    val fields: List<IrField>,
    val constructors: List<IrConstructor>,
) : IrDeclaration

data class IrModifier(
    override val name: String,
    val fields: List<IrField>,
    val constructors: List<IrConstructor>,
) : IrDeclaration

data class IrEnum(
    override val name: String,
    val values: List<String>,
) : IrDeclaration

data class IrFunction(
    override val name: String,
    val params: List<IrParam>,
    val returnType: IrType?,
    val body: List<Stmt>?,
) : IrDeclaration

data class IrTemplate(
    override val name: String,
    val params: List<IrParam>,
    val body: List<Stmt>,
) : IrDeclaration

data class IrPreview(
    override val name: String,
    val body: List<Stmt>,
) : IrDeclaration

data class IrField(
    val name: String,
    val type: IrType,
    val default: Expr?,
)

data class IrParam(
    val name: String,
    val type: IrType,
    val default: Expr?,
)

data class IrConstructor(
    val params: List<IrParam>,
    val value: Expr,
)
