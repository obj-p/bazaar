parser grammar BazaarParser;

options {
  tokenVocab = BazaarLexer;
}

bzr
    : declaration* EOF
    ;

declaration
    : import_declaration
    | data_declaration
    | enum_declaration
    | component_declaration
    | function_declaration
    | template_declaration
    ;

import_declaration
    : IMPORT identifier
    ;

data_declaration
    : DATA identifier LCURLY field_list? RCURLY
    ;

field_list
    : field (field)*
    ;

field
    : identifier type_annotation
    ;

enum_declaration
    : ENUM identifier LCURLY enum_value_list? RCURLY
    ;

enum_value_list
    : enum_value (enum_value)*
    ;

enum_value
    : identifier
    ;

component_declaration
    : COMPONENT identifier LCURLY field_list? RCURLY
    ;

function_declaration
    : FUNCTION identifier parameter_clause return_type? function_body?
    ;

function_body
    : LCURLY statement_list? RCURLY
    ;

statement_list
    : statement (statement)*
    ;

statement
    : expression
    ;

return_type
    : type_annotation
    ;

template_declaration
    : TEMPLATE identifier parameter_clause return_type? template_body
    ;

template_body
    : LCURLY template_content RCURLY
    ;

template_content
    : component_instantiation
    ;

component_instantiation
    : identifier (LPAREN argument_list? RPAREN)? component_body?
    ;

component_body
    : LCURLY component_content_list? RCURLY (event_handler)*
    ;

component_content_list
    : component_content (component_content)*
    ;

component_content
    : component_instantiation
    | conditional_statement
    ;

conditional_statement
    : IF LPAREN expression RPAREN LCURLY component_content_list? RCURLY
    ;

event_handler
    : identifier LCURLY statement_list? RCURLY
    ;

parameter_clause
    : LPAREN parameter_list? RPAREN
    ;

parameter_list
    : parameter (COMMA parameter)*
    ;

parameter
    : identifier type_annotation
    ;

argument_list
    : argument (COMMA argument)*
    ;

argument
    : (identifier EQUALS)? expression
    ;

expression
    : primary_expression (DOT identifier)*
    ;

primary_expression
    : identifier (LPAREN argument_list? RPAREN)?
    | string_literal
    | DOT identifier
    ;

type_annotation
    : type_identifier QUESTION?
    | type_identifier LBRACKET RBRACKET
    ;

type_identifier
    : identifier
    | STRING
    | BOOL
    | INT
    ;

string_literal
    : STRING_LITERAL
    ;

identifier
    : IDENTIFIER
    ;
