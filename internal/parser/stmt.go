package parser

import "github.com/obj-p/bazaar/internal/token"

type VarDeclStmt struct {
	Annotation *AnnotationExpr    `parser:"@@?"`
	Dest       *DestructuringExpr `parser:"'var' @@"`
	Type       *TypeDecl          `parser:"@@?"`
	Source     *Expr              `parser:"'=' @@"`
}

type AssignStmt struct {
	Name  *string   `parser:"@Ident"`
	Op    *token.Op `parser:"@AssignOperator"`
	Value *Expr     `parser:"@@"`
}

type ForStmt struct {
	Dest   *DestructuringExpr `parser:"'for' ((@@"`
	Source *Expr              `parser:"'in' @@)"`
	Expr   *Expr              `parser:"| @@)"`
	Block  []*Stmt            `parser:"'{' @@* '}'"`
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
	Block []*Stmt `parser:"@@*"`
}

type SwitchBody struct {
	Cases   []*SwitchCase `parser:"@@*"`
	Default []*Stmt       `parser:"('default' ':' @@*)"`
}

type SwitchStmt struct {
	Expr *Expr       `parser:"'switch' @@"`
	Body *SwitchBody `parser:"'{' @@ '}'"`
}

type ReturnStmt struct {
	Value *Expr `parser:"'return' @@?"`
}

type CallStmt struct {
	Annotation     *AnnotationExpr `parser:"@@?"`
	Name           *string         `parser:"@Ident"`
	Arguments      []*ArgumentExpr `parser:"(('(' (@@ (',' @@)* ','?)? ')'"`
	TrailingLambda *LambdaExpr     `parser:"@@?)"`
	LambdaOnly     *LambdaExpr     `parser:"| @@)"`
}

type Stmt struct {
	Call   *CallStmt    `parser:"@@"`
	Var    *VarDeclStmt `parser:"| @@"`
	Assign *AssignStmt  `parser:"| @@"`
	For    *ForStmt     `parser:"| @@"`
	If     *IfStmt      `parser:"| @@"`
	Switch *SwitchStmt  `parser:"| @@"`
	Return *ReturnStmt  `parser:"| @@"`
	Expr   *Expr        `parser:"| @@"`
}
