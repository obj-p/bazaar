@preconcurrency import Antlr4
import TemplateParser
import Testing

struct TemplateParserTests {
    static let example = """
    view Text
    """

    @Test
    func parser() throws {
        // Given
        let input = ANTLRInputStream(Self.example)
        let lexer = TemplateLexer(input)
        let ts = CommonTokenStream(lexer)
        let parser = try TemplateParser(ts)
        let tree = try parser.template()

        // When
        Visitor().visit(tree)
    }
}

class Visitor: TemplateParserBaseVisitor<Void> {
    override func visitView(_ ctx: TemplateParser.ViewContext) {
        print(ctx.getText())

        super.visitView(ctx)
    }
}
