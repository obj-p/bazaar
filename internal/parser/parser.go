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
	Package *PackageDecl    `parser:"@@"`
	Decls   []*TopLevelDecl `parser:"@@*"`
}

type TopLevelDecl struct {
	Enum      *EnumDecl      `parser:"@@"`
	Component *ComponentDecl `parser:"| @@"`
	Function  *Function      `parser:"| @@"`
	Data      *DataDecl      `parser:"| @@"`
	Template  *TemplateDecl  `parser:"| @@"`
	Preview   *PreviewDecl   `parser:"| @@"`
}

type PackageDecl struct {
	Name string `parser:"'package' @Ident"`
}

type EnumDecl struct {
	Name  string   `parser:"'enum' @Ident"`
	Cases []string `parser:"'{' @Ident (',' @Ident)* '}'"`
}

type ComponentDecl struct {
	Name   string   `parser:"'component' @Ident"`
	Fields []*Field `parser:"'{' @@* '}'"`
}

type DataDecl struct {
	Name   string   `parser:"'data' @Ident"`
	Fields []*Field `parser:"'{' @@* '}'"`
}

type TemplateDecl struct {
	Name       string            `parser:"'template' @Ident"`
	Parameters []*Parameter      `parser:"'(' (@@ (',' @@)*)? ')'"`
	Statements []*BlockStatement `parser:"'{' (@@ (',' @@)*)? '}'"`
}

type PreviewDecl struct {
	Name        string        `parser:"'preview' @Ident"`
	Expressions []*Expression `parser:"'{' (@@ (',' @@)*)? '}'"`
}

type Field struct {
	Name     string        `parser:"@Ident"`
	Optional bool          `parser:"@'?'?"`
	Type     *TypeRef      `parser:"@@"`
	Default  *DefaultValue `parser:"('=' @@)?"`
}

type Function struct {
	Name       string       `parser:"'func' @Ident"`
	Parameters []*Parameter `parser:"'(' (@@ (',' @@)*)? ')'"`
	ReturnType *TypeRef     `parser:"('-' '>' @@)?"`
}

type Parameter struct {
	Name     string        `parser:"@Ident"`
	Optional bool          `parser:"@'?'?"`
	Type     *TypeRef      `parser:"@@"`
	Default  *DefaultValue `parser:"('=' @@)?"`
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

type CollectionLiteral struct{}

type DefaultValue struct {
	Literal *Literal `parser:"@@"`
	KeyPath *KeyPath `parser:"| @@"`
	// TODO: should this just be an expression?
}

type KeyPath struct {
	Implicit bool      `parser:"@'.'?"`
	Path     []*string `parser:"@Ident ('.' @Ident)*"`
}

type Callable struct {
	Name      string        `parser:"@Ident"`
	Arguments []*Expression `parser:"'(' (@@ (',' @@)*)? ')'"`
}

type Loop struct{}

type Expression struct {
	Literal *Literal `parser:"@@"`
	KeyPath *KeyPath `parser:"| @@"`
}

type BlockStatement struct {
	Callable *Callable `parser:"@@"`
}
