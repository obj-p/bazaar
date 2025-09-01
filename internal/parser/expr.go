package parser

import (
	"github.com/alecthomas/participle/v2"
	participleLexer "github.com/alecthomas/participle/v2/lexer"
	bazaarLexer "github.com/obj-p/bazaar/internal/lexer"
	"github.com/obj-p/bazaar/internal/token"
)

// Adapted from https://github.com/alecthomas/langx

type CallExpr struct {
	Parameters []*Expr `parser:"'(' (@@ (',' @@)* ','?)? ')'"`
	Block      *Block  `parser:"@@?"`
}

type KeyPathExpr struct {
	Subscript *Expr        `parser:"('[' @@ ']'"`
	Reference *string      `parser:"| ('.' @Ident)"`
	Call      *CallExpr    `parser:"| @@)"`
	Optional  bool         `parser:"'?'?"`
	Next      *KeyPathExpr `parser:"@@?"`
}

type ReferenceExpr struct {
	Literal  *Literal `parser:"@@"`
	Implicit bool     `parser:"| (@'.'?"`
	Ident    *string  `parser:"@Ident)"`
}

type PrimaryExpr struct {
	Reference *ReferenceExpr `parser:"@@"`
	KeyPath   *KeyPathExpr   `parser:"@@?"`
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
	token.OpAdd: {Priority: 1},
	token.OpSub: {Priority: 1},
	token.OpMul: {Priority: 2},
	token.OpDiv: {Priority: 2},
	token.OpMod: {Priority: 2},
	// TODO(jason.prasad): other Op
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
