include scripts/ANTLR.mk

PRE_COMMIT_VERSION = 4.2.0
BIN_PRE_COMMIT     = bin/pre-commit-$(PRE_COMMIT_VERSION).pyz
PRE_COMMIT         = python3 $(BIN_PRE_COMMIT)

$(BIN_ANTLR_JAR):
	@echo $(realpath $@)
	@curl --create-dirs --output-dir bin -O https://www.antlr.org/download/$(ANTLR_JAR)

$(BIN_PRE_COMMIT):
	@curl --create-dirs --output-dir bin -LO \
		https://github.com/pre-commit/pre-commit/releases/download/v$(PRE_COMMIT_VERSION)/pre-commit-$(PRE_COMMIT_VERSION).pyz

.PHONY: bootstrap
bootstrap:
	@mint bootstrap
	@$(MAKE) $(BIN_PRE_COMMIT)
	@$(PRE_COMMIT) install
	@$(MAKE) $(BIN_ANTLR_JAR)

.PHONY: fmt
fmt:
	@mint run swiftformat .

.PHONY: lint
lint:
	@mint run swiftlint .

.PHONY: pre-commit
pre-commit:
	@$(PRE_COMMIT) run
