#!/usr/bin/env bash
#
# Patches ANTLR generated code for use in Swift 6 and strict concurrency
# https://developer.apple.com/documentation/swift/adoptingswift6

SOURCE_FILE="$1"

sed -i "" "s/import Antlr4/@preconcurrency import Antlr4/g" "$SOURCE_FILE"
sed -i "" "s/internal static var _decisionToDFA/internal static let _decisionToDFA/" "$SOURCE_FILE"
