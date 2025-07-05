lexer grammar BazaarLexer;

COMPONENT    : 'component';
DATA         : 'data';

COMMA     : ',';
LCURLY    : '{';
RCURLY    : '}';
LPAREN    : '(';
RPAREN    : ')';
QUESTION  : '?';

IDENTIFIER
    : [_A-Za-z] [_0-9A-Za-z]*
    ;

WS     :   [ \t\r\n]+ -> skip;
