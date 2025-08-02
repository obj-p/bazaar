PRE_COMMIT_VERSION = 4.2.0
BIN_PRE_COMMIT     = bin/pre-commit-$(PRE_COMMIT_VERSION).pyz
PRE_COMMIT         = python3 $(BIN_PRE_COMMIT)


$(BIN_PRE_COMMIT):
	@curl --create-dirs --output-dir bin -LO \
		https://github.com/pre-commit/pre-commit/releases/download/v$(PRE_COMMIT_VERSION)/pre-commit-$(PRE_COMMIT_VERSION).pyz

.PHONY: bootstrap
bootstrap:
	@$(MAKE) $(BIN_PRE_COMMIT)
	@$(PRE_COMMIT) install

.PHONY: fmt
fmt:
	@go fmt ./...

.PHONY: pre-commit
pre-commit:
	@$(PRE_COMMIT) run

.PHONY: test
test:
	@go test ./...

.PHONY: vet
vet: fmt
	@go vet ./...
