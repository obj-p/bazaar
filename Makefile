.PHONY: bootstrap
bootstrap:
	@mint bootstrap

.PHONY: fmt
fmt:
	@mint run swiftformat .

.PHONY: lint
lint:
	@mint run swiftlint .

