lexer grammar TemplateLexer;

VIEW    : 'view';

LPARANS   : '(';
RPARANS   : ')';
QUESTION  : '?';

IDENTIFIER
    : [_A-Za-z] [_0-9A-Za-z]*
    ;

WS     :   [ \t\r\n]+ -> skip;
