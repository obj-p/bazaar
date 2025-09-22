package lexer

import (
	"testing"

	require "github.com/alecthomas/assert/v2"
	"github.com/alecthomas/participle/v2/lexer"
)

func TestLexer(t *testing.T) {
	tokens := `
	nil true false 42.0 -1337 "Hello, ${firstName ", " + (lastName)}!" foobar
	// Some comment
	enumerate len range
	case component data default enum for if import in modifier package return switch var
	??-+*/%<>^!?.
	==<=>=!=&&||
	+=-=*=/=%==
	[](){}@#$:;,
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
		{"Number", "-1337"},
		{"Whitespace", " "},
		{"String", "\""},
		{"StringText", "Hello, "},
		{"StringExpr", "${"},
		{"Ident", "firstName"},
		{"StringExprWhitespace", " "},
		{"String", "\""},
		{"StringText", ", "},
		{"StringEnd", "\""},
		{"StringExprWhitespace", " "},
		{"Operator", "+"},
		{"StringExprWhitespace", " "},
		{"Punct", "("},
		{"Ident", "lastName"},
		{"Punct", ")"},
		{"StringExprEnd", "}"},
		{"StringText", "!"},
		{"StringEnd", "\""},
		{"Whitespace", " "},
		{"Ident", "foobar"},
		{"Whitespace", "\n\t"},
		{"Comment", "// Some comment"},
		{"Whitespace", "\n\t"},
		{"BuiltIn", "enumerate"},
		{"Whitespace", " "},
		{"BuiltIn", "len"},
		{"Whitespace", " "},
		{"BuiltIn", "range"},
		{"Whitespace", "\n\t"},
		{"Keyword", "case"},
		{"Whitespace", " "},
		{"Ident", "component"},
		{"Whitespace", " "},
		{"Ident", "data"},
		{"Whitespace", " "},
		{"Keyword", "default"},
		{"Whitespace", " "},
		{"Keyword", "enum"},
		{"Whitespace", " "},
		{"Keyword", "for"},
		{"Whitespace", " "},
		{"Keyword", "if"},
		{"Whitespace", " "},
		{"Keyword", "import"},
		{"Whitespace", " "},
		{"Keyword", "in"},
		{"Whitespace", " "},
		{"Ident", "modifier"},
		{"Whitespace", " "},
		{"Ident", "package"},
		{"Whitespace", " "},
		{"Keyword", "return"},
		{"Whitespace", " "},
		{"Keyword", "switch"},
		{"Whitespace", " "},
		{"Keyword", "var"},
		{"Whitespace", "\n\t"},
		{"Operator", "??"},
		{"Operator", "-"},
		{"Operator", "+"},
		{"Operator", "*"},
		{"Operator", "/"},
		{"Operator", "%"},
		{"Operator", "<"},
		{"Operator", ">"},
		{"Operator", "^"},
		{"Operator", "!"},
		{"Operator", "?"},
		{"Operator", "."},
		{"Whitespace", "\n\t"},
		{"LogicOperator", "=="},
		{"LogicOperator", "<="},
		{"LogicOperator", ">="},
		{"LogicOperator", "!="},
		{"LogicOperator", "&&"},
		{"LogicOperator", "||"},
		{"Whitespace", "\n\t"},
		{"AssignOperator", "+="},
		{"AssignOperator", "-="},
		{"AssignOperator", "*="},
		{"AssignOperator", "/="},
		{"AssignOperator", "%="},
		{"AssignOperator", "="},
		{"Whitespace", "\n\t"},
		{"Punct", "["},
		{"Punct", "]"},
		{"Punct", "("},
		{"Punct", ")"},
		{"Punct", "{"},
		{"Punct", "}"},
		{"Punct", "@"},
		{"Punct", "#"},
		{"Punct", "$"},
		{"Punct", ":"},
		{"Punct", ";"},
		{"Punct", ","},
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
