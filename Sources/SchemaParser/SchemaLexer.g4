lexer grammar SchemaLexer;

DATA    : 'data';
VIEW    : 'component';

LPAREN    : '(';
RPAREN    : ')';
QUESTION  : '?';

IDENTIFIER
    : [_A-Za-z] [_0-9A-Za-z]*
    ;

WS     :   [ \t\r\n]+ -> skip;
