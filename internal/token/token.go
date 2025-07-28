package token

import "strconv"

type Token int

const (
	ILLEGAL Token = iota
	EOF

	literal_begin
	IDENTIFIER // foobar
	BOOL       // true, false
	INT        // 42
	DOUBLE     // 42.0
	STRING     // "foobar"
	literal_end

	operator_begin
	ADD // +
	SUB // -
	MUL // *
	DIV // /
	MOD // %

	ASSIGN     // =
	ADD_ASSIGN // +=
	SUB_ASSIGN // -=
	MUL_ASSIGN // *=
	DIV_ASSIGN // /=
	MOD_ASSIGN // %=

	EQUAL     // ==
	LT        // <
	LTE       // <=
	GT        // >
	GTE       // >=
	NOT       // !
	NOT_EQUAL // !=
	LAND      // &&
	LOR       // ||

	COMMA      // ,
	COLON      // :
	DOT        // .
	SEMICOLON  // ;
	UNDERSCORE // _

	LBRACK   // [
	RBRACK   // ]
	LPAREN   // (
	RPAREN   // )
	LBRACE   // {
	RBRACE   // }
	QUESTION // ?

	COALESCE // ??
	UNWRAP   // !!
	operator_end

	// Keywords
	keyword_begin
	BREAK
	CASE
	CONTINUE
	DEFAULT
	FALLTHROUGH
	FOR
	ELSE
	IF
	IN
	RANGE
	RETURN
	SWITCH

	COMPONENT
	DATA
	ENUM
	FUNC
	MODIFIER
	TEMPLATE

	IMPORT
	PACKAGE

	LET
	NIL
	VAR
	keyword_end
)

var tokens = [...]string{
	ILLEGAL: "ILLEGAL",
	EOF:     "EOF",

	IDENTIFIER: "IDENTIFIER",
	BOOL:       "BOOL",
	INT:        "INT",
	DOUBLE:     "DOUBLE",
	STRING:     "STRING",

	ADD: "+",
	SUB: "-",
	MUL: "*",
	DIV: "/",
	MOD: "%",

	ASSIGN:     "=",
	ADD_ASSIGN: "+=",
	SUB_ASSIGN: "-=",
	MUL_ASSIGN: "*=",
	DIV_ASSIGN: "/=",
	MOD_ASSIGN: "%=",

	EQUAL:     "==",
	LT:        "<",
	LTE:       "<=",
	GT:        ">",
	GTE:       ">=",
	NOT:       "!",
	NOT_EQUAL: "!=",
	LAND:      "&&",
	LOR:       "||",

	COMMA:      ",",
	COLON:      ":",
	DOT:        ".",
	SEMICOLON:  ";",
	UNDERSCORE: "_",

	LBRACK:   "[",
	RBRACK:   "]",
	LPAREN:   "(",
	RPAREN:   ")",
	LBRACE:   "{",
	RBRACE:   "}",
	QUESTION: "?",

	COALESCE: "??",
	UNWRAP:   "!!",

	BREAK:       "break",
	CASE:        "case",
	CONTINUE:    "continue",
	DEFAULT:     "default",
	FALLTHROUGH: "fallthrough",
	FOR:         "for",
	ELSE:        "else",
	IF:          "if",
	IN:          "in",
	RANGE:       "range",
	RETURN:      "return",
	SWITCH:      "switch",

	COMPONENT: "component",
	DATA:      "data",
	ENUM:      "enum",
	FUNC:      "func",
	MODIFIER:  "modifier",
	TEMPLATE:  "template",

	IMPORT:  "import",
	PACKAGE: "package",

	LET: "let",
	NIL: "nil",
	VAR: "var",
}

func (tok Token) String() string {
	s := ""
	if 0 <= tok && tok < Token(len(tokens)) {
		s = tokens[tok]
	}
	if s == "" {
		s = "token(" + strconv.Itoa(int(tok)) + ")"
	}
	return s
}

var keywords = map[string]Token{
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
	"modifier":  MODIFIER,
	"nil":       NIL,
	"package":   PACKAGE,
	"return":    RETURN,
	"string":    STRING,
	"switch":    SWITCH,
	"template":  TEMPLATE,
}

func LookupIdentifier(identifier string) Token {
	if tok, ok := keywords[identifier]; ok {
		return tok
	}

	return IDENTIFIER
}
