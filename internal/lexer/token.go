package lexer

type TokenType int

const (
	ILLEGAL TokenType = iota
	EOF
	IDENTIFIER

	// Keywords
	COMPONENT
	DATA
	FUNCTION
	TEMPLATE

	// Literals
	BOOL
	INT
	DOUBLE
	STRING

	// Punctuation
	ASSIGN
	COMMA
	LBRACK
	RBRACK
	LPAREN
	RPAREN
	LBRACE
	RBRACE
)

type Token struct {
	Type    TokenType
	Literal string
}

var keywords = map[string]TokenType{
	"component": COMPONENT,
}

func LookupIdentifier(identifier string) TokenType {
	if tok, ok := keywords[identifier]; ok {
		return tok
	}

	return IDENTIFIER
}
