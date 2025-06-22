#!/usr/bin/env bash

SOURCE_FILE="$1"

sed -i "" "s/import Antlr4/@preconcurrency import Antlr4/g" "$SOURCE_FILE"
sed -i "" "s/internal static var _decisionToDFA/internal static let _decisionToDFA/" "$SOURCE_FILE"
