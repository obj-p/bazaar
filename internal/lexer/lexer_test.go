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
	}

	l := New(input)

	for i, tt := range tests {
		tok := l.NextToken()

		if tok.Type != tt.expectedType {
			t.Fatalf("tests[%d] - tokentype wrong. expected=%d, got=%d",
				i, tt.expectedType, tok.Type)
		}
	}
}
