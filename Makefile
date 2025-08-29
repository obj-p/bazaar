PRE_COMMIT_VERSION = 4.2.0
BIN_PRE_COMMIT     = bin/pre-commit-$(PRE_COMMIT_VERSION).pyz
PRE_COMMIT         = python3 $(BIN_PRE_COMMIT)

.PHONY: help
help: ## Show this help message
	@echo "Usage: make [target]"
	@echo ""
	@echo "Targets:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  %-15s %s\n", $$1, $$2}' $(MAKEFILE_LIST)

$(BIN_PRE_COMMIT): ## Download pre-commit
	@curl --create-dirs --output-dir bin -LO \
		https://github.com/pre-commit/pre-commit/releases/download/v$(PRE_COMMIT_VERSION)/pre-commit-$(PRE_COMMIT_VERSION).pyz

.PHONY: bootstrap
bootstrap: ## Development setup
	@$(MAKE) $(BIN_PRE_COMMIT)
	@$(PRE_COMMIT) install --hook-type pre-commit --hook-type pre-push

.PHONY: fmt
fmt: ## go fmt
	@go fmt ./...

.PHONY: grammar
grammar: ## Print the Bazaar grammar
	@go run github.com/obj-p/bazaar/cmd/bazaar grammar

.PHONY: parse
parse: ## Print the parsed source
	@go run github.com/obj-p/bazaar/cmd/bazaar parse $(SOURCE)

.PHONY: pre-commit
pre-commit: ## Run pre-commit
	@$(PRE_COMMIT) run

.PHONY: railroad
railroad: ## Visualize the Bazaar grammar (Unfortunately, the grammar breaks the railroad atm)
	@$(MAKE) grammar | bash scripts/railroad.sh

PHONY: test
test: ## go test ./...
	@go test ./...

.PHONY: tidy
tidy: ## go mod tidy
	@go mod tidy

.PHONY: vet
vet: fmt ## go vet ./...
	@go vet ./...
