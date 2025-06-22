@preconcurrency import Antlr4
import Templates
import Testing

struct TemplatesParserTests {
    static let example = """
    data Text(
        value String
    )
    """

    @Test
    func parser() throws {
        // Given
        let input = ANTLRInputStream(Self.example)
        let lexer = TemplatesLexer(input)
        let ts = CommonTokenStream(lexer)
        let parser = try TemplatesParser(ts)
        let tree = try parser.schema()

        // When
        Visitor().visit(tree)
    }
}

class Visitor: TemplatesParserBaseVisitor<Void> {
    override func visitData(_ ctx: TemplatesParser.DataContext) {
        print(ctx.getText())

        super.visitData(ctx)
    }

    override func visitMember(_ ctx: TemplatesParser.MemberContext) {
        print(ctx.getText())
    }
}
