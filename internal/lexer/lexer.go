package lexer

import "github.com/alecthomas/participle/v2/lexer"

var BazaarLexer = lexer.MustStateful(lexer.Rules{
	"Root": {
		lexer.Include("Tokens"),
		{Name: "String", Pattern: `"`, Action: lexer.Push("String")},
	},
	"String": {
		{Name: "Escaped", Pattern: `\\.`, Action: nil},
		{Name: "StringEnd", Pattern: `"`, Action: lexer.Pop()},
		{Name: "StringExpr", Pattern: `\${`, Action: lexer.Push("StringExpr")},
		{Name: "Char", Pattern: `\$|[^$"\\]+`, Action: nil},
	},
	"StringExpr": {
		{Name: "StringExprEnd", Pattern: `}`, Action: lexer.Pop()},
		lexer.Include("Tokens"),
	},
	"Tokens": {
		{Name: "Comment", Pattern: `//.*`, Action: nil},
		{Name: "Float", Pattern: `\d*\.\d+`, Action: nil},
		{Name: "Int", Pattern: `\d+`, Action: nil},
		{Name: "Ident", Pattern: `[\w_][\w\d_]*`, Action: nil},
		{Name: "Operator", Pattern: `[-[!@#$%^&*()+_={}\|:;'<,>.?/]|]`, Action: nil},
		{Name: "Whitespace", Pattern: `[ \t\n\r]+`, Action: nil},
	},
})
