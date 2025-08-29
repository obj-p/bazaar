package main

import (
	"os"

	"github.com/alecthomas/repr"
	"github.com/obj-p/bazaar/internal/parser"
)

type ParserCmd struct {
	Source string `arg:"" name:"source" help:"Source path."`
}

func (p *ParserCmd) Run(ctx *Context) error {
	source, err := os.ReadFile(p.Source)
	if err != nil {
		return err
	}

	v, err := parser.BazaarParser.ParseBytes(p.Source, source)
	defer repr.Println(v)

	if err != nil {
		return err
	}

	return nil
}
