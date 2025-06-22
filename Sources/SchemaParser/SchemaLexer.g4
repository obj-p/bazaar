lexer grammar SchemaLexer;

DATA    : 'data';
VIEW    : 'component';

LPARANS   : '(';
RPARANS   : ')';
QUESTION  : '?';

IDENTIFIER
    : [_A-Za-z] [_0-9A-Za-z]*
    ;

WS     :   [ \t\r\n]+ -> skip;
