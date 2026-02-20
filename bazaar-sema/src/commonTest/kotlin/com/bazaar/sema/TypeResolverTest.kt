package com.bazaar.sema

import com.bazaar.parser.ast.*
import com.bazaar.sema.ir.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TypeResolverTest {

    private fun tableWith(vararg decls: Decl): SymbolTable {
        val file = BazaarFile(declarations = decls.toList())
        return DeclarationCollector.collect(file).symbolTable
    }

    @Test
    fun resolvesBuiltinInt() {
        val file = BazaarFile(declarations = listOf(
            ComponentDecl("Box", listOf(FieldDecl("width", ValueType("int")))),
        ))
        val table = tableWith(ComponentDecl("Box"))
        val result = TypeResolver.resolve(file, table)

        assertTrue(result.diagnostics.isEmpty())
        val comp = assertIs<IrComponent>(result.ir.declarations[0])
        val field = comp.fields[0]
        val type = assertIs<IrBuiltinType>(field.type)
        assertEquals("int", type.name)
        assertEquals(IrPrimitiveKind.INT, type.kind)
        assertEquals(false, type.nullable)
    }

    @Test
    fun resolvesBuiltinString() {
        val file = BazaarFile(declarations = listOf(
            ComponentDecl("Label", listOf(FieldDecl("text", ValueType("string")))),
        ))
        val table = tableWith(ComponentDecl("Label"))
        val result = TypeResolver.resolve(file, table)

        assertTrue(result.diagnostics.isEmpty())
        val comp = assertIs<IrComponent>(result.ir.declarations[0])
        val type = assertIs<IrBuiltinType>(comp.fields[0].type)
        assertEquals("string", type.name)
        assertEquals(IrPrimitiveKind.STRING, type.kind)
    }

    @Test
    fun resolvesNullableBuiltin() {
        val file = BazaarFile(declarations = listOf(
            ComponentDecl("Box", listOf(FieldDecl("label", ValueType("string", nullable = true)))),
        ))
        val table = tableWith(ComponentDecl("Box"))
        val result = TypeResolver.resolve(file, table)

        assertTrue(result.diagnostics.isEmpty())
        val comp = assertIs<IrComponent>(result.ir.declarations[0])
        val type = assertIs<IrBuiltinType>(comp.fields[0].type)
        assertEquals(true, type.nullable)
    }

    @Test
    fun resolvesDeclaredType() {
        val file = BazaarFile(declarations = listOf(
            ComponentDecl("Icon"),
            ComponentDecl("Button", listOf(FieldDecl("icon", ValueType("Icon")))),
        ))
        val table = tableWith(ComponentDecl("Icon"), ComponentDecl("Button"))
        val result = TypeResolver.resolve(file, table)

        assertTrue(result.diagnostics.isEmpty())
        val button = assertIs<IrComponent>(result.ir.declarations[1])
        val type = assertIs<IrDeclaredType>(button.fields[0].type)
        assertEquals("Icon", type.name)
        assertEquals(SymbolKind.COMPONENT, type.symbolKind)
    }

    @Test
    fun resolvesArrayType() {
        val file = BazaarFile(declarations = listOf(
            ComponentDecl("List", listOf(FieldDecl("items", ArrayType(ValueType("string"))))),
        ))
        val table = tableWith(ComponentDecl("List"))
        val result = TypeResolver.resolve(file, table)

        assertTrue(result.diagnostics.isEmpty())
        val comp = assertIs<IrComponent>(result.ir.declarations[0])
        val type = assertIs<IrArrayType>(comp.fields[0].type)
        val elem = assertIs<IrBuiltinType>(type.elementType)
        assertEquals("string", elem.name)
    }

    @Test
    fun resolvesMapType() {
        val file = BazaarFile(declarations = listOf(
            DataDecl("Config", listOf(FieldDecl("props", MapType(ValueType("string"), ValueType("int"))))),
        ))
        val table = tableWith(DataDecl("Config"))
        val result = TypeResolver.resolve(file, table)

        assertTrue(result.diagnostics.isEmpty())
        val data = assertIs<IrData>(result.ir.declarations[0])
        val type = assertIs<IrMapType>(data.fields[0].type)
        val key = assertIs<IrBuiltinType>(type.keyType)
        val value = assertIs<IrBuiltinType>(type.valueType)
        assertEquals("string", key.name)
        assertEquals("int", value.name)
    }

    @Test
    fun resolvesFunctionType() {
        val file = BazaarFile(declarations = listOf(
            ComponentDecl("Button", listOf(
                FieldDecl("onClick", FunctionType(paramTypes = emptyList(), nullable = true)),
            )),
        ))
        val table = tableWith(ComponentDecl("Button"))
        val result = TypeResolver.resolve(file, table)

        assertTrue(result.diagnostics.isEmpty())
        val comp = assertIs<IrComponent>(result.ir.declarations[0])
        val type = assertIs<IrFunctionType>(comp.fields[0].type)
        assertTrue(type.paramTypes.isEmpty())
        assertNull(type.returnType)
        assertEquals(true, type.nullable)
    }

    @Test
    fun resolvesFunctionTypeWithParams() {
        val file = BazaarFile(declarations = listOf(
            ComponentDecl("Widget", listOf(
                FieldDecl("transform", FunctionType(
                    paramTypes = listOf(ValueType("int"), ValueType("string")),
                    returnType = ValueType("bool"),
                )),
            )),
        ))
        val table = tableWith(ComponentDecl("Widget"))
        val result = TypeResolver.resolve(file, table)

        assertTrue(result.diagnostics.isEmpty())
        val comp = assertIs<IrComponent>(result.ir.declarations[0])
        val type = assertIs<IrFunctionType>(comp.fields[0].type)
        assertEquals(2, type.paramTypes.size)
        assertIs<IrBuiltinType>(type.paramTypes[0])
        assertIs<IrBuiltinType>(type.paramTypes[1])
        val ret = assertIs<IrBuiltinType>(type.returnType)
        assertEquals("bool", ret.name)
    }

    @Test
    fun emitsErrorForUndefinedType() {
        val file = BazaarFile(declarations = listOf(
            ComponentDecl("Box", listOf(FieldDecl("child", ValueType("NonExistent")))),
        ))
        val table = tableWith(ComponentDecl("Box"))
        val result = TypeResolver.resolve(file, table)

        assertEquals(1, result.diagnostics.size)
        assertEquals(SemaSeverity.ERROR, result.diagnostics[0].severity)
        assertTrue(result.diagnostics[0].message.contains("undefined type"))
        assertTrue(result.diagnostics[0].message.contains("NonExistent"))
    }

    @Test
    fun emitsErrorForUndefinedArrayElement() {
        val file = BazaarFile(declarations = listOf(
            ComponentDecl("List", listOf(FieldDecl("items", ArrayType(ValueType("Unknown"))))),
        ))
        val table = tableWith(ComponentDecl("List"))
        val result = TypeResolver.resolve(file, table)

        assertEquals(1, result.diagnostics.size)
        assertTrue(result.diagnostics[0].message.contains("Unknown"))
    }

    @Test
    fun resolvesEnumDecl() {
        val file = BazaarFile(declarations = listOf(
            EnumDecl("Color", listOf("RED", "GREEN", "BLUE")),
        ))
        val table = tableWith(EnumDecl("Color", listOf("RED", "GREEN", "BLUE")))
        val result = TypeResolver.resolve(file, table)

        assertTrue(result.diagnostics.isEmpty())
        val enum = assertIs<IrEnum>(result.ir.declarations[0])
        assertEquals("Color", enum.name)
        assertEquals(listOf("RED", "GREEN", "BLUE"), enum.values)
    }

    @Test
    fun resolvesFunctionDecl() {
        val file = BazaarFile(declarations = listOf(
            FunctionDecl("add", listOf(
                ParameterDecl("a", ValueType("int")),
                ParameterDecl("b", ValueType("int")),
            ), returnType = ValueType("int")),
        ))
        val table = tableWith(FunctionDecl("add"))
        val result = TypeResolver.resolve(file, table)

        assertTrue(result.diagnostics.isEmpty())
        val fn = assertIs<IrFunction>(result.ir.declarations[0])
        assertEquals("add", fn.name)
        assertEquals(2, fn.params.size)
        assertIs<IrBuiltinType>(fn.params[0].type)
        val ret = assertIs<IrBuiltinType>(fn.returnType)
        assertEquals("int", ret.name)
    }

    @Test
    fun resolvesTemplateDecl() {
        val file = BazaarFile(declarations = listOf(
            TemplateDecl("MyScreen", listOf(
                ParameterDecl("title", ValueType("string")),
            )),
        ))
        val table = tableWith(TemplateDecl("MyScreen"))
        val result = TypeResolver.resolve(file, table)

        assertTrue(result.diagnostics.isEmpty())
        val tmpl = assertIs<IrTemplate>(result.ir.declarations[0])
        assertEquals("MyScreen", tmpl.name)
        assertEquals(1, tmpl.params.size)
        val type = assertIs<IrBuiltinType>(tmpl.params[0].type)
        assertEquals("string", type.name)
    }

    @Test
    fun resolvesPreviewDecl() {
        val file = BazaarFile(declarations = listOf(
            PreviewDecl("PreviewMain"),
        ))
        val table = tableWith(PreviewDecl("PreviewMain"))
        val result = TypeResolver.resolve(file, table)

        assertTrue(result.diagnostics.isEmpty())
        val preview = assertIs<IrPreview>(result.ir.declarations[0])
        assertEquals("PreviewMain", preview.name)
    }

    @Test
    fun resolvesPackageName() {
        val file = BazaarFile(
            packageDecl = PackageDecl(listOf("com", "example", "ui")),
            declarations = listOf(ComponentDecl("Box")),
        )
        val table = tableWith(ComponentDecl("Box"))
        val result = TypeResolver.resolve(file, table)

        assertEquals("com.example.ui", result.ir.packageName)
    }

    @Test
    fun nullPackageWhenNoneProvided() {
        val file = BazaarFile(declarations = listOf(ComponentDecl("Box")))
        val table = tableWith(ComponentDecl("Box"))
        val result = TypeResolver.resolve(file, table)

        assertNull(result.ir.packageName)
    }

    @Test
    fun resolvesFieldWithDefault() {
        val file = BazaarFile(declarations = listOf(
            ComponentDecl("Button", listOf(
                FieldDecl("label", ValueType("string"), default = StringLiteral(listOf(TextPart("Click")))),
            )),
        ))
        val table = tableWith(ComponentDecl("Button"))
        val result = TypeResolver.resolve(file, table)

        assertTrue(result.diagnostics.isEmpty())
        val comp = assertIs<IrComponent>(result.ir.declarations[0])
        val field = comp.fields[0]
        assertIs<StringLiteral>(field.default)
    }

    @Test
    fun resolvesConstructorMembers() {
        val file = BazaarFile(declarations = listOf(
            ModifierDecl("Padding", listOf(
                FieldDecl("value", ValueType("int")),
                ConstructorDecl(
                    params = listOf(ParameterDecl("all", ValueType("int"))),
                    value = ReferenceExpr("all"),
                ),
            )),
        ))
        val table = tableWith(ModifierDecl("Padding"))
        val result = TypeResolver.resolve(file, table)

        assertTrue(result.diagnostics.isEmpty())
        val mod = assertIs<IrModifier>(result.ir.declarations[0])
        assertEquals(1, mod.fields.size)
        assertEquals(1, mod.constructors.size)
        assertEquals(1, mod.constructors[0].params.size)
        val paramType = assertIs<IrBuiltinType>(mod.constructors[0].params[0].type)
        assertEquals("int", paramType.name)
    }

    @Test
    fun resolvesNestedArrayOfDeclaredType() {
        val file = BazaarFile(declarations = listOf(
            DataDecl("Item"),
            ComponentDecl("Grid", listOf(
                FieldDecl("rows", ArrayType(ArrayType(ValueType("Item")))),
            )),
        ))
        val table = tableWith(DataDecl("Item"), ComponentDecl("Grid"))
        val result = TypeResolver.resolve(file, table)

        assertTrue(result.diagnostics.isEmpty())
        val grid = assertIs<IrComponent>(result.ir.declarations[1])
        val outer = assertIs<IrArrayType>(grid.fields[0].type)
        val inner = assertIs<IrArrayType>(outer.elementType)
        val elem = assertIs<IrDeclaredType>(inner.elementType)
        assertEquals("Item", elem.name)
    }
}
