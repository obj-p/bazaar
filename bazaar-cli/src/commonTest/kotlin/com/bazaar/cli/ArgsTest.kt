package com.bazaar.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ArgsTest {

    @Test
    fun helpLongFlag() {
        assertIs<ArgsResult.Help>(parseArgs(arrayOf("--help")))
    }

    @Test
    fun helpShortFlag() {
        assertIs<ArgsResult.Help>(parseArgs(arrayOf("-h")))
    }

    @Test
    fun formatTextExplicit() {
        val result = parseArgs(arrayOf("--format", "text", "file.bzr"))
        val success = assertIs<ArgsResult.Success>(result)
        assertEquals(OutputFormat.TEXT, success.args.format)
        assertEquals(listOf("file.bzr"), success.args.files)
    }

    @Test
    fun formatJson() {
        val result = parseArgs(arrayOf("--format", "json", "file.bzr"))
        val success = assertIs<ArgsResult.Success>(result)
        assertEquals(OutputFormat.JSON, success.args.format)
    }

    @Test
    fun formatCaseInsensitive() {
        val result = parseArgs(arrayOf("--format", "JSON", "file.bzr"))
        val success = assertIs<ArgsResult.Success>(result)
        assertEquals(OutputFormat.JSON, success.args.format)
    }

    @Test
    fun checkFlag() {
        val result = parseArgs(arrayOf("--check", "file.bzr"))
        val success = assertIs<ArgsResult.Success>(result)
        assertTrue(success.args.check)
    }

    @Test
    fun multiplePositionalFiles() {
        val result = parseArgs(arrayOf("a.bzr", "b.bzr"))
        val success = assertIs<ArgsResult.Success>(result)
        assertEquals(listOf("a.bzr", "b.bzr"), success.args.files)
    }

    @Test
    fun combinedFlags() {
        val result = parseArgs(arrayOf("--format", "json", "--check", "a.bzr", "b.bzr"))
        val success = assertIs<ArgsResult.Success>(result)
        assertEquals(OutputFormat.JSON, success.args.format)
        assertTrue(success.args.check)
        assertEquals(listOf("a.bzr", "b.bzr"), success.args.files)
    }

    @Test
    fun noArgsReturnsError() {
        val result = parseArgs(arrayOf())
        val error = assertIs<ArgsResult.Error>(result)
        assertEquals("No input files specified.", error.message)
    }

    @Test
    fun unknownOptionReturnsError() {
        val result = parseArgs(arrayOf("--unknown"))
        assertIs<ArgsResult.Error>(result)
    }

    @Test
    fun formatWithoutValueReturnsError() {
        val result = parseArgs(arrayOf("--format"))
        assertIs<ArgsResult.Error>(result)
    }

    @Test
    fun formatInvalidValueReturnsError() {
        val result = parseArgs(arrayOf("--format", "invalid", "file.bzr"))
        assertIs<ArgsResult.Error>(result)
    }

    @Test
    fun checkAloneNoFilesReturnsError() {
        val result = parseArgs(arrayOf("--check"))
        val error = assertIs<ArgsResult.Error>(result)
        assertEquals("No input files specified.", error.message)
    }

    @Test
    fun defaultsAreTextFormatAndNoCheck() {
        val result = parseArgs(arrayOf("file.bzr"))
        val success = assertIs<ArgsResult.Success>(result)
        assertEquals(OutputFormat.TEXT, success.args.format)
        assertEquals(false, success.args.check)
    }
}
