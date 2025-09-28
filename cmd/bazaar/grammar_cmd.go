package main

import (
	"fmt"

	"github.com/obj-p/bazaar/internal/parser"
)

type GrammarCmd struct{}

func (g *GrammarCmd) Run(_ *Context) error {
	fmt.Println(parser.BazaarParser.String())
	return nil
}
