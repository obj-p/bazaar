GRAMMAR_LEXER  = TemplateLexer.g4
GRAMMAR_NAME   = Template
GRAMMAR_RULE  ?= schema
GRAMMAR_PARSER = TemplateParser.g4

include ../../scripts/ANTLR.mk

.PHONY: force-parser
force-parser:
	@$(ANTLR) -Dlanguage=Swift -package antlr -o . TemplateLexer.g4 TemplateParser.g4 -visitor

.PHONY: parser
parser: TemplateParser.swift

TemplateParser.swift:
	@$(MAKE) force-parser
