package lexer

import (
	"github.com/obj-p/bazaar/internal/token"
	"testing"
)

func TestNextTokenInExample(t *testing.T) {
	input := `
	package main

	enum RowAlignment {
		top, center, bottom
	}

	component Row {
		alignnment RowAlignment = .center
		children []component
	}

	component Button {
		label string
		onClick? func() = nil
	}

	component Text {
		value string
	}

	func add(x int, y int) int

	func truncate(x double) int

	func print(message string)

	func dismiss(animated bool)

	data TextAndButtonRowModel {
		value string
		label string
		message? string
	}

	template TextAndButtonRow(models []TextAndButtonRowModel) {
		for model in models {
			Row {
				Text(model.value)

				Button(model.label) {
					if let message = model.message {
						print(message)
					}
				}
			}
		}
	}
	`

	tests := []struct {
		expectedToken   token.Token
		expectedLiteral string
	}{
		{token.PACKAGE, "package"},
		{token.IDENTIFIER, "main"},
		{token.ENUM, "enum"},
		{token.IDENTIFIER, "RowAlignment"},
		{token.LBRACE, "{"},
		{token.IDENTIFIER, "top"},
		{token.COMMA, ","},
		{token.IDENTIFIER, "center"},
		{token.COMMA, ","},
		{token.IDENTIFIER, "bottom"},
		{token.RBRACE, "}"},
		{token.COMPONENT, "component"},
		{token.IDENTIFIER, "Row"},
		{token.LBRACE, "{"},
		{token.IDENTIFIER, "alignnment"},
		{token.IDENTIFIER, "RowAlignment"},
		{token.ASSIGN, "="},
		{token.DOT, "."},
		{token.IDENTIFIER, "center"},
		{token.IDENTIFIER, "children"},
		{token.LBRACK, "["},
		{token.RBRACK, "]"},
		{token.COMPONENT, "component"},
		{token.RBRACE, "}"},
		{token.COMPONENT, "component"},
		{token.IDENTIFIER, "Button"},
		{token.LBRACE, "{"},
		{token.IDENTIFIER, "label"},
		{token.STRING, "string"},
		{token.IDENTIFIER, "onClick"},
		{token.QUESTION, "?"},
		{token.FUNC, "func"},
		{token.LPAREN, "("},
		{token.RPAREN, ")"},
		{token.ASSIGN, "="},
		{token.NIL, "nil"},
		{token.RBRACE, "}"},
		{token.COMPONENT, "component"},
		{token.IDENTIFIER, "Text"},
		{token.LBRACE, "{"},
		{token.IDENTIFIER, "value"},
		{token.STRING, "string"},
		{token.RBRACE, "}"},
		{token.FUNC, "func"},
		{token.IDENTIFIER, "add"},
		{token.LPAREN, "("},
		{token.IDENTIFIER, "x"},
		{token.INT, "int"},
		{token.COMMA, ","},
		{token.IDENTIFIER, "y"},
		{token.INT, "int"},
		{token.RPAREN, ")"},
		{token.INT, "int"},
		{token.FUNC, "func"},
		{token.IDENTIFIER, "truncate"},
		{token.LPAREN, "("},
		{token.IDENTIFIER, "x"},
		{token.DOUBLE, "double"},
		{token.RPAREN, ")"},
		{token.INT, "int"},
		{token.FUNC, "func"},
		{token.IDENTIFIER, "print"},
		{token.LPAREN, "("},
		{token.IDENTIFIER, "message"},
		{token.STRING, "string"},
		{token.RPAREN, ")"},
		{token.FUNC, "func"},
		{token.IDENTIFIER, "dismiss"},
		{token.LPAREN, "("},
		{token.IDENTIFIER, "animated"},
		{token.BOOL, "bool"},
		{token.RPAREN, ")"},
		{token.DATA, "data"},
		{token.IDENTIFIER, "TextAndButtonRowModel"},
		{token.LBRACE, "{"},
		{token.IDENTIFIER, "value"},
		{token.STRING, "string"},
		{token.IDENTIFIER, "label"},
		{token.STRING, "string"},
		{token.IDENTIFIER, "message"},
		{token.QUESTION, "?"},
		{token.STRING, "string"},
		{token.RBRACE, "}"},
		{token.TEMPLATE, "template"},
		{token.IDENTIFIER, "TextAndButtonRow"},
		{token.LPAREN, "("},
		{token.IDENTIFIER, "models"},
		{token.LBRACK, "["},
		{token.RBRACK, "]"},
		{token.IDENTIFIER, "TextAndButtonRowModel"},
		{token.RPAREN, ")"},
		{token.LBRACE, "{"},
		{token.FOR, "for"},
		{token.IDENTIFIER, "model"},
		{token.IN, "in"},
		{token.IDENTIFIER, "models"},
		{token.LBRACE, "{"},
		{token.IDENTIFIER, "Row"},
		{token.LBRACE, "{"},
		{token.IDENTIFIER, "Text"},
		{token.LPAREN, "("},
		{token.IDENTIFIER, "model"},
		{token.DOT, "."},
		{token.IDENTIFIER, "value"},
		{token.RPAREN, ")"},
		{token.IDENTIFIER, "Button"},
		{token.LPAREN, "("},
		{token.IDENTIFIER, "model"},
		{token.DOT, "."},
		{token.IDENTIFIER, "label"},
		{token.RPAREN, ")"},
		{token.LBRACE, "{"},
		{token.IF, "if"},
		{token.LET, "let"},
		{token.IDENTIFIER, "message"},
		{token.ASSIGN, "="},
		{token.IDENTIFIER, "model"},
		{token.DOT, "."},
		{token.IDENTIFIER, "message"},
		{token.LBRACE, "{"},
		{token.IDENTIFIER, "print"},
		{token.LPAREN, "("},
		{token.IDENTIFIER, "message"},
		{token.RPAREN, ")"},
		{token.RBRACE, "}"},
		{token.RBRACE, "}"},
		{token.RBRACE, "}"},
		{token.RBRACE, "}"},
		{token.RBRACE, "}"},
	}

	l := New(input)

	for i, tt := range tests {
		expectedToken := tt.expectedToken.String()
		tok := l.NextToken().String()
		literal := l.Literal()

		if tok != expectedToken {
			t.Fatalf("tests[%d] - token type wrong for literal=%q. expected=%q, got=%q",
				i, literal, expectedToken, tok)
		}

		if literal != tt.expectedLiteral {
			t.Fatalf("tests[%d] - literal wrong. expected=%q, got=%q",
				i, tt.expectedLiteral, literal)
		}
	}

	tok := l.NextToken()
	literal := l.Literal()
	if tok != token.EOF || literal != "" {
		t.Fatalf("expected EOF got=%q", literal)
	}
}
