# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Bazaar is a Swift-based domain-specific language (DSL) for defining UI components and templates. It uses ANTLR4 for parsing and generates Swift code for UI component definitions.

## Key Commands

### Development Setup
```bash
make bootstrap    # Install dependencies (mint tools, pre-commit hooks, ANTLR jar)
```

### Build & Test
```bash
swift test        # Run all tests
swift test --filter ParserTests  # Run specific test target
```

### Code Quality
```bash
make lint         # Run SwiftLint
make fmt          # Format code with SwiftFormat
make pre-commit   # Run pre-commit hooks
```

### Parser Generation
The project uses ANTLR4 to generate Swift parser code from grammar files. The generated files are already included in the repository and excluded from the Swift package build.

## Architecture

### Core Components

1. **Parser Module** (`Sources/Parser/`):
   - `BazaarLexer.g4` / `BazaarParser.g4`: ANTLR grammar definitions
   - Generated Swift parser classes from ANTLR
   - Handles parsing of `.bzr` files (Bazaar DSL files)

2. **Bazaar Module** (`Sources/bazaar/`):
   - Main library target (currently minimal)

3. **Language Features**:
   - **Data structures**: `data Person { firstName String }`
   - **Components**: `component Text { value String }`
   - **Templates**: `template Profile(person Person) { ... }`
   - **Functions**: `func add(a Int, b Int) Int`
   - **State management**: `state { }`, `init { }`, `deint { }`

### Test Structure

- `ParserTests/`: Tests for ANTLR-generated parser
- `bazaarTests/`: Tests for main library

### Grammar Structure

The Bazaar language supports:
- View definitions with optional parameters
- Type annotations with optional types (`identifier?`)
- Component and data declarations
- Template definitions with state lifecycle hooks

## Dependencies

- **ANTLR4**: Parser generation (version 4.13.1)
- **SwiftFormat**: Code formatting (via Mint)
- **SwiftLint**: Code linting (via Mint)
- **Pre-commit**: Git hooks for code quality

## Development Workflow

1. Modify grammar files (`*.g4`) if changing language syntax
2. Use `make bootstrap` to set up development environment
3. Run tests with `swift test`
4. Use `make fmt` and `make lint` before committing
5. Pre-commit hooks will automatically run quality checks
