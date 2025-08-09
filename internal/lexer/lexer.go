package lexer

import "github.com/alecthomas/participle/v2/lexer"

var BazaarLexer = lexer.MustStateful(lexer.Rules{
	"Common": {
		{Name: "Float", Pattern: `\d*\.\d+`, Action: nil},
		{Name: "Int", Pattern: `\d+`, Action: nil},
		{Name: "String", Pattern: `"`, Action: lexer.Push("String")},
		{Name: "Ident", Pattern: `[\w_][\w\d_]*`, Action: nil},
		{Name: "Operator", Pattern: `[-[!@#$%^&*()+_={}\|:;<,>.?/]|]`, Action: nil},
	},
	"Root": {
		{Name: "Comment", Pattern: `//.*`, Action: nil},
		lexer.Include("Common"),
		{Name: "Whitespace", Pattern: `[ \t\n\r]+`, Action: nil},
	},
	"String": {
		{Name: "StringEnd", Pattern: `"`, Action: lexer.Pop()},
		{Name: "StringEsc", Pattern: `\\.`, Action: nil},
		{Name: "StringExpr", Pattern: `\${`, Action: lexer.Push("StringExpr")},
		{Name: "StringText", Pattern: `\$|[^$"\\]+`, Action: nil},
	},
	"StringExpr": {
		{Name: "StringExprEnd", Pattern: `}`, Action: lexer.Pop()},
		lexer.Include("Common"),
		{Name: "StringExprWhitespace", Pattern: `\s+`, Action: nil},
	},
})
