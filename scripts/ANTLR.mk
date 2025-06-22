ANTLR_JAR       = antlr-4.13.1-complete.jar
BIN_ANTLR_JAR   = bin/$(ANTLR_JAR)
SCRIPTS_DIR    := $(dir $(realpath $(lastword $(MAKEFILE_LIST))))
ANTLR_JAR_PATH  = $(abspath $(SCRIPTS_DIR)/../$(BIN_ANTLR_JAR))
ANTLR           = java -jar $(ANTLR_JAR_PATH)
GRAMMAR_LEXER  ?=
GRAMMAR_PARSER ?=
GRAMMAR_NAME   ?=
GRAMMAR_RULE   ?=
GRUN            = CLASSPATH=$(ANTLR_JAR_PATH):. java org.antlr.v4.gui.TestRig

ifneq ($(and $(GRAMMAR_LEXER),$(GRAMMAR_PARSER)),)
.antlr:
	@$(MAKE) force-antlr

force-antlr:
	@rm -rf .antlr
	@mkdir -p .antlr
	@$(ANTLR) -o .antlr $(GRAMMAR_LEXER) $(GRAMMAR_PARSER)
	@cd .antlr && CLASSPATH=$(ANTLR_JAR_PATH) javac *.java

.PHONY: grun
grun: .antlr
	@cd .antlr && $(GRUN) $(GRAMMAR_NAME) $(GRAMMAR_RULE) -gui
endif
