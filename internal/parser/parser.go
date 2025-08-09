package parser

import (
	"github.com/alecthomas/participle/v2"
	"github.com/obj-p/bazaar/internal/lexer"
)

var BazaarParser = participle.MustBuild[Bazaar](
	participle.Lexer(lexer.BazaarLexer),
	participle.Elide("Comment", "Whitespace"),
	participle.UseLookahead(2),
)

type Bazaar struct {
	Statement []*Statement `parser:"@@*"`
}

type Statement struct {
	Package   *Package   `parser:"@@"`
	Enum      *Enum      `parser:"| @@"`
	Component *Component `parser:"| @@"`
	Function  *Function  `parser:"| @@"`
	Data      *Data      `parser:"| @@"`
}

type Package struct {
	Name string `parser:"'package' @Ident"`
}

type Enum struct {
	Name  string   `parser:"'enum' @Ident"`
	Cases []string `parser:"'{' @Ident (',' @Ident)* '}'"`
}

type Component struct {
	Name   string   `parser:"'component' @Ident"`
	Fields []*Field `parser:"'{' @@* '}'"`
}

type Data struct {
	Name   string   `parser:"'data' @Ident"`
	Fields []*Field `parser:"'{' @@* '}'"`
}

type Field struct {
	Name     string   `parser:"@Ident"`
	Optional bool     `parser:"@'?'?"`
	Type     *TypeRef `parser:"@@"`
	Default  *Value   `parser:"('=' @@)?"`
}

type Function struct {
	Name       string      `parser:"'func' @Ident"`
	Arguments  []*Argument `parser:"'(' (@@ (',' @@)*)? ')'"`
	ReturnType *TypeRef    `parser:"('-' '>' @@)?"`
}

type Argument struct {
	Name     string   `parser:"@Ident"`
	Optional bool     `parser:"@'?'?"`
	Type     *TypeRef `parser:"@@"`
	Default  *Value   `parser:"('=' @@)?"`
}

type TypeRef struct {
	FunctionType   *FunctionRef   `parser:"@@"`
	CollectionType *CollectionRef `parser:"| @@"`
	ValueType      *string        `parser:"| @Ident"`
}

type FunctionRef struct {
	Arguments  []*Argument `parser:"'func' '(' (@@ (',' @@)*)? ')'"`
	ReturnType *TypeRef    `parser:"('-' '>' @@)?"`
}

type CollectionRef struct {
	KeyType   *string `parser:"('[' @Ident? ']')"`
	ValueType *string `parser:"@Ident"`
}

type Fragment struct {
	Escaped string `parser:"@Escaped"`
	Text    string `parser:"| @Char"`
}

type String struct {
	Fragments []*Fragment `parser:"\"\\\"\" @@* \"\\\"\""`
}

type Value struct {
	Literal *Literal `parser:"@@"`
	Symbol  *Symbol  `parser:"| @@"`
}

type Bool bool

func (b *Bool) Capture(values []string) error {
	*b = values[0] == "true"
	return nil
}

type Literal struct {
	Nil    bool    `parser:"@'nil'"`
	Bool   *Bool   `parser:"| @('true' | 'false')"`
	Float  *string `parser:"| @Float"`
	Int    *string `parser:"| @Int"`
	String *String `parser:"| @@"`
}

type Symbol struct {
	Implicit bool    `parser:"@'.'?"`
	Name     *string `parser:"@Ident"`
}
