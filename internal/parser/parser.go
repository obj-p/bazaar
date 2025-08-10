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
	Imports []*ImportDecl   `parser:"@@*"`
	Decls   []*TopLevelDecl `parser:"@@*"`
}

type TopLevelDecl struct {
	Enum      *EnumDecl      `parser:"@@"`
	Component *ComponentDecl `parser:"| @@"`
	Data      *DataDecl      `parser:"| @@"`
	Function  *FunctionDecl  `parser:"| @@"`
	Template  *TemplateDecl  `parser:"| @@"`
	Preview   *PreviewDecl   `parser:"| @@"`
}

type PackageDecl struct {
	Domain []string `parser:"'package' @Ident ('.' @Ident)*"`
}

type ImportDecl struct {
	Domain []string `parser:"'import' @Ident ('.' @Ident)*"`
}

type EnumDecl struct {
	Name  string   `parser:"'enum' @Ident"`
	Cases []string `parser:"'{' (@Ident (',' @Ident)* ','?)? '}'"`
}

type ComponentDecl struct {
	Name   string   `parser:"'component' @Ident"`
	Fields []*Field `parser:"'{' @@* '}'"`
}

type DataDecl struct {
	Name   string   `parser:"'data' @Ident"`
	Fields []*Field `parser:"'{' @@* '}'"`
}

type FunctionDecl struct {
	Name       string       `parser:"'func' @Ident"`
	Parameters []*Parameter `parser:"'(' (@@ (',' @@)*)? ')'"`
	ReturnType *TypeRef     `parser:"('-' '>' @@)?"`
	// TODO: body
}

type TemplateDecl struct {
	Name       string       `parser:"'template' @Ident"`
	Parameters []*Parameter `parser:"'(' (@@ (',' @@)*)? ')'"`
	Statements []*BlockStmt `parser:"'{' (@@ (',' @@)*)? '}'"`
}

type PreviewDecl struct {
	Name        string      `parser:"'preview' @Ident"`
	Expressions []*ExprStmt `parser:"'{' (@@ (',' @@)*)? '}'"`
}

type Field struct {
	Name     string   `parser:"@Ident"`
	Optional bool     `parser:"@'?'?"`
	Type     *TypeRef `parser:"@@"`
	Default  *Expr    `parser:"('=' @@)?"`
}

type Parameter struct {
	Name     string   `parser:"@Ident"`
	Optional bool     `parser:"@'?'?"`
	Type     *TypeRef `parser:"@@"`
	Default  *Expr    `parser:"('=' @@)?"`
}

type TypeRef struct {
	FunctionType *FunctionRef `parser:"@@"`
	ArrayType    *ArrayRef    `parser:"| @@"`
	MapType      *MapRef      `parser:"| @@"`
	ValueType    *string      `parser:"| @Ident"`
}

type FunctionRef struct {
	ParameterTypes []*TypeRef `parser:"'func' '(' (@@ (',' @@)* ','?)? ')'"`
	ReturnType     *TypeRef   `parser:"('-' '>' @@)?"`
}

type ArrayRef struct {
	ValueType TypeRef `parser:"'[' ']' @@"`
}

type MapRef struct {
	KeyType   TypeRef `parser:"('[' @@ ']')"`
	ValueType TypeRef `parser:"@@"`
}

type Bool bool

func (b *Bool) Capture(values []string) error {
	*b = values[0] == "true"
	return nil
}

type StringFragment struct {
	Esc  *string   `parser:"@StringEsc"`
	Expr *ExprStmt `parser:"| '${' @@ '}'"`
	Text *string   `parser:"| @StringText"`
}

type String struct {
	Fragments []*StringFragment `parser:"\"\\\"\" @@* \"\\\"\""`
}

type Literal struct {
	Nil    bool          `parser:"@'nil'"`
	Bool   *Bool         `parser:"| @('true' | 'false')"`
	Float  *string       `parser:"| @Float"`
	Int    *string       `parser:"| @Int"`
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
	Entries []*MapEntry `parser:"'[' (':' | (@@ (',' @@)* ','?)) ']'"`
}

type CallableExpr struct {
	Name      string      `parser:"@Ident"`
	Arguments []*ExprStmt `parser:"'(' (@@ (',' @@)* ','?)? ')'"`
}

type KeyPathExpr struct {
	Implicit bool      `parser:"@'.'?"`
	Path     []*string `parser:"@Ident ('.' @Ident)*"`
}

type UnaryExpr struct {
	Callable *CallableExpr `parser:"@@"`
	Literal  *Literal      `parser:"| @@"`
	KeyPath  *KeyPathExpr  `parser:"| @@"`
}

type Expr struct {
	Unary *UnaryExpr `parser:"@@"`
}

type ExprStmt struct {
	Literal *Literal     `parser:"@@"`
	KeyPath *KeyPathExpr `parser:"| @@"`
}

type BlockStmt struct {
	Callable *CallableExpr `parser:"@@"`
}
