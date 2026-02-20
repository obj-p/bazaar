package com.bazaar.sema

import com.bazaar.parser.ast.*
import com.bazaar.sema.ir.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ExprTypeInferrerTest {

    private val emptySymbols = SymbolTable()

    private fun symbolsWithEnum(name: String, values: List<String>): SymbolTable {
        val st = SymbolTable()
        st.define(Symbol(name, SymbolKind.ENUM, EnumDecl(name, values)))
        return st
    }

    private fun assertInferred(expected: IrType, expr: Expr, symbolTable: SymbolTable = emptySymbols) {
        val result = ExprTypeInferrer.infer(expr, symbolTable)
        val inferred = assertIs<InferResult.Inferred>(result)
        assertEquals(expected, inferred.type)
    }

    @Test
    fun infersIntegerLiteral() {
        assertInferred(IrBuiltinType("int", IrPrimitiveKind.INT), NumberLiteral("42"))
    }

    @Test
    fun infersNegativeInteger() {
        assertInferred(IrBuiltinType("int", IrPrimitiveKind.INT), NumberLiteral("0"))
    }

    @Test
    fun infersDoubleLiteralWithDot() {
        assertInferred(IrBuiltinType("double", IrPrimitiveKind.DOUBLE), NumberLiteral("3.14"))
    }

    @Test
    fun infersDoubleLiteralWithExponent() {
        assertInferred(IrBuiltinType("double", IrPrimitiveKind.DOUBLE), NumberLiteral("1e10"))
    }

    @Test
    fun infersDoubleLiteralWithUpperExponent() {
        assertInferred(IrBuiltinType("double", IrPrimitiveKind.DOUBLE), NumberLiteral("2.5E3"))
    }

    @Test
    fun infersStringLiteral() {
        assertInferred(
            IrBuiltinType("string", IrPrimitiveKind.STRING),
            StringLiteral(listOf(TextPart("hello"))),
        )
    }

    @Test
    fun infersBoolLiteralTrue() {
        assertInferred(IrBuiltinType("bool", IrPrimitiveKind.BOOL), BoolLiteral(true))
    }

    @Test
    fun infersBoolLiteralFalse() {
        assertInferred(IrBuiltinType("bool", IrPrimitiveKind.BOOL), BoolLiteral(false))
    }

    @Test
    fun infersNullLiteral() {
        val result = ExprTypeInferrer.infer(NullLiteral, emptySymbols)
        assertIs<InferResult.NullLiteralResult>(result)
    }

    @Test
    fun infersEmptyArray() {
        assertInferred(IrArrayType(IrErrorType("unknown")), ArrayLiteral(emptyList()))
    }

    @Test
    fun infersNonEmptyArray() {
        assertInferred(
            IrArrayType(IrBuiltinType("int", IrPrimitiveKind.INT)),
            ArrayLiteral(listOf(NumberLiteral("1"), NumberLiteral("2"))),
        )
    }

    @Test
    fun infersEmptyMap() {
        assertInferred(
            IrMapType(IrErrorType("unknown"), IrErrorType("unknown")),
            MapLiteral(emptyList()),
        )
    }

    @Test
    fun infersNonEmptyMap() {
        assertInferred(
            IrMapType(
                IrBuiltinType("string", IrPrimitiveKind.STRING),
                IrBuiltinType("int", IrPrimitiveKind.INT),
            ),
            MapLiteral(listOf(
                MapEntry(StringLiteral(listOf(TextPart("key"))), NumberLiteral("1")),
            )),
        )
    }

    @Test
    fun infersEnumMemberReference() {
        val symbols = symbolsWithEnum("Color", listOf("red", "green", "blue"))
        assertInferred(
            IrDeclaredType("Color", SymbolKind.ENUM),
            MemberExpr(ReferenceExpr("Color"), "red"),
            symbols,
        )
    }

    @Test
    fun returnsUninferrableForUnknownMemberTarget() {
        val result = ExprTypeInferrer.infer(
            MemberExpr(ReferenceExpr("Unknown"), "value"),
            emptySymbols,
        )
        assertIs<InferResult.Uninferrable>(result)
    }

    @Test
    fun infersUnaryNegateNumber() {
        assertInferred(
            IrBuiltinType("int", IrPrimitiveKind.INT),
            UnaryExpr(UnaryOp.NEGATE, NumberLiteral("5")),
        )
    }

    @Test
    fun infersUnaryNegateDouble() {
        assertInferred(
            IrBuiltinType("double", IrPrimitiveKind.DOUBLE),
            UnaryExpr(UnaryOp.NEGATE, NumberLiteral("3.14")),
        )
    }

    @Test
    fun infersUnaryNotBool() {
        assertInferred(
            IrBuiltinType("bool", IrPrimitiveKind.BOOL),
            UnaryExpr(UnaryOp.NOT, BoolLiteral(true)),
        )
    }

    @Test
    fun returnsUninferrableForComplexExpr() {
        val result = ExprTypeInferrer.infer(
            BinaryExpr(NumberLiteral("1"), BinaryOp.ADD, NumberLiteral("2")),
            emptySymbols,
        )
        assertIs<InferResult.Uninferrable>(result)
    }

    @Test
    fun returnsUninferrableForReferenceExpr() {
        val result = ExprTypeInferrer.infer(ReferenceExpr("x"), emptySymbols)
        assertIs<InferResult.Uninferrable>(result)
    }

    @Test
    fun returnsUninferrableForNonEnumMember() {
        val st = SymbolTable()
        st.define(Symbol("Foo", SymbolKind.COMPONENT, ComponentDecl("Foo")))
        val result = ExprTypeInferrer.infer(
            MemberExpr(ReferenceExpr("Foo"), "bar"),
            st,
        )
        assertIs<InferResult.Uninferrable>(result)
    }
}
