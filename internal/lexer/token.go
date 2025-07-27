package lexer

type TokenType int

const (
	ILLEGAL TokenType = iota
	EOF
	IDENTIFIER

	// Keywords
	COMPONENT
	DATA
	ENUM
	FUNCTION
	NULL
	TEMPLATE

	// Control flow
	FOR
	IF
	IN
	LET

	// Literals
	BOOL
	INT
	DOUBLE
	STRING

	// Punctuation
	ASSIGN
	COMMA
	DOT
	LBRACK
	RBRACK
	LPAREN
	RPAREN
	LBRACE
	RBRACE
	QUESTION
)

type Token struct {
	Type    TokenType
	Literal string
}

var keywords = map[string]TokenType{
	"bool":      BOOL,
	"component": COMPONENT,
	"data":      DATA,
	"double":    DOUBLE,
	"enum":      ENUM,
	"for":       FOR,
	"function":  FUNCTION,
	"if":        IF,
	"in":        IN,
	"int":       INT,
	"let":       LET,
	"null":      NULL,
	"string":    STRING,
	"template":  TEMPLATE,
}

func LookupIdentifier(identifier string) TokenType {
	if tok, ok := keywords[identifier]; ok {
		return tok
	}

	return IDENTIFIER
}
