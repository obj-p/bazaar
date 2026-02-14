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

// ── Expressions ─────────────────────────────────────────────
// Precedence (high→low): postfix, unary(! -), **, */%, +-, < <= > >=, == !=, &&, ||, ??
// Right-associative: **, ??
// QUESTION LPAREN / QUESTION LBRACK bind as postfix optional-call / optional-index.
expr
    : expr DOT identOrKeyword                                      # memberExpr
    | expr QUESTION_DOT identOrKeyword                              # optionalMemberExpr
    | expr LPAREN argList? RPAREN lambda?                           # callExpr
    | expr QUESTION LPAREN argList? RPAREN                          # optionalCallExpr
    | expr LBRACK expr RBRACK                                       # indexExpr
    | expr QUESTION LBRACK expr RBRACK                              # optionalIndexExpr
    // Note: when #25 adds expression-statements, trailingLambdaExpr will greedily
    // attach any following LBRACE block. This will need semantic predicates or
    // grammar restructuring to prevent unwanted attachment in statement contexts.
    | expr lambda                                                   # trailingLambdaExpr
    | (BANG | MINUS) expr                                           # unaryExpr
    | <assoc=right> expr STAR_STAR expr                             # powerExpr
    | expr (STAR | SLASH | PERCENT) expr                            # mulExpr
    | expr (PLUS | MINUS) expr                                      # addExpr
    | expr (LESS | LESS_EQUAL | GREATER | GREATER_EQUAL) expr       # compareExpr
    | expr (EQUAL_EQUAL | BANG_EQUAL) expr                          # equalExpr
    | expr AMP_AMP expr                                             # andExpr
    | expr PIPE_PIPE expr                                           # orExpr
    | <assoc=right> expr QUESTION_QUESTION expr                     # coalesceExpr
    | NULL                                                          # nullExpr
    | TRUE                                                          # trueExpr
    | FALSE                                                         # falseExpr
    | NUMBER                                                        # numberExpr
    | stringLiteral                                                 # stringExpr
    | LBRACK argList? RBRACK                                        # arrayExpr
    | mapLiteral                                                    # mapExpr
    | lambda                                                        # lambdaExpr
    | identOrKeyword                                                # identExpr
    | LPAREN expr RPAREN                                            # parenExpr
    ;

// Note: LBRACE is shared by block, mapLiteral, and lambda. The colon
// distinguishes maps ({expr: expr}); the IN keyword distinguishes lambda
// params ({(params) in ...}); body-only lambda ({stmts}) is the fallback.
mapLiteral
    : LBRACE COLON RBRACE
    | LBRACE mapEntry (COMMA mapEntry)* COMMA? RBRACE
    ;

mapEntry: expr COLON expr;

lambda
    : LBRACE lambdaParams (ARROW typeDecl)? IN stmt* RBRACE
    | LBRACE stmt* RBRACE
    ;

lambdaParams: LPAREN lambdaParam (COMMA lambdaParam)* COMMA? RPAREN;
lambdaParam: identOrKeyword typeDecl?;

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
