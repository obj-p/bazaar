GRAMMAR_LEXER  = SchemaLexer.g4
GRAMMAR_NAME   = Schema
GRAMMAR_RULE  ?= schema
GRAMMAR_PARSER = SchemaParser.g4

include ../../scripts/ANTLR.mk

.PHONY: force-parser
force-parser:
	@$(ANTLR) -Dlanguage=Swift -package antlr -o . SchemaLexer.g4 SchemaParser.g4 -visitor

.PHONY: parser
parser: SchemaParser.swift

SchemaParser.swift:
	@$(MAKE) force-parser
