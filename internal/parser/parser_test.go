package parser

import (
	"os"
	"testing"

	require "github.com/alecthomas/assert/v2"
)

func TestParser(t *testing.T) {
	source, err := os.ReadFile("example.bz")
	require.NoError(t, err)
	_, err = BazaarParser.ParseBytes("example", source)
	require.NoError(t, err)
}
