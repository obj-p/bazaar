ANTLR_JAR        = antlr-4.13.1-complete.jar
ANTLR_JAR_TARGET = bin/$(ANTLR_JAR)
ANTLR            = java -jar bin/$(ANTLR_JAR)

$(ANTLR_JAR_TARGET):
	@curl --create-dirs --output-dir bin -O https://www.antlr.org/download/$(ANTLR_JAR)

.PHONY: bootstrap
bootstrap:
	@mint bootstrap

.PHONY: fmt
fmt:
	@mint run swiftformat .

.PHONY: lint
lint:
	@mint run swiftlint .

.PHONY: parsers
parsers: Sources/Templates/TemplatesParser.swift

Sources/Templates/TemplatesParser.swift: $(ANTLR_JAR_TARGET)
	@$(ANTLR) -Dlanguage=Swift -package antlr -o . Sources/Templates/TemplatesLexer.g4 Sources/Templates/TemplatesParser.g4 -visitor

