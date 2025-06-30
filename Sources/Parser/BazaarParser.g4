parser grammar BazaarParser;

options {
  tokenVocab = BazaarLexer;
}

template
    : view+ EOF
    ;

view
    : VIEW identifier parameter_clause?
    ;

parameter_clause
    : LPAREN parameter_list? RPAREN
    ;

parameter_list
    : parameter (COMMA parameter)*
    ;

parameter
    : identifier QUESTION? type_identifier
    ;

type_identifier
    : identifier
    ;

identifier
    : IDENTIFIER
    ;
