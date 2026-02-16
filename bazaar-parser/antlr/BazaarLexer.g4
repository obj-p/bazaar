lexer grammar BazaarLexer;

// antlr-kotlin specific: prevents EmptyStackException on unmatched '}'.
@members {
    override fun popMode(): Int {
        if (_modeStack.isEmpty) return _mode
        return super.popMode()
    }
}

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
fragment HEX:      [0-9a-fA-F];

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
LBRACE:  '{' -> pushMode(DEFAULT_MODE);
RBRACE:  '}' -> popMode;
AT:      '@';
COLON:   ':';
COMMA:   ',';

// ── String literal (open) ─────────────────────────────────────

STRING_OPEN: '"' -> pushMode(StringMode);

// ── Skipped ────────────────────────────────────────────────────

LINE_COMMENT: '//' ~[\r\n]* -> skip;
WS:           [ \t\r\n]+   -> skip;

// ── String mode ──────────────────────────────────────────────

mode StringMode;

STRING_CLOSE:          '"' -> popMode;
STRING_INTERP_OPEN:    '${' -> pushMode(DEFAULT_MODE);
UNICODE_LONG_ESCAPE:   '\\U' HEX HEX HEX HEX HEX HEX HEX HEX;
UNICODE_SHORT_ESCAPE:  '\\u' HEX HEX HEX HEX;
STRING_ESCAPE:         '\\' [\\"ntr];
STRING_TEXT:            (~["\\$\r\n])+;
STRING_NL:             [\r\n] -> popMode;
STRING_DOLLAR:         '$';
STRING_BAD_ESCAPE:     '\\' .;
