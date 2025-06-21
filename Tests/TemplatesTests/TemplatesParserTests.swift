@preconcurrency import Antlr4
import Templates
import Testing

struct TemplatesParserTests {
    @Test
    func parser() throws {
        // Given
        let input = ANTLRInputStream("")
        let lexer = TemplatesLexer(input)
        let ts = BufferedTokenStream(lexer)

        // When
        let parser = try TemplatesParser(ts)

        // Then
        print(parser)
    }
}
