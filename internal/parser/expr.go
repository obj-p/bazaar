package parser

import (
	"github.com/alecthomas/participle/v2/lexer"
)

// Adapted from https://github.com/alecthomas/langx

type Expr struct{}

func (e *Expr) Parse(lex *lexer.PeekingLexer) error {
	return nil
}

type precedence struct {
	RightAssociative bool
	Priority         int
}

var opPrecedence = map[Op]precedence{
	OpAdd: {Priority: 1},
	OpSub: {Priority: 1},
	OpMul: {Priority: 2},
	OpDiv: {Priority: 2},
	OpMod: {Priority: 2},
}
