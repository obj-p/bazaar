parser grammar SchemaParser;

options {
  tokenVocab = SchemaLexer;
}

schema
    : data+ EOF
    ;

data
    : DATA identifier data_clause
    ;

data_clause
    : LPAREN data_members RPAREN
    ;

data_members
    : member*
    ;

member
    : member_identifier QUESTION? type_identifier
    ;

member_identifier
    : identifier
    ;

type_identifier
    : identifier
    ;

identifier
    : IDENTIFIER
    ;
