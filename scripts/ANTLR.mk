ANTLR_JAR       = antlr-4.13.1-complete.jar
BIN_ANTLR_JAR   = bin/$(ANTLR_JAR)
SCRIPTS_DIR    := $(abspath $(dir $(lastword $(MAKEFILE_LIST))))
ANTLR_JAR_PATH  = $(abspath $(SCRIPTS_DIR)/../$(BIN_ANTLR_JAR))
ANTLR           = java -jar $(ANTLR_JAR_PATH)
GRUN            = CLASSPATH=$(ANTLR_JAR_PATH):. java org.antlr.v4.gui.TestRig
