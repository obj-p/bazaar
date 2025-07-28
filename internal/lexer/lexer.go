package lexer

import "github.com/obj-p/bazaar/internal/token"

type Lexer struct {
	input        string
	position     int
	readPosition int
	ch           rune
}

const (
	eof = -1
)

func New(input string) *Lexer {
	l := &Lexer{input: input}
	if len(input) > 0 {
		l.ch = rune(l.input[l.readPosition])
	} else {
		l.ch = eof
	}

	return l
}

func (l *Lexer) nextChar() rune {
	ch := l.ch
	l.readPosition += 1
	if l.readPosition >= len(l.input) {
		l.ch = eof
	} else {
		l.ch = rune(l.input[l.readPosition])
	}

	return ch
}

func (l *Lexer) NextToken() token.Token {
	var tok token.Token
	l.position = l.readPosition
	ch := l.nextChar()
	for ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r' {
		l.position = l.readPosition
		ch = l.nextChar()
	}

	switch ch {
	case '[':
		tok = token.LBRACK
	case ']':
		tok = token.RBRACK
	case '(':
		tok = token.LPAREN
	case ')':
		tok = token.RPAREN
	case '{':
		tok = token.LBRACE
	case '}':
		tok = token.RBRACE
	case '+':
		tok = token.ADD
	case '=':
		tok = token.ASSIGN
	case '.':
		tok = token.DOT
	case ',':
		tok = token.COMMA
	case '?':
		tok = token.QUESTION
	case eof:
		l.readPosition -= 1
		tok = token.EOF
	default:
		if isLetter(ch) {
			tok = l.readIdentifier()
		} else if isDigit(ch) {
			tok = l.readNumber()
		} else {
			tok = token.ILLEGAL
		}
	}

	return tok
}

func (l *Lexer) readIdentifier() token.Token {
	for isLetter(l.ch) {
		l.nextChar()
	}

	literal := l.Literal()
	return token.LookupIdentifier(literal)
}

func (l *Lexer) readNumber() token.Token {
	for isDigit(l.ch) {
		l.nextChar()
	}

	return token.INT
}

func (l *Lexer) Literal() string {
	return l.input[l.position:l.readPosition]
}

func isDigit(ch rune) bool {
	return '0' <= ch && ch <= '9'
}

func isLetter(ch rune) bool {
	return 'a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z' || ch == '_'
}
