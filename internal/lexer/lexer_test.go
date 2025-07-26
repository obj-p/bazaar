package lexer

import "testing"

func TestNextToken(t *testing.T) {
	input := `
	component Row {
		children []component
	}

	component Button {
		label string
		onClick? function()
	}

	component Text {
		value string
	}

	function print(message string)

	template TextAndButtonRow() component {
		Row {
			Text("Hello, text!)

			Button("Click me!, function() {
				print("Hello, from button!)
			})
		}
	}
	`

	tests := []struct {
		expectedType    TokenType
		expectedLiteral string
	}{
		{COMPONENT, "component"},
		{IDENTIFIER, "Row"},
		{LBRACE, "{"},
		{IDENTIFIER, "children"},
	}

	l := New(input)

	for i, tt := range tests {
		tok := l.NextToken()

		if tok.Type != tt.expectedType {
			t.Fatalf("tests[%d] - token type wrong. expected=%d, got=%d",
				i, tt.expectedType, tok.Type)
		}

		if tok.Literal != tt.expectedLiteral {
			t.Fatalf("tests[%d] - literal wrong. expected=%q, got=%q",
				i, tt.expectedLiteral, tok.Literal)
		}
	}
}
