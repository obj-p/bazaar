package parser

import (
	"github.com/alecthomas/participle/v2"
	participleLexer "github.com/alecthomas/participle/v2/lexer"
	bazaarLexer "github.com/obj-p/bazaar/internal/lexer"
	"github.com/obj-p/bazaar/internal/token"
)

// Adapted from https://github.com/alecthomas/langx

type AnnotationExpr struct {
	Name *string   `parser:"'@' @Ident"`
	Call *CallExpr `parser:"@@?"`
}

type DestructuringExpr struct {
	Names []string `parser:"'(' @Ident (',' @Ident)* ','? ')'"`
}

type VariableExpr struct {
	Name          *string            `parser:"@Ident"`
	Destructuring *DestructuringExpr `parser:"| @@"`
}

type LambdaParameter struct {
	Name string    `parser:"@Ident"`
	Type *TypeDecl `parser:"@@?"`
}

type LambdaExpr struct {
	Parameters []*LambdaParameter `parser:"'{' ('(' (@@ (',' @@)* ','?)? ')'"`
	Return     *TypeDecl          `parser:"('-' '>' @@)?"`
	Stmts      []*Stmt            `parser:"'in')? @@* '}'"`
}

type ArgumentExpr struct {
	Name *string `parser:"(@Ident '=')?"`
	Expr *Expr   `parser:"@@"`
}

type CallExpr struct {
	Annotation *AnnotationExpr `parser:"@@?"`
	Arguments  []*ArgumentExpr `parser:"'(' (@@ (',' @@)* ','?)? ')'"`
}

type KeyPathExpr struct {
	Optional  bool           `parser:"@'?'?"`
	Subscript *Expr          `parser:"('[' @@ ']'"`
	Reference *ReferenceExpr `parser:"| '.' @@"`
	Call      *CallExpr      `parser:"| @@)"`
	Next      *KeyPathExpr   `parser:"@@?"`
}

type ReferenceExpr struct {
	Name    *string      `parser:"@Ident"`
	KeyPath *KeyPathExpr `parser:"@@?"`
}

type PrimaryExpr struct {
	Literal   *Literal       `parser:"@@"`
	Reference *ReferenceExpr `parser:"| @@"`
	Lambda    *LambdaExpr    `parser:"| @@"`
	Nested    *Expr          `parser:"| '(' @@ ')'"`
}

type BinaryExpr struct {
	Left  *Expr
	Op    token.Op
	Right *Expr
}

type UnaryExpr struct {
	Op      token.Op     `parser:"@('!' | '-')?"`
	Primary *PrimaryExpr `parser:"@@"`
}

type Expr struct {
	Unary  *UnaryExpr
	Binary *BinaryExpr
}

func (e *Expr) Parse(lex *participleLexer.PeekingLexer) error {
	ex, err := parseExpr(lex, 0)
	if err != nil {
		return err
	}
	*e = *ex
	return nil
}

type precedence struct {
	RightAssociative bool
	Priority         int
}

var opPrecedence = map[token.Op]precedence{
	token.OpCoalesce: {Priority: 0, RightAssociative: true},
	token.OpAdd:      {Priority: 1},
	token.OpSub:      {Priority: 1},
	token.OpMul:      {Priority: 2},
	token.OpDiv:      {Priority: 2},
	token.OpMod:      {Priority: 2},
	token.OpPow:      {Priority: 3, RightAssociative: true},
}

var unaryExprParser = participle.MustBuild[UnaryExpr](
	participle.Lexer(bazaarLexer.BazaarLexer),
	participle.UseLookahead(1),
)

func parseExpr(lex *participleLexer.PeekingLexer, minPrec int) (*Expr, error) {
	lhs, err := parseOperand(lex)
	if err != nil {
		return nil, err
	}

	for {
		tok := lex.Peek()
		if tok.EOF() {
			break
		}

		if tok.Type != bazaarLexer.LogicOperatorToken && tok.Type != bazaarLexer.OperatorToken {
			break
		}

		binaryExpr := &BinaryExpr{}
		err = binaryExpr.Op.Capture([]string{tok.Value})
		if err != nil {
			return lhs, nil
		}

		if opPrecedence[binaryExpr.Op].Priority < minPrec {
			break
		}

		lex.Next()
		nextMinPrec := opPrecedence[binaryExpr.Op].Priority
		if !opPrecedence[binaryExpr.Op].RightAssociative {
			nextMinPrec++
		}

		rhs, err := parseExpr(lex, nextMinPrec)
		if err != nil {
			return nil, err
		}

		binaryExpr.Left = lhs
		binaryExpr.Right = rhs
		lhs = &Expr{Binary: binaryExpr}
	}

	return lhs, nil
}

func parseOperand(lex *participleLexer.PeekingLexer) (*Expr, error) {
	u, err := unaryExprParser.ParseFromLexer(lex, participle.AllowTrailing(true))
	if err != nil {
		return nil, err
	}
	return &Expr{Unary: u}, nil
}
