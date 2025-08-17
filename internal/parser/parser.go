package parser

import (
	"github.com/alecthomas/participle/v2"
	"github.com/obj-p/bazaar/internal/lexer"
)

var BazaarParser = participle.MustBuild[Bazaar](
	participle.Lexer(lexer.BazaarLexer),
	participle.Elide("Comment", "StringExprWhitespace", "Whitespace"),
	participle.UseLookahead(2),
)

type Bazaar struct {
	Package *PackageDecl    `parser:"@@"`
	Imports []*ImportDecl   `parser:"@@*"`
	Decls   []*TopLevelDecl `parser:"@@*"`
}
