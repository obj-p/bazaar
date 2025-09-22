package lexer

import "github.com/alecthomas/participle/v2/lexer"

var (
	BazaarLexer = lexer.MustStateful(lexer.Rules{
		"Common": {
			{Name: "Number", Pattern: `[-+]?(\d*\.)?\d+`},
			{Name: "String", Pattern: `"`, Action: lexer.Push("String")},
			{Name: "BuiltIn", Pattern: `\b(enumerate|len|range)\b`, Action: nil},
			{Name: "Keyword", Pattern: `\b(case|default|enum|for|if|import|in|return|switch|var)\b`, Action: nil},
			{Name: "Ident", Pattern: `[\w_][\w\d_]*`, Action: nil},
			{Name: "LogicOperator", Pattern: `==|<=|>=|!=|&&|\|\|`, Action: nil},
			{Name: "AssignOperator", Pattern: `\+=|-=|\*=|/=|%=|=`, Action: nil},
			{Name: "Operator", Pattern: `\*\*|\?\?|[-+*/%<>^!?.]`, Action: nil},
			{Name: "Punct", Pattern: `[\[\](){}@#$:;,]`, Action: nil},
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

	LogicOperatorToken = BazaarLexer.Symbols()["LogicOperator"]
	OperatorToken      = BazaarLexer.Symbols()["Operator"]
)
