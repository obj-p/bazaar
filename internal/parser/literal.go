package parser

type Bool bool

func (b *Bool) Capture(values []string) error {
	*b = values[0] == "true"
	return nil
}

type StringFragment struct {
	Esc  *string `parser:"@StringEsc"`
	Expr *Expr   `parser:"| '${' @@ '}'"`
	Text *string `parser:"| @StringText"`
}

type String struct {
	Fragments []*StringFragment `parser:"\"\\\"\" @@* \"\\\"\""`
}

type Literal struct {
	Null   bool          `parser:"@'null'"`
	Bool   *Bool         `parser:"| @('true' | 'false')"`
	Number *string       `parser:"| @Number"`
	String *String       `parser:"| @@"`
	Array  *ArrayLiteral `parser:"| @@"`
	Map    *MapLiteral   `parser:"| @@"`
}

type ArrayLiteral struct {
	Values []*Expr `parser:"'[' (@@ (',' @@)* ','?)? ']'"`
}

type MapEntry struct {
	Key   Expr `parser:"@@"`
	Value Expr `parser:"':' @@"`
}

type MapLiteral struct {
	Entries []*MapEntry `parser:"'{' (':' | (@@ (',' @@)* ','?)) '}'"`
}
