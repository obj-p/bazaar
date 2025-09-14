package parser

import "github.com/obj-p/bazaar/internal/token"

type Destructuring struct {
	Names []*string `parser:"@Ident | '(' @Ident (',' @Ident)? ','? ')'"`
}

type VarDeclStmt struct {
	Dest   *Destructuring `parser:"'var' @@"`
	Type   *TypeDecl      `parser:"@@?"`
	Source *Expr          `parser:"'=' @@"`
}

type AssignStmt struct {
	Name  *string   `parser:"@Ident"`
	Op    *token.Op `parser:"@AssignOperator"`
	Value *Expr     `parser:"@@"`
}

type ForStmt struct {
	Dest   *Destructuring `parser:"'for' ((@@"`
	Source *Expr          `parser:"'in' @@)"`
	Expr   *Expr          `parser:"| @@)"`
	Block  []*Stmt        `parser:"'{' @@* '}'"`
}

type IfFragment struct {
	Var         *VarDeclStmt `parser:"@@"`
	ImplicitVar *string      `parser:"| 'var' @Ident"`
	Expr        *Expr        `parser:"| @@"`
}

type IfStmt struct {
	Fragments []*IfFragment `parser:"'if' (@@ (',' @@)* ','?)"`
	Block     []*Stmt       `parser:"'{' @@* '}'"`
}

type SwitchCase struct {
	Expr  *Expr   `parser:"'case' @@ ':'"`
	Block []*Expr `parser:"@@*"`
}

type SwitchBody struct {
	Cases   *SwitchCase `parser:"@@*"`
	Default []*Expr     `parser:"('default' ':' @@*)"`
}

type SwitchStmt struct {
	Expr *Expr       `parser:"'switch' @@"`
	Body *SwitchBody `parser:"'{' @@ '}'"`
}

type ReturnStmt struct {
	Value *Expr `parser:"'return' @@?"`
}

type CallStmt struct {
	Call           *CallExpr   `parser:"@@"`
	TrailingLambda *LambdaExpr `parser:"@@?"`
}

type Stmt struct {
	Var    *VarDeclStmt `parser:"@@"`
	Assign *AssignStmt  `parser:"| @@"`
	For    *ForStmt     `parser:"| @@"`
	If     *IfStmt      `parser:"| @@"`
	Switch *SwitchStmt  `parser:"| @@"`
	Return *ReturnStmt  `parser:"| @@"`
	Call   *CallStmt    `parser:"| @@"`
	Expr   *Expr        `parser:"| @@"`
}
