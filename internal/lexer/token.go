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
	FUNC
	IMPORT
	MODIFIER
	NIL
	PACKAGE
	RETURN
	TEMPLATE

	// Control flow
	FOR
	IF
	IN
	LET
	MATCH

	// Literals
	BOOL
	INT
	DOUBLE
	STRING

	// Operators
	PLUS

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
	"func":      FUNC,
	"if":        IF,
	"import":    IMPORT,
	"in":        IN,
	"int":       INT,
	"let":       LET,
	"match":     MATCH,
	"modifier":  MODIFIER,
	"nil":       NIL,
	"package":   PACKAGE,
	"return":    RETURN,
	"string":    STRING,
	"template":  TEMPLATE,
}

func LookupIdentifier(identifier string) TokenType {
	if tok, ok := keywords[identifier]; ok {
		return tok
	}

	return IDENTIFIER
}
