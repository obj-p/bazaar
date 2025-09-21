package parser

import (
	"github.com/alecthomas/participle/v2"
	participleLexer "github.com/alecthomas/participle/v2/lexer"
	bazaarLexer "github.com/obj-p/bazaar/internal/lexer"
)

var mapEntryParser = participle.MustBuild[MapEntry](
	participle.Lexer(bazaarLexer.BazaarLexer),
	participle.Elide("Comment", "StringExprWhitespace", "Whitespace"),
)

type Bool bool

func (b *Bool) Capture(values []string) error {
	*b = values[0] == "true"
	return nil
}

type StringFragment struct {
	Esc  *string `parser:"@StringEsc"`
	Expr *Expr   `parser:"| '${' @@ '}'"`
	Text *string `parser:"| @StringText"`
}

type String struct {
	Fragments []*StringFragment `parser:"\"\\\"\" @@* \"\\\"\""`
}

type Literal struct {
	Null   bool          `parser:"@'null'"`
	Bool   *Bool         `parser:"| @('true' | 'false')"`
	Number *string       `parser:"| @Number"`
	String *String       `parser:"| @@"`
	Array  *ArrayLiteral `parser:"| @@"`
	Map    *MapLiteral   `parser:"| @@"`
}

type ArrayLiteral struct {
	Values []*Expr `parser:"'[' (@@ (',' @@)* ','?)? ']'"`
}

type MapEntry struct {
	Key   Expr `parser:"@@"`
	Value Expr `parser:"':' @@"`
}

type MapLiteral struct {
	Entries []*MapEntry // `parser:"'{' (':' | (@@ (',' @@)* ','?)) '}'"`
}

func (m *MapLiteral) Parse(lex *participleLexer.PeekingLexer) error {
	tok := lex.Peek()
	if tok.Value != "{" {
		return participle.NextMatch
	}

	checkpoint := lex.MakeCheckpoint()
	lex.Next()

	if tok = lex.Peek(); tok.Value == ":" {
		lex.Next()
		if tok = lex.Peek(); tok.Value != "}" {
			return participle.Errorf(tok.Pos, "expected a '}' for empty map but got %s", tok.Value)
		}

		lex.Next()
		return nil
	}

	lex.LoadCheckpoint(checkpoint)
	return participle.NextMatch
}
