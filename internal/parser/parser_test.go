package parser

import (
	"io/fs"
	"os"
	"path/filepath"
	"strings"
	"testing"

	require "github.com/alecthomas/assert/v2"
	"github.com/alecthomas/repr"
)

func TestParser(t *testing.T) {
	err := filepath.WalkDir("testdata", func(path string, d fs.DirEntry, err error) error {
		if err != nil {
			return err
		}

		if d.IsDir() || !strings.HasSuffix(d.Name(), ".bz") {
			return nil
		}

		source, err := os.ReadFile(path)
		if err != nil {
			return err
		}

		v, err := BazaarParser.ParseBytes(d.Name(), source)
		if err != nil {
			repr.Println(v)
		}
		return err
	})

	require.NoError(t, err)
}
