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
	Return     *TypeExpr    `parser:"('-' '>' @@)?"`
	Block      *Block       `parser:"('{' @@? '}')?"`
}

type TemplateDecl struct {
	Name       string       `parser:"'template' @Ident"`
	Parameters []*Parameter `parser:"'(' (@@ (',' @@)*)? ')'"`
	Block      *Block       `parser:"('{' @@? '}')?"`
}

type PreviewDecl struct {
	Name  string  `parser:"'preview' @Ident"`
	Exprs []*Expr `parser:"'{' (@@ (',' @@)*)? '}'"`
}

type Field struct {
	Name    string    `parser:"@Ident"`
	Type    *TypeExpr `parser:"@@"`
	Default *Expr     `parser:"('=' @@)?"`
}

type Parameter struct {
	Name    string    `parser:"@Ident"`
	Type    *TypeExpr `parser:"@@"`
	Default *Expr     `parser:"('=' @@)?"`
}

type TypeDecl struct {
	Function *FunctionTypeDecl `parser:"@@"`
	Array    *ArrayTypeDecl    `parser:"| @@"`
	Map      *MapTypeDecl      `parser:"| @@"`
	Expr     *TypeExpr         `parser:"| '(' @@ ')'"`
	Value    *string           `parser:"| @Ident"`
}

type FunctionTypeDecl struct {
	Parameters []*TypeExpr `parser:"'func' '(' (@@ (',' @@)* ','?)? ')'"`
	Return     *TypeExpr   `parser:"('-' '>' @@)?"`
}

type ArrayTypeDecl struct {
	Value TypeExpr `parser:"'[' @@ ']'"`
}

type MapTypeDecl struct {
	Key   TypeExpr `parser:"('{' @@)"`
	Value TypeExpr `parser:"':' @@ '}'"`
}

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
	Entries []*MapEntry `parser:"'{' (':' | (@@ (',' @@)* ','?)) '}'"`
}

type TypeExpr struct {
	Decl     TypeDecl `parser:"@@"`
	Optional bool     `parser:"@'?'?"`
}

type CallableExpr struct {
	Name      string  `parser:"@Ident"`
	Arguments []*Expr `parser:"'(' (@@ (',' @@)* ','?)? ')'"`
}

type KeyPathExpr struct {
	Implicit bool `parser:"@'.'?"`
	// Receiver *string   `parser:"@"`
	// Next
	Path []*string `parser:"@Ident ('.' @Ident)*"`
}

type PrimaryExpr struct {
	Callable *CallableExpr `parser:"@@"`
	Literal  *Literal      `parser:"| @@"`
	KeyPath  *KeyPathExpr  `parser:"| @@"`
}

type Expr struct {
	Primary *PrimaryExpr `parser:"@@"`
}

type Stmt struct {
	Callable *CallableExpr `parser:"@@"`
}

type Block struct {
	Stmts []*Stmt `parser:"@@*"`
}
