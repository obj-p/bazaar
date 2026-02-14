parser grammar BazaarParser;

options { tokenVocab = BazaarLexer; }

// ── File structure ───────────────────────────────────────────
bazaarFile: packageDecl? importDecl* topLevelDecl* EOF;

packageDecl:   PACKAGE qualifiedName;
importDecl:    IMPORT qualifiedName (AS IDENTIFIER)?;
qualifiedName: IDENTIFIER (DOT IDENTIFIER)*;

// ── Top-level declarations ───────────────────────────────────
topLevelDecl
    : enumDecl
    | componentDecl
    | dataDecl
    | modifierDecl
    | functionDecl
    | templateDecl
    | previewDecl
    ;

enumDecl:      ENUM IDENTIFIER LBRACE (IDENTIFIER (COMMA IDENTIFIER)* COMMA?)? RBRACE;
componentDecl: COMPONENT IDENTIFIER LBRACE memberDecl* RBRACE;
dataDecl:      DATA IDENTIFIER LBRACE memberDecl* RBRACE;
modifierDecl:  MODIFIER IDENTIFIER LBRACE memberDecl* RBRACE;
functionDecl:  FUNC IDENTIFIER LPAREN parameterList? RPAREN (ARROW typeDecl)? block?;
templateDecl:  TEMPLATE IDENTIFIER (LPAREN parameterList? RPAREN)? block;
previewDecl:   PREVIEW IDENTIFIER block;

// ── Members ──────────────────────────────────────────────────
memberDecl:    constructorDecl | fieldDecl;
fieldDecl:     identOrKeyword typeDecl (EQUAL expr)?;
constructorDecl: CONSTRUCTOR LPAREN parameterList? RPAREN EQUAL expr;

// ── Parameters ───────────────────────────────────────────────
parameterList: parameterDecl (COMMA parameterDecl)* COMMA?;
parameterDecl: identOrKeyword typeDecl (EQUAL expr)?;

// ── Block ────────────────────────────────────────────────────
block: LBRACE stmt* RBRACE;

// ── Contextual keywords usable as identifiers ────────────────
identOrKeyword
    : IDENTIFIER | COMPONENT | CONSTRUCTOR | DATA | ENUM | MODIFIER
    | FUNC | NULL | PACKAGE | PREVIEW | TEMPLATE | TRUE | FALSE
    ;

// ── Types ────────────────────────────────────────────────────
typeDecl
    : IDENTIFIER QUESTION?
    | COMPONENT QUESTION?
    | FUNC LPAREN typeList? RPAREN (ARROW typeDecl)? QUESTION?
    | LBRACK typeDecl RBRACK QUESTION?
    | LBRACE typeDecl COLON typeDecl RBRACE QUESTION?
    | LPAREN typeDecl RPAREN QUESTION?
    ;

typeList: typeDecl (COMMA typeDecl)* COMMA?;

// ── Stub: expr (completed by #22) ────────────────────────────
expr
    : expr DOT identOrKeyword
    | expr LPAREN argList? RPAREN
    | expr LBRACK expr RBRACK
    | identOrKeyword
    | NUMBER
    | stringLiteral
    | LBRACK argList? RBRACK
    | LPAREN expr RPAREN
    ;

argList: arg (COMMA arg)* COMMA?;
arg:     (IDENTIFIER EQUAL)? expr;

// ── Stub: stmt (completed by #25) ────────────────────────────
stmt: block | stringLiteral | ~(LBRACE | RBRACE);

// ── String literal ───────────────────────────────────────────
stringLiteral: STRING_OPEN stringPart* STRING_CLOSE;

stringPart
    : STRING_TEXT
    | STRING_DOLLAR
    | STRING_ESCAPE
    | UNICODE_SHORT_ESCAPE
    | UNICODE_LONG_ESCAPE
    | stringInterp
    ;

stringInterp: STRING_INTERP_OPEN expr RBRACE;
