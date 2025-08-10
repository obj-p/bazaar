package parser

import (
	"github.com/alecthomas/participle/v2"
	"github.com/obj-p/bazaar/internal/lexer"
)

var BazaarParser = participle.MustBuild[Bazaar](
	participle.Lexer(lexer.BazaarLexer),
	participle.Elide("Comment", "StringExprWhitespace", "Whitespace"),
	participle.UseLookahead(2),
)

type Bazaar struct {
	Package    *Package             `parser:"@@"`
	Statements []*TopLevelStatement `parser:"@@*"`
}

type TopLevelStatement struct {
	Enum      *Enum      `parser:"@@"`
	Component *Component `parser:"| @@"`
	Function  *Function  `parser:"| @@"`
	Data      *Data      `parser:"| @@"`
	Template  *Template  `parser:"| @@"`
	Preview   *Preview   `parser:"| @@"`
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

type Template struct {
	Name       string            `parser:"'template' @Ident"`
	Parameters []*Parameter      `parser:"'(' (@@ (',' @@)*)? ')'"`
	Statements []*BlockStatement `parser:"'{' (@@ (',' @@)*)? '}'"`
}

type Preview struct {
	Name        string        `parser:"'preview' @Ident"`
	Expressions []*Expression `parser:"'{' (@@ (',' @@)*)? '}'"`
}

type Field struct {
	Name     string       `parser:"@Ident"`
	Optional bool         `parser:"@'?'?"`
	Type     *TypeRef     `parser:"@@"`
	Default  *StaticValue `parser:"('=' @@)?"`
}

type Function struct {
	Name       string       `parser:"'func' @Ident"`
	Parameters []*Parameter `parser:"'(' (@@ (',' @@)*)? ')'"`
	ReturnType *TypeRef     `parser:"('-' '>' @@)?"`
}

type Parameter struct {
	Name     string       `parser:"@Ident"`
	Optional bool         `parser:"@'?'?"`
	Type     *TypeRef     `parser:"@@"`
	Default  *StaticValue `parser:"('=' @@)?"`
}

type TypeRef struct {
	FunctionType   *FunctionRef   `parser:"@@"`
	CollectionType *CollectionRef `parser:"| @@"`
	ValueType      *string        `parser:"| @Ident"`
}

type FunctionRef struct {
	Parameters []*Parameter `parser:"'func' '(' (@@ (',' @@)*)? ')'"`
	ReturnType *TypeRef     `parser:"('-' '>' @@)?"`
}

type CollectionRef struct {
	KeyType   *string `parser:"('[' @Ident? ']')"`
	ValueType *string `parser:"@Ident"`
}

type StaticValue struct {
	Literal *Literal `parser:"@@"`
	Member  *string  `parser:"| '.' @Ident"`
}

type Bool bool

func (b *Bool) Capture(values []string) error {
	*b = values[0] == "true"
	return nil
}

type StringFragment struct {
	Esc  *string     `parser:"@StringEsc"`
	Expr *Expression `parser:"| '${' @@ '}'"`
	Text *string     `parser:"| @StringText"`
}

type String struct {
	Fragments []*StringFragment `parser:"\"\\\"\" @@* \"\\\"\""`
}

type Literal struct {
	Nil    bool    `parser:"@'nil'"`
	Bool   *Bool   `parser:"| @('true' | 'false')"`
	Float  *string `parser:"| @Float"`
	Int    *string `parser:"| @Int"`
	String *String `parser:"| @@"`
}

type BlockStatement struct {
	Invocation *Invocation `parser:"@@"`
}

type Invocation struct {
	Name      string        `parser:"@Ident"`
	Arguments []*Expression `parser:"'(' (@@ (',' @@)*)? ')'"`
}

type Loop struct{}

type Expression struct {
	Literal   *Literal `parser:"@@"`
	Identifer *string  `parser:"| @Ident"`
}
