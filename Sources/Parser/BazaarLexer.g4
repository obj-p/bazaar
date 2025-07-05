lexer grammar BazaarLexer;

COMPONENT    : 'component';
DATA         : 'data';
ENUM         : 'enum';
FUNCTION     : 'function';
TEMPLATE     : 'template';
IMPORT       : 'import';
IF           : 'if';

STRING       : 'String';
BOOL         : 'Bool';
INT          : 'Int';

COMMA        : ',';
LCURLY       : '{';
RCURLY       : '}';
LPAREN       : '(';
RPAREN       : ')';
LBRACKET     : '[';
RBRACKET     : ']';
QUESTION     : '?';
DOT          : '.';
EQUALS       : '=';
SEMICOLON    : ';';

STRING_LITERAL
    : '"' (~["\r\n] | '\\' .)* '"'
    ;

IDENTIFIER
    : [_A-Za-z] [_0-9A-Za-z]*
    ;

WS     :   [ \t\r\n]+ -> skip;
