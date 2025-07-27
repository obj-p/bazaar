package lexer

import "testing"

func TestNextToken(t *testing.T) {
	input := `
	42
	import match modifier return
	+ += - -= * ** *= / // /=
	< <= > >= == ! !! != && ||
	\ : ;
	?= ?. =>
	`

	tests := []struct {
		expectedType    TokenType
		expectedLiteral string
	}{
		{INT, "42"},
		{IMPORT, "import"},
		{MATCH, "match"},
		{MODIFIER, "modifier"},
		{RETURN, "return"},
	}

	l := New(input)

	for i, tt := range tests {
		tok := l.NextToken()

		if tok.Type != tt.expectedType {
			t.Fatalf("tests[%d] - token type wrong for literal=%q. expected=%d, got=%d",
				i, tok.Literal, tt.expectedType, tok.Type)
		}

		if tok.Literal != tt.expectedLiteral {
			t.Fatalf("tests[%d] - literal wrong. expected=%q, got=%q",
				i, tt.expectedLiteral, tok.Literal)
		}
	}
}

func TestNextTokenInExampleBz(t *testing.T) {
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
		expectedType    TokenType
		expectedLiteral string
	}{
		{PACKAGE, "package"},
		{IDENTIFIER, "main"},
		{ENUM, "enum"},
		{IDENTIFIER, "RowAlignment"},
		{LBRACE, "{"},
		{IDENTIFIER, "top"},
		{COMMA, ","},
		{IDENTIFIER, "center"},
		{COMMA, ","},
		{IDENTIFIER, "bottom"},
		{RBRACE, "}"},
		{COMPONENT, "component"},
		{IDENTIFIER, "Row"},
		{LBRACE, "{"},
		{IDENTIFIER, "alignnment"},
		{IDENTIFIER, "RowAlignment"},
		{ASSIGN, "="},
		{DOT, "."},
		{IDENTIFIER, "center"},
		{IDENTIFIER, "children"},
		{LBRACK, "["},
		{RBRACK, "]"},
		{COMPONENT, "component"},
		{RBRACE, "}"},
		{COMPONENT, "component"},
		{IDENTIFIER, "Button"},
		{LBRACE, "{"},
		{IDENTIFIER, "label"},
		{STRING, "string"},
		{IDENTIFIER, "onClick"},
		{QUESTION, "?"},
		{FUNC, "func"},
		{LPAREN, "("},
		{RPAREN, ")"},
		{ASSIGN, "="},
		{NIL, "nil"},
		{RBRACE, "}"},
		{COMPONENT, "component"},
		{IDENTIFIER, "Text"},
		{LBRACE, "{"},
		{IDENTIFIER, "value"},
		{STRING, "string"},
		{RBRACE, "}"},
		{FUNC, "func"},
		{IDENTIFIER, "add"},
		{LPAREN, "("},
		{IDENTIFIER, "x"},
		{INT, "int"},
		{COMMA, ","},
		{IDENTIFIER, "y"},
		{INT, "int"},
		{RPAREN, ")"},
		{INT, "int"},
		{FUNC, "func"},
		{IDENTIFIER, "truncate"},
		{LPAREN, "("},
		{IDENTIFIER, "x"},
		{DOUBLE, "double"},
		{RPAREN, ")"},
		{INT, "int"},
		{FUNC, "func"},
		{IDENTIFIER, "print"},
		{LPAREN, "("},
		{IDENTIFIER, "message"},
		{STRING, "string"},
		{RPAREN, ")"},
		{FUNC, "func"},
		{IDENTIFIER, "dismiss"},
		{LPAREN, "("},
		{IDENTIFIER, "animated"},
		{BOOL, "bool"},
		{RPAREN, ")"},
		{DATA, "data"},
		{IDENTIFIER, "TextAndButtonRowModel"},
		{LBRACE, "{"},
		{IDENTIFIER, "value"},
		{STRING, "string"},
		{IDENTIFIER, "label"},
		{STRING, "string"},
		{IDENTIFIER, "message"},
		{QUESTION, "?"},
		{STRING, "string"},
		{RBRACE, "}"},
		{TEMPLATE, "template"},
		{IDENTIFIER, "TextAndButtonRow"},
		{LPAREN, "("},
		{IDENTIFIER, "models"},
		{LBRACK, "["},
		{RBRACK, "]"},
		{IDENTIFIER, "TextAndButtonRowModel"},
		{RPAREN, ")"},
		{LBRACE, "{"},
		{FOR, "for"},
		{IDENTIFIER, "model"},
		{IN, "in"},
		{IDENTIFIER, "models"},
		{LBRACE, "{"},
		{IDENTIFIER, "Row"},
		{LBRACE, "{"},
		{IDENTIFIER, "Text"},
		{LPAREN, "("},
		{IDENTIFIER, "model"},
		{DOT, "."},
		{IDENTIFIER, "value"},
		{RPAREN, ")"},
		{IDENTIFIER, "Button"},
		{LPAREN, "("},
		{IDENTIFIER, "model"},
		{DOT, "."},
		{IDENTIFIER, "label"},
		{RPAREN, ")"},
		{LBRACE, "{"},
		{IF, "if"},
		{LET, "let"},
		{IDENTIFIER, "message"},
		{ASSIGN, "="},
		{IDENTIFIER, "model"},
		{DOT, "."},
		{IDENTIFIER, "message"},
		{LBRACE, "{"},
		{IDENTIFIER, "print"},
		{LPAREN, "("},
		{IDENTIFIER, "message"},
		{RPAREN, ")"},
		{RBRACE, "}"},
		{RBRACE, "}"},
		{RBRACE, "}"},
		{RBRACE, "}"},
		{RBRACE, "}"},
	}

	l := New(input)

	for i, tt := range tests {
		tok := l.NextToken()

		if tok.Type != tt.expectedType {
			t.Fatalf("tests[%d] - token type wrong for literal=%q. expected=%d, got=%d",
				i, tok.Literal, tt.expectedType, tok.Type)
		}

		if tok.Literal != tt.expectedLiteral {
			t.Fatalf("tests[%d] - literal wrong. expected=%q, got=%q",
				i, tt.expectedLiteral, tok.Literal)
		}
	}

	tok := l.NextToken()
	if tok.Type != EOF || tok.Literal != "" {
		t.Fatalf("expected EOF got=%q", tok.Literal)
	}
}
