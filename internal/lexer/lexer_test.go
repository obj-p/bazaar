package lexer

import (
	"github.com/obj-p/bazaar/internal/token"
	"testing"
)

func TestNextToken(t *testing.T) {
	input := ",(){}"
	tests := []struct {
		expectedType    token.TokenType
		expectedLiteral string
	}{
		{token.COMMA, ","},
		{token.LPAREN, "("},
		{token.RPAREN, ")"},
		{token.LBRACE, "{"},
		{token.RBRACE, "}"},
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
