ANTLR_JAR       = antlr-4.13.1-complete.jar
BIN_ANTLR_JAR   = bin/$(ANTLR_JAR)
SCRIPTS_DIR    := $(dir $(realpath $(lastword $(MAKEFILE_LIST))))
ANTLR_JAR_PATH  = $(abspath $(SCRIPTS_DIR)/../$(BIN_ANTLR_JAR))
ANTLR           = java -jar $(ANTLR_JAR_PATH)
GRAMMAR_LEXER  ?=
GRAMMAR_NAME   ?=
GRAMMAR_PARSER ?=
GRAMMAR_RULE   ?=
GRUN            = CLASSPATH=$(ANTLR_JAR_PATH):. java org.antlr.v4.gui.TestRig
GRUN_INPUT     ?=

ifneq ($(and $(GRAMMAR_LEXER),$(GRAMMAR_PARSER)),)
GENERATED_LEXER  = $(basename $(GRAMMAR_LEXER)).swift
GENERATED_PARSER = $(basename $(GRAMMAR_PARSER)).swift

$(GENERATED_PARSER):
	@$(MAKE) force-parser

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
	@cd .antlr-java && $(GRUN) $(GRAMMAR_NAME) $(GRAMMAR_RULE) -gui $(abspath $(GRUN_INPUT))

.PHONY: parser
parser: $(GENERATED_PARSER)
endif
