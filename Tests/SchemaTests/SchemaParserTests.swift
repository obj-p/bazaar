@preconcurrency import Antlr4
import Schema
import Testing

struct TemplateParserTests {
    static let example = """
    data Text(
        value String
    )
    """

    @Test
    func parser() throws {
        // Given
        let input = ANTLRInputStream(Self.example)
        let lexer = SchemaLexer(input)
        let ts = CommonTokenStream(lexer)
        let parser = try SchemaParser(ts)
        let tree = try parser.schema()

        // When
        Visitor().visit(tree)
    }
}

class Visitor: SchemaParserBaseVisitor<Void> {
    override func visitData(_ ctx: SchemaParser.DataContext) {
        print(ctx.getText())

        super.visitData(ctx)
    }

    override func visitMember(_ ctx: SchemaParser.MemberContext) {
        print(ctx.getText())
    }
}
