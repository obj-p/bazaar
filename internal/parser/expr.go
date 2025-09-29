package parser

import (
	"fmt"
	"github.com/alecthomas/participle/v2"
	participleLexer "github.com/alecthomas/participle/v2/lexer"
	bazaarLexer "github.com/obj-p/bazaar/internal/lexer"
	"github.com/obj-p/bazaar/internal/token"
)

// Interfaces to reduce duplication in parsing logic

// ExprNode represents any expression node
type ExprNode interface {
	IsControl() bool
}

// CallNode represents any call expression
type CallNode interface {
	ExprNode
	GetArguments() []*ArgumentExpr
}

// ReferenceNode represents any reference expression
type ReferenceNode interface {
	ExprNode
	GetName() *string
}

// ExpressionParser provides shared parsing functionality
type ExpressionParser struct{}

func (p *ExpressionParser) ValidateReference(node ReferenceNode) error {
	if node.GetName() == nil {
		return fmt.Errorf("reference name cannot be nil")
	}
	return nil
}

func (p *ExpressionParser) ValidateCall(node CallNode) error {
	return ValidateCallArguments(node.GetArguments())
}

// Adapted from https://github.com/alecthomas/langx

// TODO: likely need to differentiate between a control flow expression (no calls with trailing labmdas)
// and block level expressions. Perhaps Stmt should be BlockLevelExpr?

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
	Call       *CallExpr          `parser:"@@?"`
}

type ArgumentExpr struct {
	Name *string `parser:"(@Ident '=')?"`
	Expr *Expr   `parser:"@@"`
}

type CallExpr struct {
	Arguments []*ArgumentExpr `parser:"'(' (@@ (',' @@)* ','?)? ')'"`
}

func (c *CallExpr) IsControl() bool               { return false }
func (c *CallExpr) GetArguments() []*ArgumentExpr { return c.Arguments }

type CallableExpr struct {
	Annotations    []*AnnotationExpr `parser:"@@*"`
	Name           *string           `parser:"@Ident"`
	Arguments      []*ArgumentExpr   `parser:"(('(' (@@ (',' @@)* ','?)? ')'"`
	TrailingLambda *LambdaExpr       `parser:"@@?)"`
	LambdaOnly     *LambdaExpr       `parser:"| @@)"`
}

func (c *CallableExpr) IsControl() bool               { return false }
func (c *CallableExpr) GetArguments() []*ArgumentExpr { return c.Arguments }

// ControlCallExpr is the same as CallExpr but implements control context
type ControlCallExpr struct {
	Arguments []*ArgumentExpr `parser:"'(' (@@ (',' @@)* ','?)? ')'"`
}

func (c *ControlCallExpr) IsControl() bool               { return true }
func (c *ControlCallExpr) GetArguments() []*ArgumentExpr { return c.Arguments }

type KeyPathExpr struct {
	Optional  bool           `parser:"@'?'?"`
	Subscript *Expr          `parser:"('[' @@ ']'"`
	Reference *ReferenceExpr `parser:"| '.' @@"`
	Call      *CallExpr      `parser:"| @@)"`
	Next      *KeyPathExpr   `parser:"@@?"`
}

// ControlKeyPathExpr is like KeyPathExpr but uses ControlCallExpr
type ControlKeyPathExpr struct {
	Optional  bool                  `parser:"@'?'?"`
	Subscript *ControlExpr          `parser:"('[' @@ ']'"`
	Reference *ControlReferenceExpr `parser:"| '.' @@"`
	Call      *ControlCallExpr      `parser:"| @@)"`
	Next      *ControlKeyPathExpr   `parser:"@@?"`
}

type ReferenceExpr struct {
	Name    *string      `parser:"@Ident"`
	KeyPath *KeyPathExpr `parser:"@@?"`
}

func (r *ReferenceExpr) IsControl() bool  { return false }
func (r *ReferenceExpr) GetName() *string { return r.Name }

// ControlReferenceExpr is like ReferenceExpr but uses ControlKeyPathExpr
type ControlReferenceExpr struct {
	Name    *string             `parser:"@Ident"`
	KeyPath *ControlKeyPathExpr `parser:"@@?"`
}

func (r *ControlReferenceExpr) IsControl() bool  { return true }
func (r *ControlReferenceExpr) GetName() *string { return r.Name }

type PrimaryExpr struct {
	Literal   *Literal       `parser:"@@"`
	Reference *ReferenceExpr `parser:"| @@"`
	Callable  *CallableExpr  `parser:"| @@"`
	Lambda    *LambdaExpr    `parser:"| @@"`
	Nested    *Expr          `parser:"| '(' @@ ')'"`
}

// ControlPrimaryExpr is like PrimaryExpr but excludes lambda and uses ControlReferenceExpr
type ControlPrimaryExpr struct {
	Literal   *Literal              `parser:"@@"`
	Reference *ControlReferenceExpr `parser:"| @@"`
	Nested    *ControlExpr          `parser:"| '(' @@ ')'"`
}

type BinaryExpr struct {
	Left  *Expr
	Op    token.Op
	Right *Expr
}

// ControlBinaryExpr is like BinaryExpr but uses ControlExpr
type ControlBinaryExpr struct {
	Left  *ControlExpr
	Op    token.Op
	Right *ControlExpr
}

type UnaryExpr struct {
	Op      token.Op     `parser:"@('!' | '-')?"`
	Primary *PrimaryExpr `parser:"@@"`
}

// ControlUnaryExpr is like UnaryExpr but uses ControlPrimaryExpr
type ControlUnaryExpr struct {
	Op      token.Op            `parser:"@('!' | '-')?"`
	Primary *ControlPrimaryExpr `parser:"@@"`
}

type Expr struct {
	Unary  *UnaryExpr
	Binary *BinaryExpr
}

func (e *Expr) IsControl() bool { return false }

// ControlExpr is like Expr but excludes trailing lambda calls
type ControlExpr struct {
	Unary  *ControlUnaryExpr
	Binary *ControlBinaryExpr
}

func (e *ControlExpr) IsControl() bool { return true }

func (e *Expr) Parse(lex *participleLexer.PeekingLexer) error {
	ex, err := parseExpr(lex, 0)
	if err != nil {
		return err
	}
	*e = *ex
	return nil
}

func (e *ControlExpr) Parse(lex *participleLexer.PeekingLexer) error {
	ex, err := parseControlExpr(lex, 0)
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

var controlUnaryExprParser = participle.MustBuild[ControlUnaryExpr](
	participle.Lexer(bazaarLexer.BazaarLexer),
	participle.UseLookahead(1),
)

var annotationParser = participle.MustBuild[AnnotationExpr](
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

func parseControlExpr(lex *participleLexer.PeekingLexer, minPrec int) (*ControlExpr, error) {
	lhs, err := parseControlOperand(lex)
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

		binaryExpr := &ControlBinaryExpr{}
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

		rhs, err := parseControlExpr(lex, nextMinPrec)
		if err != nil {
			return nil, err
		}

		binaryExpr.Left = lhs
		binaryExpr.Right = rhs
		lhs = &ControlExpr{Binary: binaryExpr}
	}

	return lhs, nil
}

func parseControlOperand(lex *participleLexer.PeekingLexer) (*ControlExpr, error) {
	u, err := controlUnaryExprParser.ParseFromLexer(lex, participle.AllowTrailing(true))
	if err != nil {
		return nil, err
	}
	return &ControlExpr{Unary: u}, nil
}

// Shared helper functions to reduce duplication

// ValidateCallArguments validates call arguments for both CallExpr and ControlCallExpr
func ValidateCallArguments(args []*ArgumentExpr) error {
	// Shared validation logic
	for _, arg := range args {
		if arg.Expr == nil {
			return fmt.Errorf("argument expression cannot be nil")
		}
	}
	return nil
}

// ProcessCallArguments processes arguments for both call types
func ProcessCallArguments(node CallNode) ([]*ArgumentExpr, error) {
	args := node.GetArguments()
	if err := ValidateCallArguments(args); err != nil {
		return nil, err
	}
	return args, nil
}

// CreateBinaryExpr creates a binary expression with shared logic
func CreateBinaryExpr(left, right ExprNode, op token.Op) ExprNode {
	if left.IsControl() && right.IsControl() {
		return &ControlExpr{
			Binary: &ControlBinaryExpr{
				Left:  left.(*ControlExpr),
				Right: right.(*ControlExpr),
				Op:    op,
			},
		}
	} else {
		return &Expr{
			Binary: &BinaryExpr{
				Left:  left.(*Expr),
				Right: right.(*Expr),
				Op:    op,
			},
		}
	}
}

// ParseOperatorPrecedence provides shared precedence parsing logic
func ParseOperatorPrecedence(isControl bool, lex *participleLexer.PeekingLexer, minPrec int) (ExprNode, error) {
	if isControl {
		expr, err := parseControlExpr(lex, minPrec)
		return expr, err
	} else {
		expr, err := parseExpr(lex, minPrec)
		return expr, err
	}
}
