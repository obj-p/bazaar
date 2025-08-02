package lexer

import (
	"testing"

	require "github.com/alecthomas/assert/v2"
	"github.com/alecthomas/participle/v2/lexer"
)

func TestLexer(t *testing.T) {
	tokens := `
	true false
	"Hello, World!"
	// Some comment
	foobar
	`

	tests := []struct {
		expectedSymbol string
		expectedValue  string
	}{
		{"Whitespace", "\n\t"},
		{"Bool", "true"},
		{"Whitespace", " "},
		{"Bool", "false"},
		{"Whitespace", "\n\t"},
		{"String", "\"Hello, World!\""},
		{"Whitespace", "\n\t"},
		{"Comment", "// Some comment"},
		{"Whitespace", "\n\t"},
		{"Ident", "foobar"},
	}

	symbols := lexer.SymbolsByRune(BazaarLexer)
	lex, _ := BazaarLexer.LexString("tokens", tokens)

	for i, tt := range tests {
		tok, err := lex.Next()
		require.NoError(t, err, "tests[%d] - Next() resulted in error", i)
		symbol := symbols[tok.Type]
		require.Equal(t, tt.expectedSymbol, symbol,
			"tests[%d] - symbol wrong. expected=%q, got=%q", i, tt.expectedSymbol, symbol)
		value := tok.Value
		require.Equal(t, tt.expectedValue, value,
			"tests[%d] - value wrong. expected=%q, got=%q", i, tt.expectedValue, value)
	}
}
