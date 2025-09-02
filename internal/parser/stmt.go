package parser

import "github.com/obj-p/bazaar/internal/token"

type VarDeclStmt struct {
	Name *string   `parser:"'var' @Ident"`
	Type *TypeDecl `parser:"@@?"`
	Expr *Expr     `parser:"'=' @@"`
}

type AssignStmt struct {
	Name  *string   `parser:"@Ident"`
	Op    *token.Op `parser:"@AssignOperator"`
	Value *Expr     `parser:"@@"`
}

type ForInStmt struct {
	Key    *string `parser:"'for' @Ident"`
	Source *Expr   `parser:"'in' @@ '{'"`
	Block  []*Stmt `parser:"@@* '}'"`
}

type IfBindingStmt struct {
	VarDecl *VarDeclStmt `parser:"'if' @@ '{'"`
	Block   []*Stmt      `parser:"@@* '}'"`
}

type IfStmt struct {
	Expr  *Expr   `parser:"'if' @@ '{'"`
	Block []*Stmt `parser:"@@* '}'"`
}

type ReturnStmt struct {
	Value *Expr `parser:"'return' @@?"`
}

type Stmt struct {
	VarDecl        *VarDeclStmt        `parser:"@@"`
	Assign         *AssignStmt         `parser:"| @@"`
	ForIn          *ForInStmt          `parser:"| @@"`
	If             *IfStmt             `parser:"| @@"`
	IfBinding      *IfBindingStmt      `parser:"| @@"`
	TrailingLambda *TrailingLambdaExpr `parser:"| @@"`
	Return         *ReturnStmt         `parser:"| @@"`
	Expr           *Expr               `parser:"| @@"`
}
