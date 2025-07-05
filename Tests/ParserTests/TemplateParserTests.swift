@preconcurrency import Antlr4
import Parser
import Testing

struct ParserTests {
    static let example = """
    data Person {
        firstName String
        lastName String
    }

    component Text {
        value String
    }

    Profile(person Person) component {
        Text(person.firstName)
    }
    """

    @Test
    func parser() throws {
        // Given
        let input = ANTLRInputStream(Self.example)
        let lexer = BazaarLexer(input)
        let ts = CommonTokenStream(lexer)
        let parser = try BazaarParser(ts)
        let tree = try parser.bzr()

        // When
        Visitor().visit(tree)
    }
}

class Visitor: BazaarParserBaseVisitor<Void> {
    override func visitData_declaration(_ ctx: BazaarParser.Data_declarationContext) {
        print("Data: \(ctx.getText())")
        super.visitData_declaration(ctx)
    }

    override func visitComponent_declaration(_ ctx: BazaarParser.Component_declarationContext) {
        print("Component: \(ctx.getText())")
        super.visitComponent_declaration(ctx)
    }

    override func visitTemplate_declaration(_ ctx: BazaarParser.Template_declarationContext) {
        print("Template: \(ctx.getText())")
        super.visitTemplate_declaration(ctx)
    }
}
