package main

import "github.com/alecthomas/kong"

var (
	version = "dev"

	cli struct {
		Version kong.VersionFlag

		Grammar GrammarCmd `cmd:"" help:"Print the Bazaar Grammar"`
		Parse   ParserCmd  `cmd:"" help:"Print the parsed tokens"`
	}
)

func main() {
	kctx := kong.Parse(&cli,
		kong.Description("A command-line tool for Bazaar."),
		kong.Vars{"version": version},
	)
	err := kctx.Run(&Context{})
	kctx.FatalIfErrorf(err)
}
