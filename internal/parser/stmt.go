package parser

type AssignStmt struct {
	Name *string `parser:"'var' @Ident '='"`
	Expr *Expr   `parser:"@@"`
}

type IfStmt struct {
	Assign *AssignStmt `parser:"'if' (@@"`
	Expr   *Expr       `parser:"| @@)"`
	Block  *Block      `parser:"@@"`
}

type ForStmt struct {
	Key    *string `parser:"'for' @Ident"`
	Source *Expr   `parser:"'in' @@"`
	Block  *Block  `parser:"@@"`
}

type Stmt struct {
	For  *ForStmt `parser:"@@"`
	If   *IfStmt  `parser:"| @@"`
	Expr *Expr    `parser:"| @@"`
}

type Block struct {
	Stmts []*Stmt `parser:"'{' @@* '}'"`
}
