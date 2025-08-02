package lexer

import "github.com/alecthomas/participle/v2/lexer"

var BazaarLexer = lexer.MustSimple([]lexer.SimpleRule{
	{Name: "Bool", Pattern: `true|false`},
	{Name: "String", Pattern: `"(\\"|[^"])*"`},
	{Name: "Ident", Pattern: `[\w_][\w\d_]*`},
	{Name: "Comment", Pattern: `//.*`},
	{Name: "Whitespace", Pattern: `\s+`},
})
