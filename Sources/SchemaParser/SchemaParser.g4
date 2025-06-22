parser grammar SchemaParser;

options {
  tokenVocab = SchemaLexer;
}

schema
    : data+ EOF
    ;

data
    : DATA identifier data_body
    ;

data_body
    : LPARANS data_members RPARANS
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
