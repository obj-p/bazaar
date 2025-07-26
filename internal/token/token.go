package token

type TokenType int

const (
	ILLEGAL TokenType = iota
	EOF
	IDENTIFIER

	// Keywords
	COMPONENT
	DATA
	TEMPLATE

	// Literals
	BOOL
	INT
	DOUBLE
	STRING

	// Delimiters
	COMMA
	LPAREN
	RPAREN
	LBRACE
	RBRACE
)

type Token struct {
	Type    TokenType
	Literal string
}
