parser grammar TemplateParser;

options {
  tokenVocab = TemplateLexer;
}

template
    : view+ EOF
    ;

view
    : VIEW identifier
    ;

identifier
    : IDENTIFIER
    ;
