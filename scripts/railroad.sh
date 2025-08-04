#!/usr/bin/env bash

temp_file="$(mktemp -t bazaar).html"
temp_dir=$(dirname "$temp_file")

go run github.com/alecthomas/participle/v2/cmd/railroad -w -o "$temp_file"
mv railroad-diagrams.* "$temp_dir"
