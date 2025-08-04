package lexer

import (
	"testing"

	require "github.com/alecthomas/assert/v2"
	"github.com/alecthomas/participle/v2/lexer"
)

func TestLexer(t *testing.T) {
	tokens := `
	nil true false 42.0 1337 "Hello, World!" foobar
	// Some comment
	=!?&|[](){}
	`

	tests := []struct {
		expectedSymbol string
		expectedValue  string
	}{
		{"Whitespace", "\n\t"},
		{"Ident", "nil"}, // The parser has an expression for nil
		{"Whitespace", " "},
		{"Ident", "true"}, // The parser has an expression for Bool
		{"Whitespace", " "},
		{"Ident", "false"},
		{"Whitespace", " "},
		{"Number", "42.0"},
		{"Whitespace", " "},
		{"Number", "1337"},
		{"Whitespace", " "},
		{"String", "\"Hello, World!\""},
		{"Whitespace", " "},
		{"Ident", "foobar"},
		{"Whitespace", "\n\t"},
		{"Comment", "// Some comment"},
		{"Whitespace", "\n\t"},
		{"Operator", "="},
		{"Operator", "!"},
		{"Operator", "?"},
		{"Operator", "&"},
		{"Operator", "|"},
		{"Operator", "["},
		{"Operator", "]"},
		{"Operator", "("},
		{"Operator", ")"},
		{"Operator", "{"},
		{"Operator", "}"},
	}

	symbols := lexer.SymbolsByRune(BazaarLexer)
	lex, err := BazaarLexer.LexString("tokens", tokens)
	require.NoError(t, err)

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
