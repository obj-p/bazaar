@preconcurrency import Antlr4
import Parser
import Testing

struct ParserTests {
    static let example = """
    view Text
    """

    @Test
    func parser() throws {
        // Given
        let input = ANTLRInputStream(Self.example)
        let lexer = BazaarLexer(input)
        let ts = CommonTokenStream(lexer)
        let parser = try BazaarParser(ts)
        let tree = try parser.template()

        // When
        Visitor().visit(tree)
    }
}

class Visitor: BazaarParserBaseVisitor<Void> {
    override func visitView(_ ctx: BazaarParser.ViewContext) {
        print(ctx.getText())

        super.visitView(ctx)
    }
}
