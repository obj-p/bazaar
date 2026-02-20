package com.bazaar.sema

import com.bazaar.parser.ast.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DeclarationCollectorTest {

    @Test
    fun collectsSingleComponent() {
        val file = BazaarFile(declarations = listOf(ComponentDecl("Button")))
        val result = DeclarationCollector.collect(file)

        assertTrue(result.diagnostics.isEmpty())
        val sym = result.symbolTable.lookup("Button")
        assertNotNull(sym)
        assertEquals("Button", sym.name)
        assertEquals(SymbolKind.COMPONENT, sym.kind)
    }

    @Test
    fun collectsAllDeclKinds() {
        val file = BazaarFile(declarations = listOf(
            ComponentDecl("MyComponent"),
            DataDecl("MyData"),
            ModifierDecl("MyModifier"),
            EnumDecl("MyEnum", listOf("A", "B")),
            FunctionDecl("myFunc"),
            TemplateDecl("MyTemplate"),
            PreviewDecl("MyPreview"),
        ))
        val result = DeclarationCollector.collect(file)

        assertTrue(result.diagnostics.isEmpty())
        assertEquals(SymbolKind.COMPONENT, result.symbolTable.lookup("MyComponent")?.kind)
        assertEquals(SymbolKind.DATA, result.symbolTable.lookup("MyData")?.kind)
        assertEquals(SymbolKind.MODIFIER, result.symbolTable.lookup("MyModifier")?.kind)
        assertEquals(SymbolKind.ENUM, result.symbolTable.lookup("MyEnum")?.kind)
        assertEquals(SymbolKind.FUNCTION, result.symbolTable.lookup("myFunc")?.kind)
        assertEquals(SymbolKind.TEMPLATE, result.symbolTable.lookup("MyTemplate")?.kind)
        assertEquals(SymbolKind.PREVIEW, result.symbolTable.lookup("MyPreview")?.kind)
        assertEquals(7, result.symbolTable.allSymbols().size)
    }

    @Test
    fun detectsDuplicateDeclarations() {
        val file = BazaarFile(declarations = listOf(
            ComponentDecl("Button"),
            DataDecl("Button"),
        ))
        val result = DeclarationCollector.collect(file)

        assertEquals(1, result.diagnostics.size)
        assertEquals(SemaSeverity.ERROR, result.diagnostics[0].severity)
        assertTrue(result.diagnostics[0].message.contains("duplicate"))
        assertTrue(result.diagnostics[0].message.contains("Button"))
    }

    @Test
    fun detectsBuiltinShadowing() {
        val file = BazaarFile(declarations = listOf(
            ComponentDecl("string"),
        ))
        val result = DeclarationCollector.collect(file)

        assertEquals(1, result.diagnostics.size)
        assertEquals(SemaSeverity.ERROR, result.diagnostics[0].severity)
        assertTrue(result.diagnostics[0].message.contains("shadows built-in"))
    }

    @Test
    fun detectsAllBuiltinShadowing() {
        val file = BazaarFile(declarations = listOf(
            ComponentDecl("int"),
            DataDecl("double"),
            ModifierDecl("string"),
            EnumDecl("bool", listOf("T", "F")),
            FunctionDecl("component"),
        ))
        val result = DeclarationCollector.collect(file)

        assertEquals(5, result.diagnostics.size)
        assertTrue(result.diagnostics.all { it.severity == SemaSeverity.ERROR })
        assertTrue(result.diagnostics.all { it.message.contains("shadows built-in") })
    }

    @Test
    fun emptyFileProducesNoSymbols() {
        val file = BazaarFile()
        val result = DeclarationCollector.collect(file)

        assertTrue(result.diagnostics.isEmpty())
        assertTrue(result.symbolTable.allSymbols().isEmpty())
    }

    @Test
    fun builtinShadowingPreventsSymbolRegistration() {
        val file = BazaarFile(declarations = listOf(
            ComponentDecl("int"),
        ))
        val result = DeclarationCollector.collect(file)

        assertEquals(1, result.diagnostics.size)
        assertNull(result.symbolTable.lookup("int"))
    }

    @Test
    fun duplicateAfterBuiltinShadowDoesNotDoubleDiagnose() {
        val file = BazaarFile(declarations = listOf(
            ComponentDecl("string"),
            DataDecl("string"),
        ))
        val result = DeclarationCollector.collect(file)

        // First: shadows built-in. Second: also shadows built-in (not duplicate, since first wasn't registered).
        assertEquals(2, result.diagnostics.size)
        assertTrue(result.diagnostics.all { it.message.contains("shadows built-in") })
    }
}
