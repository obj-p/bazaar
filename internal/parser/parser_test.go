package parser

import (
	"flag"
	"io/fs"
	"os"
	"path/filepath"
	"strings"
	"testing"

	require "github.com/alecthomas/assert/v2"
	"github.com/alecthomas/repr"
)

var record = flag.Bool("record", false, "record new golden files")

func TestParser(t *testing.T) {

	err := filepath.WalkDir("testdata", func(path string, d fs.DirEntry, err error) error {
		if err != nil {
			return err
		}

		if d.IsDir() || !strings.HasSuffix(d.Name(), ".bzr") {
			return nil
		}

		source, err := os.ReadFile(path)
		if err != nil {
			return err
		}

		ast, err := BazaarParser.ParseBytes(d.Name(), source)
		require.NoError(t, err)

		actual := repr.String(ast, repr.Indent("  "))

		goldenPath := path + ".ast.golden"

		if *record {
			content := actual + "\n"
			err := os.WriteFile(goldenPath, []byte(content), 0644)
			if err != nil {
				return err
			}
			return nil
		}

		expected, err := os.ReadFile(goldenPath)
		if os.IsNotExist(err) {
			t.Errorf("Golden file missing: %s\nRun with -record to create", goldenPath)
			return nil
		}
		require.NoError(t, err)
		expectedStr := strings.TrimSuffix(string(expected), "\n")
		require.Equal(t, expectedStr, actual, "AST mismatch for %s", path)

		return nil
	})

	require.NoError(t, err)
}
