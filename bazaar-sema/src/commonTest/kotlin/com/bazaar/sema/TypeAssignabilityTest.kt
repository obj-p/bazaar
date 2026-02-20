package com.bazaar.sema

import com.bazaar.sema.ir.*
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TypeAssignabilityTest {

    @Test
    fun sameBuiltinTypeIsAssignable() {
        assertTrue(isAssignable(
            IrBuiltinType("int", IrPrimitiveKind.INT),
            IrBuiltinType("int", IrPrimitiveKind.INT),
        ))
    }

    @Test
    fun intToDoubleWideningIsAssignable() {
        assertTrue(isAssignable(
            IrBuiltinType("int", IrPrimitiveKind.INT),
            IrBuiltinType("double", IrPrimitiveKind.DOUBLE),
        ))
    }

    @Test
    fun doubleToIntIsNotAssignable() {
        assertFalse(isAssignable(
            IrBuiltinType("double", IrPrimitiveKind.DOUBLE),
            IrBuiltinType("int", IrPrimitiveKind.INT),
        ))
    }

    @Test
    fun stringToIntIsNotAssignable() {
        assertFalse(isAssignable(
            IrBuiltinType("string", IrPrimitiveKind.STRING),
            IrBuiltinType("int", IrPrimitiveKind.INT),
        ))
    }

    @Test
    fun nullableSourceToNonNullableTargetIsNotAssignable() {
        assertFalse(isAssignable(
            IrBuiltinType("int", IrPrimitiveKind.INT, nullable = true),
            IrBuiltinType("int", IrPrimitiveKind.INT),
        ))
    }

    @Test
    fun nonNullableSourceToNullableTargetIsAssignable() {
        assertTrue(isAssignable(
            IrBuiltinType("int", IrPrimitiveKind.INT),
            IrBuiltinType("int", IrPrimitiveKind.INT, nullable = true),
        ))
    }

    @Test
    fun errorTypeOnSourceSideIsAssignable() {
        assertTrue(isAssignable(
            IrErrorType("bad"),
            IrBuiltinType("int", IrPrimitiveKind.INT),
        ))
    }

    @Test
    fun errorTypeOnTargetSideIsAssignable() {
        assertTrue(isAssignable(
            IrBuiltinType("string", IrPrimitiveKind.STRING),
            IrErrorType("bad"),
        ))
    }

    @Test
    fun sameDeclaredTypeIsAssignable() {
        assertTrue(isAssignable(
            IrDeclaredType("Color", SymbolKind.ENUM),
            IrDeclaredType("Color", SymbolKind.ENUM),
        ))
    }

    @Test
    fun differentDeclaredTypeIsNotAssignable() {
        assertFalse(isAssignable(
            IrDeclaredType("Color", SymbolKind.ENUM),
            IrDeclaredType("Size", SymbolKind.ENUM),
        ))
    }

    @Test
    fun emptyArrayToAnyArrayIsAssignable() {
        assertTrue(isAssignable(
            IrArrayType(IrErrorType("unknown")),
            IrArrayType(IrBuiltinType("string", IrPrimitiveKind.STRING)),
        ))
    }

    @Test
    fun matchingArrayIsAssignable() {
        assertTrue(isAssignable(
            IrArrayType(IrBuiltinType("int", IrPrimitiveKind.INT)),
            IrArrayType(IrBuiltinType("int", IrPrimitiveKind.INT)),
        ))
    }

    @Test
    fun mismatchedArrayIsNotAssignable() {
        assertFalse(isAssignable(
            IrArrayType(IrBuiltinType("string", IrPrimitiveKind.STRING)),
            IrArrayType(IrBuiltinType("int", IrPrimitiveKind.INT)),
        ))
    }

    @Test
    fun emptyMapToAnyMapIsAssignable() {
        assertTrue(isAssignable(
            IrMapType(IrErrorType("unknown"), IrErrorType("unknown")),
            IrMapType(IrBuiltinType("string", IrPrimitiveKind.STRING), IrBuiltinType("int", IrPrimitiveKind.INT)),
        ))
    }

    @Test
    fun matchingFunctionTypeIsAssignable() {
        assertTrue(isAssignable(
            IrFunctionType(listOf(IrBuiltinType("int", IrPrimitiveKind.INT)), IrBuiltinType("string", IrPrimitiveKind.STRING)),
            IrFunctionType(listOf(IrBuiltinType("int", IrPrimitiveKind.INT)), IrBuiltinType("string", IrPrimitiveKind.STRING)),
        ))
    }

    @Test
    fun differentParamCountFunctionIsNotAssignable() {
        assertFalse(isAssignable(
            IrFunctionType(listOf(IrBuiltinType("int", IrPrimitiveKind.INT)), null),
            IrFunctionType(emptyList(), null),
        ))
    }
}
