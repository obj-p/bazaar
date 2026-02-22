package com.bazaar.parser.ast

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AstSerializerTest {
    @Test
    fun emptyFile() {
        val file = BazaarFile()
        assertEquals(
            "BazaarFile\n",
            AstSerializer.serialize(file),
        )
    }

    @Test
    fun packageDecl() {
        val file = BazaarFile(packageDecl = PackageDecl(listOf("com", "example")))
        assertEquals(
            """
            BazaarFile
              packageDecl:
                PackageDecl
                  segments: ["com", "example"]
            """.trimIndent() + "\n",
            AstSerializer.serialize(file),
        )
    }

    @Test
    fun imports() {
        val file =
            BazaarFile(
                imports =
                    listOf(
                        ImportDecl(listOf("layout")),
                        ImportDecl(listOf("com", "example", "utils"), alias = "u"),
                    ),
            )
        assertEquals(
            """
            BazaarFile
              imports:
                ImportDecl
                  segments: ["layout"]
                ImportDecl
                  segments: ["com", "example", "utils"]
                  alias: "u"
            """.trimIndent() + "\n",
            AstSerializer.serialize(file),
        )
    }

    @Test
    fun enumDecl() {
        val file =
            BazaarFile(
                declarations =
                    listOf(
                        EnumDecl("Color", listOf("Red", "Green", "Blue")),
                    ),
            )
        assertEquals(
            """
            BazaarFile
              declarations:
                EnumDecl
                  name: "Color"
                  values: ["Red", "Green", "Blue"]
            """.trimIndent() + "\n",
            AstSerializer.serialize(file),
        )
    }

    @Test
    fun componentWithFields() {
        val file =
            BazaarFile(
                declarations =
                    listOf(
                        ComponentDecl(
                            name = "Button",
                            members =
                                listOf(
                                    FieldDecl(
                                        name = "label",
                                        type = ValueType("string"),
                                    ),
                                    FieldDecl(
                                        name = "onClick",
                                        type = FunctionType(emptyList(), nullable = true),
                                        default = NullLiteral,
                                    ),
                                ),
                        ),
                    ),
            )
        assertEquals(
            """
            BazaarFile
              declarations:
                ComponentDecl
                  name: "Button"
                  members:
                    FieldDecl
                      name: "label"
                      type:
                        ValueType
                          name: "string"
                    FieldDecl
                      name: "onClick"
                      type:
                        FunctionType
                          nullable: true
                      default:
                        NullLiteral
            """.trimIndent() + "\n",
            AstSerializer.serialize(file),
        )
    }

    @Test
    fun functionDeclWithParams() {
        val file =
            BazaarFile(
                declarations =
                    listOf(
                        FunctionDecl(
                            name = "add",
                            params =
                                listOf(
                                    ParameterDecl("x", ValueType("int")),
                                    ParameterDecl("y", ValueType("int")),
                                ),
                            returnType = ValueType("int"),
                        ),
                    ),
            )
        assertEquals(
            """
            BazaarFile
              declarations:
                FunctionDecl
                  name: "add"
                  params:
                    ParameterDecl
                      name: "x"
                      type:
                        ValueType
                          name: "int"
                    ParameterDecl
                      name: "y"
                      type:
                        ValueType
                          name: "int"
                  returnType:
                    ValueType
                      name: "int"
            """.trimIndent() + "\n",
            AstSerializer.serialize(file),
        )
    }

    @Test
    fun ifStatement() {
        val file =
            BazaarFile(
                declarations =
                    listOf(
                        TemplateDecl(
                            name = "Test",
                            body =
                                listOf(
                                    IfStmt(
                                        fragments =
                                            listOf(
                                                IfExprFragment(BoolLiteral(true)),
                                            ),
                                        body =
                                            listOf(
                                                ReturnStmt(),
                                            ),
                                        elseBody =
                                            listOf(
                                                ReturnStmt(NumberLiteral("0")),
                                            ),
                                    ),
                                ),
                        ),
                    ),
            )
        assertEquals(
            """
            BazaarFile
              declarations:
                TemplateDecl
                  name: "Test"
                  body:
                    IfStmt
                      fragments:
                        IfExprFragment
                          expr:
                            BoolLiteral
                              value: true
                      body:
                        ReturnStmt
                      elseBody:
                        ReturnStmt
                          value:
                            NumberLiteral
                              value: "0"
            """.trimIndent() + "\n",
            AstSerializer.serialize(file),
        )
    }

    @Test
    fun nullLiteral() {
        val file =
            BazaarFile(
                declarations =
                    listOf(
                        FunctionDecl(
                            name = "f",
                            body =
                                listOf(
                                    ReturnStmt(NullLiteral),
                                ),
                        ),
                    ),
            )
        val result = AstSerializer.serialize(file)
        assertTrue(result.contains("NullLiteral"), "Should contain NullLiteral")
        assertFalse(result.contains("NullLiteral\n            value:"), "NullLiteral should have no children")
    }

    @Test
    fun memberExprWithOptional() {
        val expr =
            MemberExpr(
                target = ReferenceExpr("foo"),
                member = "bar",
                optional = true,
            )
        val file =
            BazaarFile(
                declarations =
                    listOf(
                        FunctionDecl(
                            name = "f",
                            body = listOf(ExprStmt(expr)),
                        ),
                    ),
            )
        val result = AstSerializer.serialize(file)
        assertTrue(result.contains("optional: true"), "Should contain optional: true")
    }

    @Test
    fun stringLiteralWithParts() {
        val file =
            BazaarFile(
                declarations =
                    listOf(
                        FunctionDecl(
                            name = "f",
                            body =
                                listOf(
                                    ExprStmt(
                                        StringLiteral(
                                            listOf(
                                                TextPart("hello "),
                                                InterpolationPart(ReferenceExpr("name")),
                                                EscapePart("\\n"),
                                            ),
                                        ),
                                    ),
                                ),
                        ),
                    ),
            )
        val result = AstSerializer.serialize(file)
        assertTrue(result.contains("TextPart"), "Should contain TextPart")
        assertTrue(result.contains("InterpolationPart"), "Should contain InterpolationPart")
        assertTrue(result.contains("EscapePart"), "Should contain EscapePart")
    }
}
