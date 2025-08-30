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
	Unary *UnaryExpr
	// Binary *BinaryExpr
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
	// TODO: other Op
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
		token := lex.Peek()
		if token.EOF() {
			break
		}
		operator, err := parseOperator(lex)
		if err != nil || operator == nil {
			break
		}

		break
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

func parseOperator(lex *participleLexer.PeekingLexer) (*token.Op, error) {
	return nil, nil
}
