package com.bazaar.parser

import com.bazaar.parser.antlr.BazaarLexer
import com.bazaar.parser.antlr.BazaarParser as AntlrParser
import com.bazaar.parser.ast.BazaarFile
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream

object BazaarParser {
    fun parse(source: String): BazaarFile {
        val lexer = BazaarLexer(CharStreams.fromString(source))
        val tokens = CommonTokenStream(lexer)
        val parser = AntlrParser(tokens)
        val tree = parser.bazaarFile()
        return BazaarAstVisitor().visitBazaarFile(tree)
    }
}
