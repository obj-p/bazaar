package lexer

import (
	"github.com/obj-p/bazaar/internal/token"
	"testing"
)

func TestNextToken(t *testing.T) {
	input := `
	42
	import switch modifier return
	+ += - -= * ** *= / // /=
	< <= > >= == ! !! != && ||
	\ : ; _
	?= ?. =>
	`

	tests := []struct {
		expectedToken   token.Token
		expectedLiteral string
	}{
		{token.INT, "42"},
		{token.IMPORT, "import"},
		{token.SWITCH, "switch"},
		{token.MODIFIER, "modifier"},
		{token.RETURN, "return"},
	}

	l := New(input)

	for i, tt := range tests {
		tok := l.NextToken()
		literal := l.Literal()

		if tok != tt.expectedToken {
			t.Fatalf("tests[%d] - token type wrong for literal=%q. expected=%d, got=%d",
				i, literal, tt.expectedToken, tok)
		}

		if literal != tt.expectedLiteral {
			t.Fatalf("tests[%d] - literal wrong. expected=%q, got=%q",
				i, tt.expectedLiteral, literal)
		}
	}
}
