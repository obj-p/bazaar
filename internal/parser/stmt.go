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

type ForInDest struct {
	First  *string `parser:"@Ident"`
	Second *string `parser:"(',' @Ident)?"`
}

type ForInSource struct {
	IsEnumerated bool  `parser:"@'enumerate'?"`
	Source       *Expr `parser:"@@"`
}

type ForInStmt struct {
	Dest   *ForInDest   `parser:"'for' @@"`
	Source *ForInSource `parser:"'in' @@ '{'"`
	Block  []*Stmt      `parser:"@@* '}'"`
}

type IfFragment struct {
	Var         *VarDeclStmt `parser:"@@"`
	ImplicitVar *string      `parser:"| 'var' @Ident"`
	Expr        *Expr        `parser:"| @@"`
}

type IfStmt struct {
	Fragments []*IfFragment `parser:"'if' (@@ (',' @@)* ','?) '{'"`
	Block     []*Stmt       `parser:"@@* '}'"`
}

type ReturnStmt struct {
	Value *Expr `parser:"'return' @@?"`
}

type Stmt struct {
	Var            *VarDeclStmt        `parser:"@@"`
	Assign         *AssignStmt         `parser:"| @@"`
	ForIn          *ForInStmt          `parser:"| @@"`
	If             *IfStmt             `parser:"| @@"`
	TrailingLambda *TrailingLambdaExpr `parser:"| @@"`
	Return         *ReturnStmt         `parser:"| @@"`
	Expr           *Expr               `parser:"| @@"`
}
