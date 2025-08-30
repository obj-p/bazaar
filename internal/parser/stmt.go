package parser

type ForStmt struct {
	Key    *string `parser:"'for' @Ident"`
	Source *Expr   `parser:"'in' @@"`
	Block  *Block  `parser:"@@"`
}

type Stmt struct {
	For  *ForStmt `parser:"@@"`
	Expr *Expr    `parser:"| @@"`
}

type Block struct {
	Stmts []*Stmt `parser:"'{' @@* '}'"`
}
