package lexer

import "github.com/alecthomas/participle/v2/lexer"

var BazaarLexer = lexer.MustSimple([]lexer.SimpleRule{
	{Name: "Comment", Pattern: `//.*`},
	{Name: "Number", Pattern: `(?:\d*\.)?\d+`},
	{Name: "String", Pattern: `"(\\"|[^"])*"`},
	{Name: "Ident", Pattern: `[\w_][\w\d_]*`},
	{Name: "Operator", Pattern: `[-[!@#$%^&*()+_={}\|:;"'<,>.?/]|]`},
	{Name: "Whitespace", Pattern: `[ \t\n\r]+`},
})
