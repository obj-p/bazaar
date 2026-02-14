lexer grammar BazaarLexer;

// ── Keywords (23) ──────────────────────────────────────────────
// Defined before IDENTIFIER so ANTLR gives them priority.

AS:          'as';
CASE:        'case';
COMPONENT:   'component';
CONSTRUCTOR: 'constructor';
DATA:        'data';
DEFAULT:     'default';
ELSE:        'else';
ENUM:        'enum';
FALSE:       'false';
FOR:         'for';
FUNC:        'func';
IF:          'if';
IMPORT:      'import';
IN:          'in';
MODIFIER:    'modifier';
NULL:        'null';
PACKAGE:     'package';
PREVIEW:     'preview';
RETURN:      'return';
SWITCH:      'switch';
TEMPLATE:    'template';
TRUE:        'true';
VAR:         'var';

// ── Number literal ─────────────────────────────────────────────
// No leading +/- — unary operators handled in parser.

NUMBER
    : DIGITS '.' DIGITS? EXPONENT?
    | '.' DIGITS EXPONENT?
    | DIGITS EXPONENT?
    ;

fragment DIGIT:    [0-9];
fragment DIGITS:   DIGIT+;
fragment EXPONENT: [eE] [+\-]? DIGITS;

// ── Identifier ─────────────────────────────────────────────────

IDENTIFIER: [a-zA-Z_] [a-zA-Z0-9_]*;

// ── Multi-char operators (15) ──────────────────────────────────

STAR_STAR:        '**';
QUESTION_DOT:     '?.';
QUESTION_QUESTION:'??';
ARROW:            '->';
EQUAL_EQUAL:      '==';
BANG_EQUAL:       '!=';
LESS_EQUAL:       '<=';
GREATER_EQUAL:    '>=';
AMP_AMP:          '&&';
PIPE_PIPE:        '||';
PLUS_EQUAL:       '+=';
MINUS_EQUAL:      '-=';
STAR_EQUAL:       '*=';
SLASH_EQUAL:      '/=';
PERCENT_EQUAL:    '%=';

// ── Single-char operators / punctuation (20) ───────────────────

PLUS:    '+';
MINUS:   '-';
STAR:    '*';
SLASH:   '/';
PERCENT: '%';
LESS:    '<';
GREATER: '>';
BANG:    '!';
QUESTION:'?';
DOT:     '.';
EQUAL:   '=';
LPAREN:  '(';
RPAREN:  ')';
LBRACK:  '[';
RBRACK:  ']';
LBRACE:  '{';
RBRACE:  '}';
AT:      '@';
COLON:   ':';
COMMA:   ',';

// ── Skipped ────────────────────────────────────────────────────

LINE_COMMENT: '//' ~[\r\n]* -> skip;
WS:           [ \t\r\n]+   -> skip;
