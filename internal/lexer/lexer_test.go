package lexer

import (
	"github.com/obj-p/bazaar/internal/token"
	"testing"
)

func TestNextToken(t *testing.T) {
	input := `
	+ - * / %
	= += -= *= /= %=
	== < <= > >= ! != && ||
	, : . ; _
	[ ] ( ) { }
	? ?? !!
	`

	tests := []struct {
		expectedToken   token.Token
		expectedLiteral string
	}{
		{token.ADD, "+"},
		{token.SUB, "-"},
		{token.MUL, "*"},
		{token.DIV, "/"},
		{token.MOD, "%"},
		{token.ASSIGN, "="},
		{token.ADD_ASSIGN, "+="},
		{token.SUB_ASSIGN, "-="},
		{token.MUL_ASSIGN, "*="},
		{token.DIV_ASSIGN, "/="},
		{token.MOD_ASSIGN, "%="},
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
}
