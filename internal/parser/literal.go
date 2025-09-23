package parser

import (
	"errors"

	"github.com/alecthomas/participle/v2"
	participleLexer "github.com/alecthomas/participle/v2/lexer"
)

type Bool bool

func (b *Bool) Capture(values []string) error {
	*b = values[0] == "true"
	return nil
}

type StringFragment struct {
	UnicodeEsc *string `parser:"@UnicodeShortEscape | @UnicodeLongEscape"`
	Esc        *string `parser:"| @StringEsc"`
	Expr       *Expr   `parser:"| '${' @@? '}'"`
	Text       *string `parser:"| @StringText"`
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
	Key   *Expr
	Value *Expr
}

type MapLiteral struct {
	Entries []*MapEntry
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
			return participle.Errorf(tok.Pos, "unexpected token \"%s\" (expected \"}\")", tok.Value)
		}

		lex.Next()
		*m = MapLiteral{}
		return nil
	}

	entry, err := parseMapEntry(lex)
	if errors.Is(err, unableToParseMapKey) || errors.Is(err, missingMapKeyValueDelimiter) {
		lex.LoadCheckpoint(checkpoint)
		return participle.NextMatch
	}

	if err != nil {
		return participle.Errorf(tok.Pos, "%s \"%s\"", err.Error(), tok.Value)
	}

	mapLiteral := MapLiteral{Entries: []*MapEntry{entry}}

	for {
		if tok := lex.Peek(); tok.Value == "," {
			lex.Next()
		}

		if tok := lex.Peek(); tok.Value == "}" {
			lex.Next()
			break
		}

		entry, err := parseMapEntry(lex)
		if err != nil {
			tok := lex.Peek()
			return participle.Errorf(tok.Pos, "%s \"%s\"", err.Error(), tok.Value)
		}

		mapLiteral.Entries = append(mapLiteral.Entries, entry)
	}

	*m = mapLiteral
	return nil
}

var (
	missingMapKeyValueDelimiter = errors.New("missing map key/value delimiter")
	unableToParseMapKey         = errors.New("unable to parse map key")
	unableToParseMapValue       = errors.New("unable to parse map value")
)

func parseMapEntry(lex *participleLexer.PeekingLexer) (*MapEntry, error) {
	key, err := parseExpr(lex, 0)
	if err != nil {
		return nil, unableToParseMapKey
	}

	tok := lex.Peek()
	if tok.Value != ":" {
		return nil, missingMapKeyValueDelimiter
	}

	lex.Next()
	value, err := parseExpr(lex, 0)
	if err != nil {
		return nil, unableToParseMapValue
	}

	mapEntry := &MapEntry{Key: key, Value: value}
	return mapEntry, nil
}
