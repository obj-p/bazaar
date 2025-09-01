package parser

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
	Name   string       `parser:"'component' @Ident"`
	Fields []*FieldDecl `parser:"'{' @@* '}'"`
}

type DataDecl struct {
	Name   string       `parser:"'data' @Ident"`
	Fields []*FieldDecl `parser:"'{' @@* '}'"`
}

type FunctionDecl struct {
	Name       string           `parser:"'func' @Ident"`
	Parameters []*ParameterDecl `parser:"'(' (@@ (',' @@)* ','?)? ')'"`
	Return     *TypeDecl        `parser:"('-' '>' @@)?"`
	Block      []*Stmt          `parser:"('{' @@* '}')?"`
}

type TemplateDecl struct {
	Name       string           `parser:"'template' @Ident"`
	Parameters []*ParameterDecl `parser:"('(' (@@ (',' @@)* ','?)? ')')?"`
	Block      []*Stmt          `parser:"('{' @@* '}')"`
}

type PreviewDecl struct {
	Name  string  `parser:"'preview' @Ident"`
	Block []*Stmt `parser:"('{' @@* '}')"`
}

type FieldDecl struct {
	Name    string    `parser:"@Ident"`
	Type    *TypeDecl `parser:"@@"`
	Default *Expr     `parser:"('=' @@)?"`
}

type ParameterDecl struct {
	Name    string    `parser:"@Ident"`
	Type    *TypeDecl `parser:"@@"`
	Default *Expr     `parser:"('=' @@)?"`
}

type TypeDecl struct {
	Function *FunctionTypeDecl `parser:"(@@"`
	Array    *ArrayTypeDecl    `parser:"| @@"`
	Map      *MapTypeDecl      `parser:"| @@"`
	Value    *string           `parser:"| (@'component' | @Ident)"`
	Nested   *TypeDecl         `parser:"| '(' @@ ')')"`
	Optional bool              `parser:"@'?'?"`
}

type FunctionTypeDecl struct {
	Parameters []*TypeDecl `parser:"'func' '(' (@@ (',' @@)* ','?)? ')'"`
	Return     *TypeDecl   `parser:"('-' '>' @@)?"`
}

type ArrayTypeDecl struct {
	Value TypeDecl `parser:"'[' @@ ']'"`
}

type MapTypeDecl struct {
	Key   TypeDecl `parser:"('{' @@)"`
	Value TypeDecl `parser:"':' @@ '}'"`
}
