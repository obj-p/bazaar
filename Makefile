ANTLR_JAR          = antlr-4.13.1-complete.jar
ANTLR_JAR_TARGET   = bin/$(ANTLR_JAR)
ANTLR              = java -jar bin/$(ANTLR_JAR)
CLASSPATH          = "bin/$(ANTLR_JAR):${CLASSPATH}"
GRUN               = "$(CLASSPATH) java org.antlr.v4.runtime.misc.TestRig"
PRE_COMMIT_VERSION = 4.2.0
PRE_COMMIT         = python3 bin/pre-commit-$(PRE_COMMIT_VERSION).pyz
PRE_COMMIT_TARGET  = bin/pre-commit-$(PRE_COMMIT_VERSION).pyz

$(ANTLR_JAR_TARGET):
	@curl --create-dirs --output-dir bin -O https://www.antlr.org/download/$(ANTLR_JAR)

$(PRE_COMMIT_TARGET):
	@curl --create-dirs --output-dir bin -LO \
		https://github.com/pre-commit/pre-commit/releases/download/v$(PRE_COMMIT_VERSION)/pre-commit-$(PRE_COMMIT_VERSION).pyz

.PHONY: bootstrap
bootstrap:
	@mint bootstrap
	@$(MAKE) $(PRE_COMMIT_TARGET)
	@$(PRE_COMMIT) install

.PHONY: fmt
fmt:
	@mint run swiftformat .

.PHONY: force-parsers
force-parsers: force-template-parser

.PHONY: force-template-parser
force-template-parser:
	@$(ANTLR) -Dlanguage=Swift -package antlr -o . Sources/Template/TemplateLexer.g4 Sources/Template/TemplateParser.g4 -visitor

.PHONY: lint
lint:
	@mint run swiftlint .

.PHONY: parsers
parsers: Sources/Template/TemplateParser.swift

.PHONY: pre-commit
pre-commit:
	@$(PRE_COMMIT) run

Sources/Template/TemplateParser.swift: $(ANTLR_JAR_TARGET)
	@$(MAKE) force-template-parser
