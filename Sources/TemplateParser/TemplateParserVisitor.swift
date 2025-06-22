// Generated from TemplateParser.g4 by ANTLR 4.13.1
import Antlr4

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link TemplateParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
open class TemplateParserVisitor<T>: ParseTreeVisitor<T> {
    /**
      * Visit a parse tree produced by {@link TemplateParser#template}.
     - Parameters:
       - ctx: the parse tree
     - returns: the visitor result
      */
    open func visitTemplate(_: TemplateParser.TemplateContext) -> T {
        fatalError(#function + " must be overridden")
    }

    /**
      * Visit a parse tree produced by {@link TemplateParser#view}.
     - Parameters:
       - ctx: the parse tree
     - returns: the visitor result
      */
    open func visitView(_: TemplateParser.ViewContext) -> T {
        fatalError(#function + " must be overridden")
    }

    /**
      * Visit a parse tree produced by {@link TemplateParser#identifier}.
     - Parameters:
       - ctx: the parse tree
     - returns: the visitor result
      */
    open func visitIdentifier(_: TemplateParser.IdentifierContext) -> T {
        fatalError(#function + " must be overridden")
    }
}
