parser grammar BazaarParser;

options { tokenVocab = BazaarLexer; }

// Stub entry — replaced by #20.
bazaar: token* EOF;

token: identOrKeyword | keyword | NUMBER | stringLiteral | operator | punctuation;

// Contextual keywords usable as identifiers (edge-cases.bzr).
identOrKeyword
    : IDENTIFIER | COMPONENT | CONSTRUCTOR | DATA | MODIFIER
    | FUNC | NULL | PACKAGE | PREVIEW | TEMPLATE | TRUE | FALSE
    ;

// Non-contextual keywords — never identifiers.
keyword: AS | CASE | DEFAULT | ELSE | ENUM | FOR | IF | IMPORT | IN | RETURN | SWITCH | VAR;

operator
    : STAR_STAR | QUESTION_DOT | QUESTION_QUESTION | ARROW
    | EQUAL_EQUAL | BANG_EQUAL | LESS_EQUAL | GREATER_EQUAL | AMP_AMP | PIPE_PIPE
    | PLUS_EQUAL | MINUS_EQUAL | STAR_EQUAL | SLASH_EQUAL | PERCENT_EQUAL
    | PLUS | MINUS | STAR | SLASH | PERCENT
    | LESS | GREATER | BANG | QUESTION | DOT | EQUAL
    ;

punctuation: LPAREN | RPAREN | LBRACK | RBRACK | LBRACE | RBRACE | AT | COLON | COMMA;

stringLiteral: STRING_OPEN stringPart* STRING_CLOSE;

stringPart
    : STRING_TEXT
    | STRING_DOLLAR
    | STRING_ESCAPE
    | UNICODE_SHORT_ESCAPE
    | UNICODE_LONG_ESCAPE
    | stringInterp
    ;

stringInterp: STRING_INTERP_OPEN token* RBRACE;
