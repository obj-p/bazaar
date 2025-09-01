package parser

type AssignStmt struct {
	Name *string `parser:"'var' @Ident '='"`
	Expr *Expr   `parser:"@@"`
}

type ForInStmt struct {
	Key    *string `parser:"'for' @Ident"`
	Source *Expr   `parser:"'in' @@ '{'"`
	Block  []*Stmt `parser:"@@* '}'"`
}

type IfBindingStmt struct {
	Assign *AssignStmt `parser:"'if' @@ '{'"`
	Block  []*Stmt     `parser:"@@* '}'"`
}

type IfStmt struct {
	Expr  *Expr   `parser:"'if' @@ '{'"`
	Block []*Stmt `parser:"@@* '}'"`
}

type Stmt struct {
	Assign         *AssignStmt         `parser:"@@"`
	ForIn          *ForInStmt          `parser:"| @@"`
	If             *IfStmt             `parser:"| @@"`
	IfBinding      *IfBindingStmt      `parser:"| @@"`
	TrailingLambda *TrailingLambdaExpr `parser:"| @@"`
	Expr           *Expr               `parser:"| @@"`
}
