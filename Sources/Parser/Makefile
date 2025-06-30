GRAMMAR_NAME     = Bazaar
GRAMMAR_LEXER    = $(GRAMMAR_NAME)Lexer.g4
GRAMMAR_PARSER   = $(GRAMMAR_NAME)Parser.g4
GENERATED_LEXER  = $(GRAMMAR_NAME)Lexer.swift
GENERATED_PARSER = $(GRAMMAR_NAME)Parser.swift
GRUN_INPUT      ?=
GRUN_RULE       ?= template

include ../../scripts/ANTLR.mk

$(GENERATED_PARSER):
	$(MAKE) force-parser

.antlr-java:
	@$(MAKE) force-antlr-java

.PHONY: force-antlr-java
force-antlr-java:
	@rm -rf .antlr-java
	@mkdir -p .antlr-java
	@$(ANTLR) -o .antlr-java $(GRAMMAR_LEXER) $(GRAMMAR_PARSER)
	@cd .antlr-java && CLASSPATH=$(ANTLR_JAR_PATH) javac *.java

.PHONY: force-parser
force-parser:
	@$(ANTLR) -Dlanguage=Swift -package antlr -o . $(GRAMMAR_LEXER) $(GRAMMAR_PARSER) -no-listener -visitor
	@bash $(SCRIPTS_DIR)/patch-antlr-generated-code.sh $(GENERATED_LEXER)
	@bash $(SCRIPTS_DIR)/patch-antlr-generated-code.sh $(GENERATED_PARSER)

.PHONY: grun
grun: force-antlr-java
	@cd .antlr-java && $(GRUN) $(GRAMMAR_NAME) $(GRUN_RULE) -gui $(abspath $(GRUN_INPUT))
