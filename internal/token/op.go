package token

import "fmt"

// Adapted from https://github.com/alecthomas/langx

type Op int

const (
	OpBegin Op = iota

	OpAdd // +
	OpSub // -
	OpMul // *
	OpDiv // /
	OpMod // %

	OpAssign    // =
	OpAddAssign // +=
	OpSubAssign // -=
	OpMulAssign // *=
	OpDivAssign // /=
	OpModAssign // %=

	OpEql    // ==
	OpLt     // <
	OpLte    // <=
	OpGt     // >
	OpGte    // >=
	OpNot    // !
	OpNotEql // !=
	OpAnd    // &&
	OpOr     // ||

	OpEnd
)

var ops = [...]string{
	OpAdd: "token.OpAdd",
	OpSub: "token.OpSub",
	OpMul: "token.OpMul",
	OpDiv: "token.OpDiv",
	OpMod: "token.OpMod",

	OpAssign:    "token.OpAssign",
	OpAddAssign: "token.OpAddAssign",
	OpSubAssign: "token.OpSubAssign",
	OpMulAssign: "token.OpMulAssign",
	OpDivAssign: "token.OpDivAssign",
	OpModAssign: "token.OpModAssign",

	OpEql:    "token.OpEql",
	OpLt:     "token.OpLt",
	OpLte:    "token.OpLte",
	OpGt:     "token.OpGt",
	OpGte:    "token.OpGte",
	OpNot:    "token.OpNot",
	OpNotEql: "token.OpNotEql",
	OpAnd:    "token.OpAnd",
	OpOr:     "token.OpOr",
}

func (o Op) String() string {
	if o <= OpBegin || o >= OpEnd {
		panic("invalid operator")
	}
	return ops[o]
}

func (o *Op) Capture(values []string) error {
	switch values[0] {
	case "+":
		*o = OpAdd
	case "-":
		*o = OpSub
	case "*":
		*o = OpMul
	case "/":
		*o = OpDiv
	case "%":
		*o = OpMod
	case "=":
		*o = OpAssign
	case "+=":
		*o = OpAddAssign
	case "-=":
		*o = OpSubAssign
	case "*=":
		*o = OpMulAssign
	case "/=":
		*o = OpDivAssign
	case "%=":
		*o = OpModAssign
	case "==":
		*o = OpEql
	case "<":
		*o = OpLt
	case "<=":
		*o = OpLte
	case ">":
		*o = OpGt
	case ">=":
		*o = OpGte
	case "!":
		*o = OpNot
	case "!=":
		*o = OpNotEql
	case "&&":
		*o = OpAnd
	case "||":
		*o = OpOr
	default:
		return fmt.Errorf("invalid operator %q", values[0])
	}
	return nil
}
