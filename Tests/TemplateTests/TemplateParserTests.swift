@preconcurrency import Antlr4
import Template
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
        let lexer = TemplateLexer(input)
        let ts = CommonTokenStream(lexer)
        let parser = try TemplateParser(ts)
        let tree = try parser.schema()

        // When
        Visitor().visit(tree)
    }
}

class Visitor: TemplateParserBaseVisitor<Void> {
    override func visitData(_ ctx: TemplateParser.DataContext) {
        print(ctx.getText())

        super.visitData(ctx)
    }

    override func visitMember(_ ctx: TemplateParser.MemberContext) {
        print(ctx.getText())
    }
}
