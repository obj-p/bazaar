package lexer

import "github.com/alecthomas/participle/v2/lexer"

var (
	BazaarLexer = lexer.MustStateful(lexer.Rules{
		"Common": {
			{Name: "Number", Pattern: `[-+]?(\d+\.?\d*|\d*\.\d+)([eE][-+]?\d+)?`},
			{Name: "String", Pattern: `"`, Action: lexer.Push("String")},
			{Name: "Keyword", Pattern: `\b(case|default|else|enum|for|if|import|in|return|switch|var)\b`, Action: nil},
			{Name: "Ident", Pattern: `[\w_][\w\d_]*`, Action: nil},
			{Name: "LogicOperator", Pattern: `==|<=|>=|!=|&&|\|\|`, Action: nil},
			{Name: "AssignOperator", Pattern: `\+=|-=|\*=|/=|%=|=`, Action: nil},
			{Name: "Operator", Pattern: `\*\*|\?\?|[-+*/%<>^!?.]`, Action: nil},
		},
		"Root": {
			{Name: "Comment", Pattern: `//.*`, Action: nil},
			lexer.Include("Common"),
			{Name: "Punct", Pattern: `[\[\](){}@#$:;,]`, Action: nil},
			{Name: "Whitespace", Pattern: `[ \t\n\r]+`, Action: nil},
		},
		"String": {
			{Name: "StringEnd", Pattern: `"`, Action: lexer.Pop()},
			{Name: "StringEsc", Pattern: `\\.`, Action: nil},
			{Name: "StringExpr", Pattern: `\${`, Action: lexer.Push("StringExpr")},
			{Name: "StringText", Pattern: `\$|[^$"\\]+`, Action: nil},
		},
		"StringExprCommon": {
			{Name: "StringExprPunct", Pattern: `[\[\]()@#$:;,]`, Action: nil},
			{Name: "StringExprWhitespace", Pattern: `\s+`, Action: nil},
		},
		"StringExpr": {
			{Name: "StringExprEnd", Pattern: `}`, Action: lexer.Pop()},
			lexer.Include("Common"),
			{Name: "String", Pattern: `"`, Action: lexer.Push("String")},
			{Name: "StringExprBlock", Pattern: `{`, Action: lexer.Push("StringExprBlock")},
			lexer.Include("StringExprCommon"),
		},
		"StringExprBlock": {
			{Name: "StringExprBlockEnd", Pattern: `}`, Action: lexer.Pop()},
			lexer.Include("Common"),
			{Name: "StringExprBlock", Pattern: `{`, Action: lexer.Push("StringExprBlock")},
			lexer.Include("StringExprCommon"),
		},
	})

	LogicOperatorToken = BazaarLexer.Symbols()["LogicOperator"]
	OperatorToken      = BazaarLexer.Symbols()["Operator"]
)
