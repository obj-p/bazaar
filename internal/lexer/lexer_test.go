package lexer

import "testing"

func TestNextToken(t *testing.T) {
	input := `
	enum RowAlignment {
		top, center, bottom
	}

	component Row {
		alignnment RowAlignment = .center
		children []component
	}

	component Button {
		label string
		onClick? function() = null
	}

	component Text {
		value string
	}

	function add(x int, y int) int

	function truncate(x double) int

	function print(message string)

	function dismiss(animated bool)

	data TextAndButtonRowModel {
		value string
		label string
		message? string
	}

	template TextAndButtonRow(models []TextAndButtonRowModel) {
		for model in models {
			Row {
				Text(model.value)

				Button(model.label, function() {
					if let message = model.message {
						print(message)
					}
				})
			}
		}
	}
	`

	tests := []struct {
		expectedType    TokenType
		expectedLiteral string
	}{
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
		{FUNCTION, "function"},
		{LPAREN, "("},
		{RPAREN, ")"},
		{ASSIGN, "="},
		{NULL, "null"},
		{RBRACE, "}"},
		{COMPONENT, "component"},
		{IDENTIFIER, "Text"},
		{LBRACE, "{"},
		{IDENTIFIER, "value"},
		{STRING, "string"},
		{RBRACE, "}"},
		{FUNCTION, "function"},
		{IDENTIFIER, "add"},
		{LPAREN, "("},
		{IDENTIFIER, "x"},
		{INT, "int"},
		{COMMA, ","},
		{IDENTIFIER, "y"},
		{INT, "int"},
		{RPAREN, ")"},
		{INT, "int"},
		{FUNCTION, "function"},
		{IDENTIFIER, "truncate"},
		{LPAREN, "("},
		{IDENTIFIER, "x"},
		{DOUBLE, "double"},
		{RPAREN, ")"},
		{INT, "int"},
		{FUNCTION, "function"},
		{IDENTIFIER, "print"},
		{LPAREN, "("},
		{IDENTIFIER, "message"},
		{STRING, "string"},
		{RPAREN, ")"},
		{FUNCTION, "function"},
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
		{COMMA, ","},
		{FUNCTION, "function"},
		{LPAREN, "("},
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
		{RPAREN, ")"},
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
